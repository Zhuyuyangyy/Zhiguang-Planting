package com.laserdentalrobot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger配置 方便看接口文档
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Laser Dental Robot API")
                        .description("API for dental laser robot control system")
                        .version("v1.0"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("laser-dental-robot")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenApiCustomiser modelResponseCustomizer() {
        
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(operation -> {
                    ApiResponses responses = operation.getResponses();
                    responses.addApiResponse("400", makeErrResp("INVALID_FORMAT", "请求参数格式错误"));
                    responses.addApiResponse("401", makeErrResp("UNAUTHORIZED", "未授权访问"));
                    responses.addApiResponse("404", makeErrResp("FILE_NOT_FOUND", "资源不存在"));
                    responses.addApiResponse("429", makeErrResp("TOO_MANY_REQUESTS", "请求频率超过限制"));
                });
            });
        };
    }

    private ApiResponse makeErrResp(String errCode, String desc) {
        Schema<?> errSchema = new Schema<>();
        errSchema.$ref("#/components/schemas/Result");

        MediaType mt = new MediaType();
        mt.schema(errSchema);

        return new ApiResponse()
                .description(desc + "(" + errCode + ")")
                .content(new Content().addMediaType("application/json", mt));
    }
}
