package works.weave.socks.cart.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Value("${version}")
    private String version;

    @Value("${Swagger.enable}")
    private boolean enable;

    @Value("${spring.application.name}")
    private String title;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("works.weave.socks.cart.controllers"))
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Hou ShengMing", "https://github.com/pivotone", "18865290907@163.com");

        return new ApiInfo(
                title,
                title + " api description",
                version,
                "http://localhost:8081",
                contact,
                "Apache2.0",
                "http://www.apache.org/license/LICENSE-2.0",
                new ArrayList<>()
        );
    }
}
