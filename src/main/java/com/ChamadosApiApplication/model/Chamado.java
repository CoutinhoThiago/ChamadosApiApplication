package com.ChamadosApiApplication.model;

public record Chamado(
        Long id,
        String titulo,
        String solicitante,
        String setor,
        Prioridade prioridade,
        StatusChamado status
) {
}
