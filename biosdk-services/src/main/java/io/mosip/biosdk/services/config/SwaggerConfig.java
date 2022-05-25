package io.mosip.biosdk.services.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;


import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Value("${application.env.local:false}")
    private Boolean localEnv;

    @Value("${swagger.base-url:#{null}}")
    private String swaggerBaseUrl;

    @Value("${server.port:9092}")
    private int serverPort;

    String proto = "http";
    String host = "localhost";
    int port = -1;
    String hostWithPort = "localhost:9092";

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Autowired
    private OpenApiProperties openApiProperties;

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }

    @Bean
    public OpenAPI openApi() {
        OpenAPI api = new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title(openApiProperties.getInfo().getTitle())
                        .version(openApiProperties.getInfo().getVersion())
                        .description(openApiProperties.getInfo().getDescription())
                        .license(new License()
                                .name(openApiProperties.getInfo().getLicense().getName())
                                .url(openApiProperties.getInfo().getLicense().getUrl())));

        openApiProperties.getService().getServers().forEach(server -> {
            api.addServersItem(new Server().description(server.getDescription()).url(server.getUrl()));
        });
        logger.info("swagger open api bean is ready");
        return api;
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder().group(openApiProperties.getGroup().getName())
                .pathsToMatch(openApiProperties.getGroup().getPaths().stream().toArray(String[]::new))
                .build();
    }

    private Set<String> protocols() {
        Set<String> protocols = new HashSet<>();
        protocols.add(proto);
        return protocols;
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
