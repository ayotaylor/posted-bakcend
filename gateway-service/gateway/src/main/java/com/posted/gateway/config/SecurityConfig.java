package com.posted.gateway.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.posted.gateway.filters.JwtAuthenticationGatewayFilterFactory;
import com.posted.gateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return routeDefinitionLocator.getRouteDefinitions()
            .collectList()
            .flatMap(routeDefinitions -> {
                http.csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.disable())
                    .authorizeExchange(ex -> {
                        List<String> paths = routeDefinitions.stream()
                            .flatMap(routeDefinition -> routeDefinition.getPredicates().stream())
                            .filter(predicateDefinition -> "Path".equals(predicateDefinition.getName()))
                            .flatMap(predicateDefinition -> {
                                String path = predicateDefinition.getArgs().get("_genkey_0");
                                return path != null ? List.of(path.split(",")).stream() : Stream.empty();
                            })
                            .collect(Collectors.toList());

                        for (String path : paths) {
                            ex.pathMatchers(path.trim()).permitAll();
                        }
                        ex.pathMatchers("/actuator/health", "/actuator/info").permitAll();
                        ex.anyExchange().authenticated();
                    });

                return Mono.just(http.build());
            })
            .block();
    }

    @Bean
    public JwtAuthenticationGatewayFilterFactory jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationGatewayFilterFactory(jwtUtil);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }
}
