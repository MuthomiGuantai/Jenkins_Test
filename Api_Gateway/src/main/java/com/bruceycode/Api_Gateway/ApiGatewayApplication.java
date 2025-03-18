package com.bruceycode.Api_Gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bruceycode.Api_Gateway.Util.LoadBalancerGatewayFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
	private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

	private final LoadBalancerClient loadBalancerClient;

	// Hardcoded JWT token for demonstration (replace with dynamic retrieval in production)
	private static final String JWT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9BRE1JTiIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzQyMjg2ODE4LCJleHAiOjE3NDMxNTA4MTh9.R5nILb4sbhE67fkNpn8HU7SU0ZdWE39dZ-UTwXIThp0";

	public ApiGatewayApplication(LoadBalancerClient loadBalancerClient) {
		this.loadBalancerClient = loadBalancerClient;
		logger.info("ApiGatewayApplication initialized with LoadBalancerClient");
	}

	public static void main(String[] args) {
		logger.info("Starting ApiGatewayApplication");
		SpringApplication.run(ApiGatewayApplication.class, args);
		logger.info("ApiGatewayApplication started successfully");
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		logger.info("Configuring custom RouteLocator");
		RouteLocator routes = builder.routes()
				.route("medical_service", r -> {
					logger.debug("Configuring route for medical_service with path /medical/**");
					return r.path("/medical/**")
							.filters(f -> f
									.rewritePath("/medical/(?<remaining>.*)", "/${remaining}")
									.filter(new LoadBalancerGatewayFilter(loadBalancerClient))
									.addRequestHeader("Authorization", JWT_TOKEN)) // Add JWT header
							.uri("lb://MEDICAL_SERVICE");
				})
				.route("patient_service", r -> {
					logger.debug("Configuring route for patient_service with path /patient/**");
					return r.path("/patient/**")
							.filters(f -> f
									.rewritePath("/patient/(?<remaining>.*)", "/${remaining}")
									.filter(new LoadBalancerGatewayFilter(loadBalancerClient))
									.addRequestHeader("Authorization", JWT_TOKEN)) // Add JWT header
							.uri("lb://PATIENT_SERVICE");
				})
				.route("department_service", r -> {
					logger.debug("Configuring route for department_service with path /department/**");
					return r.path("/department/**")
							.filters(f -> f
									.rewritePath("/department/(?<remaining>.*)", "/${remaining}")
									.filter(new LoadBalancerGatewayFilter(loadBalancerClient))
									.addRequestHeader("Authorization", JWT_TOKEN)) // Add JWT header
							.uri("lb://DEPARTMENT_SERVICE");
				})
				.build();

		// Count routes asynchronously and log the result
		Flux<Route> routeFlux = routes.getRoutes();
		routeFlux.count().subscribe(count ->
				logger.info("Custom RouteLocator configured with {} routes", count)
		);

		return routes;
	}
}