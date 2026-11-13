
package com.ChamadosApiApplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para cadastrar um chamado")
public record NovoChamadoRequest(

        @Schema(description = "Título do chamado", example = "Computador não liga")
        String titulo,

        @Schema(description = "Nome do solicitante", example = "Ana Souza")
        String solicitante,

        @Schema(description = "Setor do solicitante", example = "Financeiro")
        String setor,

        @Schema(description = "Prioridade: BAIXA, MEDIA ou ALTA", example = "ALTA")
        String prioridade,

        @Schema(
                description = "Status: ABERTO, EM_ANDAMENTO ou RESOLVIDO",
                example = "ABERTO",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String status

) {
}
