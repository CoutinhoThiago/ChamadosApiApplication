
package com.ChamadosApiApplication.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chamadosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Chamados")
                        .description(
                                "API REST para cadastro, consulta, " +
                                        "pesquisa e gerenciamento de chamados."
                        )
                        .version("1.0.0"));
    }
}
