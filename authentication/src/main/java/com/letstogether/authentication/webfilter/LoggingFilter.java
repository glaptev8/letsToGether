//package com.letstogether.authentication.webfilter;
//
//import java.nio.charset.StandardCharsets;
//
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Component
//public class LoggingFilter implements WebFilter {
//
//  @Override
//  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//    return DataBufferUtils.join(exchange.getRequest().getBody())
//      .flatMap(dataBuffer -> {
//        byte[] bytes = new byte[dataBuffer.readableByteCount()];
//        dataBuffer.read(bytes);
//        DataBufferUtils.release(dataBuffer);
//
//        String body = new String(bytes, StandardCharsets.UTF_8);
//        log.info("Request body: {}", body);
//
//        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
//          @Override
//          public Flux<DataBuffer> getBody() {
//            return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
//          }
//        };
//
//        return chain.filter(exchange.mutate().request(decorator).build());
//      });
//  }
//}
