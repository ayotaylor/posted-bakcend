package com.posted.gateway.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

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