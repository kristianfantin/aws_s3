package com.kristian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.kristian"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(generateApiInfo())
                .tags(
                        new Tag("interfaceController", "Rest Services")
                );
    }


    private ApiInfo generateApiInfo()
    {
        return new ApiInfoBuilder()
                .title("Search Files in Directory (S3)")
                .description("A better experience in order to test your API")
                .version("Version 1.0")
                .contact(new Contact("Developer: Kristian", "", "kristianfantin@gmail.com"))
                .build();
    }

}
