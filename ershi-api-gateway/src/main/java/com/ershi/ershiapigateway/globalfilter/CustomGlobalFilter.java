package com.ershi.ershiapigateway.globalfilter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.ershi.common.model.entity.InterfaceInfo;
import com.ershi.common.model.entity.User;
import com.ershi.common.service.InnerInterfaceInfoService;
import com.ershi.common.service.InnerUserService;
import com.ershi.ershiapiclientsdk.utils.SignUtils;
import com.ershi.ershiapigateway.globalmodel.TotalInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;


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

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @Resource
    private TotalInterfaceInfo totalInterfaceInfo;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 记录请求的日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识: " + request.getId());
        String url = request.getPath().value();
        log.info("请求路径: " + url);
        String method = request.getMethod().toString();
        log.info("请求方法: " + method);
        log.info("请求参数: " + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址: " + sourceAddress);

        ServerHttpResponse response = exchange.getResponse();

        //2. 黑白名单
        // todo 黑白名单完善
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handlerNoAuth(response);
        }

        //3. 用户鉴权（判断 ak / sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String body = headers.getFirst("body");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");

        // 提交时间和当前时间不超过 5 分钟
        Long currentTime = System.currentTimeMillis() / 1000;
        Long FIVE_MINUTES = 60 * 5L;
        if (currentTime - Long.parseLong(timestamp) > FIVE_MINUTES) {
            return handlerNoAuth(response);
        }

        String sign = headers.getFirst("sign");
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            // todo 继续抽取公共模块、例如 ErrorCode
            log.error("getInvokeUser error = ", e);
        }
        if (invokeUser == null) {
            return handlerNoAuth(response);
        }
        // 获取对应 sk
        String secreteKey = invokeUser.getSecreteKey();
        HashMap<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("body", body);
        map.put("nonce", nonce);
        map.put("timestamp", timestamp);
        String serverSign = SignUtils.getSign(map, secreteKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handlerNoAuth(response);
        }

        //4. 请求的模拟接口是否存在
        // todo 从数据中查询模拟接口是否存在，以及请求方法是否匹配 (还可以检验请求参数)
//        innerInterfaceInfoService.getInvokeInterfaceInfo()
        //5. 请求参数校验
        // todo 有参校验

        //6. 请求转发、调用模拟接口
        //7. 响应日志
        //8. 调用成功，统计调用次数 + 1
        // todo 检查该用户是否还有调用次数
        return handleResponse(exchange, chain);
    }


    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return {@link Mono}<{@link Void}>
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                // todo 异常处理，不能暴露服务器内部信息
                return chain.filter(exchange);//降级处理返回数据
            }
            // 装饰、增强能力
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                // 等调用完转发的接口后才会执行
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                        // 往返回值里面写数据 (有返回说明调用成功)
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            // todo 8. 调用成功，统计调用次数

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
}
