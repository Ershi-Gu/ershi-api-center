package com.ershi.ershiapigateway.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.common.model.entity.User;
import com.ershi.common.service.InnerInterfaceInfoService;
import com.ershi.common.service.InnerUserService;
import com.ershi.common.service.InnerUserToInterfaceInfoService;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.ErrorCode;
import com.ershi.common.utils.SignUtils;
import com.ershi.ershiapigateway.globalmodel.TotalInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;


/**
 * 全局过滤器
 *
 * @author Ershi
 * @date 2024/05/03
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    /**
     * ip 白名单
     */
    // todo 读取外部资源，编写ip白名单
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    /**
     * 链接头
     */
    private static final String HTTP_PROTOCOL = "http://";

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserToInterfaceInfoService innerUserToInterfaceInfoService;

    @Resource
    private TotalInterfaceInfo totalInterfaceInfo;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();

        //1. 记录请求的日志
        ServerHttpRequest request = exchange.getRequest();
        String requestId = request.getId();
        String url = request.getPath().value();
        String method = request.getMethod().toString();
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求唯一标识: " + requestId);
        log.info("请求路径: " + url);
        log.info("请求方法: " + method);
        log.info("请求来源地址: " + sourceAddress);

//        //2. 黑白名单
//        // todo 黑白名单完善
//        if (!IP_WHITE_LIST.contains(sourceAddress)) {
//            return handlerNoAuth(response);
//        }

        //3. 用户鉴权（判断 ak / sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String body = headers.getFirst("body");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");

        long currentTime = System.currentTimeMillis() / 1000;
        long FIVE_MINUTES = 60 * 5L;
        if (timestamp == null || "".equals(timestamp) || currentTime - Long.parseLong(timestamp) > FIVE_MINUTES) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }

        String sign = headers.getFirst("sign");
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请求用户异常");
        }
        if (invokeUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        String secreteKey = invokeUser.getSecreteKey();
        HashMap<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("body", body);
        map.put("nonce", nonce);
        map.put("timestamp", timestamp);
        String serverSign = SignUtils.getSign(map, secreteKey);
        if (sign == null || !sign.equals(serverSign)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限");
        }

        //4. 校验请求的模拟接口是否存在
        Map<String, InterfaceInfo> interfaceInfoMap = totalInterfaceInfo.getInterfaceInfoMap();
        String id = headers.getFirst("id");
        InterfaceInfo targetInterfaceInfo = interfaceInfoMap.get(id);
        if (targetInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }


        //5. 请求参数校验
        // todo 有参校验


        //6. 动态路由转发至目标接口
        String targetHost = targetInterfaceInfo.getHost();
        log.info("请求目标主机: " + targetHost);

        URI newUrl = null;
        try {
            newUrl = new URI(HTTP_PROTOCOL + targetHost + url);
        } catch (URISyntaxException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "URI 构建异常");
        }

        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        if (route == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Route newRoute = Route.async().asyncPredicate(route.getPredicate())
                .filters(route.getFilters()).id(route.getId()).order(route.getOrder()).uri(newUrl).build();
        exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, newRoute);

        // 7. 检查用户是否还有调用次数
        if (!innerUserToInterfaceInfoService.checkInvokeCount(invokeUser, targetInterfaceInfo)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用次数不足");        }

        // 8. 放行请求
        return handleResponse(invokeUser, targetInterfaceInfo, exchange, chain);
    }


    /**
     * 异步处理响应
     *
     * @param exchange
     * @param chain
     * @return {@link Mono}<{@link Void}>
     */
    public Mono<Void> handleResponse(User invokeUser, InterfaceInfo targetInterfaceInfo,
                                     ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            // 装饰、增强能力
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                // 异步方法-等调用完转发的接口后才会执行
                @Override
                // 对返回 Response 进行二次处理
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    //7. 响应日志
                    //8. 调用成功，统计调用次数 + 1
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                        // 往返回值里面写数据 (有返回说明调用成功)
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                            // 统计调用次数
                            innerUserToInterfaceInfoService.invokeCount(invokeUser.getId(), targetInterfaceInfo.getId());

                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer buff = dataBufferFactory.join(dataBuffers);
                            // content - 接口返回值
                            byte[] content = new byte[buff.readableByteCount()];
                            buff.read(content);
                            DataBufferUtils.release(buff);//释放掉内存

                            // 构建返回日志(非 json)
                            String data = new String(content, StandardCharsets.UTF_8);
                            log.info("响应结果: " + data);

                            //排除Excel导出，不是application/json不打印。若请求是上传图片则在最上面判断。
                            MediaType contentType = originalResponse.getHeaders().getContentType();
                            if (!MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                                return bufferFactory.wrap(content);
                            }

                            // 构建返回日志
                            String joinData = new String(content);
                            String result = modifyBody(joinData);
                            List<Object> rspArgs = new ArrayList<>();
                            rspArgs.add(originalResponse.getStatusCode().value());
                            rspArgs.add(exchange.getRequest().getURI());
                            rspArgs.add(result);
                            log.info("<-- {} {}\n{}", rspArgs.toArray());

                            getDelegate().getHeaders().setContentLength(result.getBytes().length);
                            return bufferFactory.wrap(result.getBytes());
                        }));
                    } else {
                        log.error("<-- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());

        } catch (Exception e) {
            log.error("网关响应处理异常: " + e);
            return chain.filter(exchange);
        }
    }


    //返回统一的JSON日期数据 2024-02-23 11:00， null转空字符串
    private String modifyBody(String jsonStr) {
        JSONObject json = JSON.parseObject(jsonStr, Feature.AllowISO8601DateFormat);
        JSONObject.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
        return JSONObject.toJSONString(json, (ValueFilter) (object, name, value) -> value == null ? "" : value, SerializerFeature.WriteDateUseDateFormat);
    }


    @Override
    public int getOrder() {
        return -1;
    }


    /**
     * 无权限处理返回
     *
     * @param response
     * @return {@link Mono}<{@link Void}>
     */
    public Mono<Void> handlerNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }


    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

}
