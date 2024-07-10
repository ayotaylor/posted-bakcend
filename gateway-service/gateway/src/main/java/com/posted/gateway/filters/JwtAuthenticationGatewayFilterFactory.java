package com.posted.gateway.filters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.posted.gateway.util.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationGatewayFilterFactory(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!isAuthMandatory(request, config)) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String token = authHeader.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
            }

            Claims claims = jwtUtil.extractAllClaims(token);
            exchange.getRequest().mutate()
                .header("X-auth-user-id", String.valueOf(claims.get("userId")))
                .build();

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isAuthMandatory(ServerHttpRequest request, Config config) {

        return !config.isAllowPublicEndpoints() && !config.getPublicEndpoints().contains(request.getPath().value());
    }

    public static class Config {
        private String tokenHeader = "Authorization";
        private String tokenPrefix = "Bearer ";
        private boolean allowPublicEndpoints = true;
        private String publicEndpoints = "/api/auth/login,/api/auth/register,/actuator/info,/actuator/health";

        public String getTokenHeader() {
            return tokenHeader;
        }

        public void setTokenHeader(String tokenHeader) {
            this.tokenHeader = tokenHeader;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        public void setTokenPrefix(String tokenPrefix) {
            this.tokenPrefix = tokenPrefix;
        }

        public boolean isAllowPublicEndpoints() {
            return allowPublicEndpoints;
        }

        public void setAllowPublicEndpoints(boolean allowPublicEndpoints) {
            this.allowPublicEndpoints = allowPublicEndpoints;
        }

        public List<String> getPublicEndpoints() {
            return publicEndpoints != null ? Arrays.asList(publicEndpoints.split(",")) : Collections.emptyList();
        }

        public void setPublicEndpoints(String publicEndpoints) {
            this.publicEndpoints = publicEndpoints;
        }
    }
}
