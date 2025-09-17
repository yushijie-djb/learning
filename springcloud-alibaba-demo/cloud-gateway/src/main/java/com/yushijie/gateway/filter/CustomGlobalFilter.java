package com.yushijie.gateway.filter;

import org.springframework.stereotype.Component;

/**
 * @ClassName CustomGlobalFilter
 * @Description 自定义全局过滤器
 * @Author tigerkin
 * @Date 2022/3/14 17:03
 */
@Component
public class CustomGlobalFilter /*implements GlobalFilter, Ordered*/ {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
////    private static Joiner joiner = Joiner.on("");
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        log.info("========> 请求进入全局过滤器");
//        ServerHttpResponse originalResponse = exchange.getResponse();
//        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
//        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {
//            @Override
//            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                if (!(body instanceof Flux)) {
//                    return super.writeWith(body);
//                }
//
//                HttpStatus statusCode = getStatusCode();
//                if (statusCode.equals(HttpStatus.OK)) {
//                    // return readAndWrite(body); // 读取响应结果，可以对响应结果做二次处理再返回
//                    return super.writeWith(body); // 直接返回响应结果
//                } else { // 对非正常状态码的请求，封装统一响应信息，不弹出错误页面
//                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
//                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
//                        ApiResult result;
//                        if (statusCode.equals(HttpStatus.NOT_FOUND)) {
//                            result = new ApiResult(HttpStatus.NOT_FOUND, "访问服务资源不存在");
//                        } else {
//                            result = new ApiResult(statusCode, statusCode.name());
//                        }
//                        String resultJson = JSON.toJSONString(result);
//
//                        byte[] uppedContent = new String(resultJson.getBytes(), Charset.forName("UTF-8")).getBytes();
//                        originalResponse.setStatusCode(HttpStatus.OK); // 设置状态码
//                        originalResponse.getHeaders().setContentLength(uppedContent.length); // 设置内容长度
//                        originalResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON); // 设置内容类型
//                        return bufferFactory.wrap(uppedContent);
//                    }));
//                }
//            }
//
//            /**
//             * 读取和写入Response响应内容
//             * @param body
//             * @return
//             */
//            private Mono<Void> readAndWrite(Publisher<? extends DataBuffer> body) {
//                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
//                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
//                    List<String> list = Lists.newArrayList();
//                    dataBuffers.forEach(dataBuffer -> {
//                        try {
//                            byte[] content = new byte[dataBuffer.readableByteCount()];
//                            dataBuffer.read(content);
//                            DataBufferUtils.release(dataBuffer);
//                            list.add(new String(content, "UTF-8"));
//                        } catch (Exception e) {
//                            log.info("加载Response字节流异常，失败原因：{}", Throwables.getStackTraceAsString(e));
//                        }
//                    });
//                    String responseData = joiner.join(list);
//                    System.out.println("responseData：" + responseData);
//
//                    byte[] uppedContent = new String(responseData.getBytes(), Charset.forName("UTF-8")).getBytes();
//                    originalResponse.getHeaders().setContentLength(uppedContent.length);
//                    originalResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//                    return bufferFactory.wrap(uppedContent);
//                }));
//            }
//
//            @Override
//            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
//                return writeWith(Flux.from(body).flatMapSequential(p -> p));
//            }
//        };
//        return chain.filter(exchange.mutate().response(response).build());
//    }
//
//    /**
//     * 定义全局过滤器顺序
//     *
//     * @return
//     */
//    @Override
//    public int getOrder() {
//        return -1;
//    }
}
