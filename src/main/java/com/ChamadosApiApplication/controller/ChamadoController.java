package com.ChamadosApiApplication.controller;

import com.ChamadosApiApplication.model.Chamado;
import com.ChamadosApiApplication.model.Prioridade;
import com.ChamadosApiApplication.model.StatusChamado;
import com.ChamadosApiApplication.dto.NovoChamadoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Locale;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final List<Chamado> chamados = new ArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(10);
    public ChamadoController() {
        carregarChamadosIniciais();
    }

    private void carregarChamadosIniciais() {
        chamados.add(new Chamado(1L, "Computador não liga", "Ana Souza", "Financeiro",
                Prioridade.ALTA, StatusChamado.ABERTO));
        chamados.add(new Chamado(2L, "Impressora sem conexão", "Carlos Mendes", "RH",
                Prioridade.MEDIA, StatusChamado.EM_ANDAMENTO));
        chamados.add(new Chamado(3L, "Acesso ao sistema bloqueado", "Juliana Lima", "TI",
                Prioridade.ALTA, StatusChamado.EM_ANDAMENTO));
        chamados.add(new Chamado(4L, "Internet lenta no setor", "Pedro Alves", "Comercial",
                Prioridade.MEDIA, StatusChamado.ABERTO));
        chamados.add(new Chamado(5L, "Instalação do pacote Office", "Mariana Costa", "Administrativo",
                Prioridade.BAIXA, StatusChamado.RESOLVIDO));
        chamados.add(new Chamado(6L, "Monitor com tela piscando", "Roberto Dias", "Financeiro",
                Prioridade.MEDIA, StatusChamado.RESOLVIDO));
        chamados.add(new Chamado(7L, "Falha no acesso ao e-mail", "Fernanda Rocha", "RH",
                Prioridade.ALTA, StatusChamado.RESOLVIDO));
        chamados.add(new Chamado(8L, "Teclado não funciona", "Lucas Oliveira", "TI",
                Prioridade.BAIXA, StatusChamado.ABERTO));
        chamados.add(new Chamado(9L, "Sistema financeiro indisponível", "Beatriz Santos", "Financeiro",
                Prioridade.ALTA, StatusChamado.ABERTO));
        chamados.add(new Chamado(10L, "Configuração de impressora", "Diego Martins", "Comercial",
                Prioridade.BAIXA, StatusChamado.EM_ANDAMENTO));
    }

    @Operation(
            summary = "Listar todos os chamados",
            description = "Retorna a lista completa de chamados cadastrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de chamados retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Lista de chamados",
                            value = """
                        [
                            {
                                "id": 1,
                                "titulo": "Computador não liga",
                                "solicitante": "Ana Souza",
                                "setor": "Financeiro",
                                "prioridade": "ALTA",
                                "status": "ABERTO"
                            },
                            {
                                "id": 2,
                                "titulo": "Impressora sem conexão",
                                "solicitante": "Carlos Mendes",
                                "setor": "RH",
                                "prioridade": "MEDIA",
                                "status": "EM_ANDAMENTO"
                            }
                        ]
                        """
                    )
            )
    )
    @GetMapping
    public ResponseEntity<?> listarTodos(
            @RequestParam(required = false) String setor,
            @RequestParam(required = false) String prioridade,
            @RequestParam(required = false) String texto
    ) {
        Prioridade prioridadeFiltro = null;

        if (prioridade != null && !prioridade.isBlank()) {
            prioridadeFiltro = converterPrioridade(prioridade);

            if (prioridadeFiltro == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("erro", "Prioridade inválida. Use BAIXA, MEDIA ou ALTA.")
                );
            }
        }

        final Prioridade prioridadeFinal = prioridadeFiltro;

        List<Chamado> resultado = chamados.stream()
                .filter(chamado ->
                        setor == null || setor.isBlank()
                                || chamado.setor().equalsIgnoreCase(setor.trim())
                )
                .filter(chamado ->
                        prioridadeFinal == null
                                || chamado.prioridade() == prioridadeFinal
                )
                .filter(chamado ->
                        texto == null || texto.isBlank()
                                || chamado.titulo().toLowerCase(Locale.ROOT)
                                .contains(texto.trim().toLowerCase(Locale.ROOT))
                )
                .toList();

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar chamado por ID",
            description = "Busca um chamado específico utilizando seu identificador."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Chamado encontrado",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                        {
                            "id": 1,
                            "titulo": "Computador não liga",
                            "solicitante": "Ana Souza",
                            "setor": "Financeiro",
                            "prioridade": "ALTA",
                            "status": "ABERTO"
                        }
                        """
                    )
            )
    )
    @ApiResponse(responseCode = "404", description = "Chamado não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return chamados.stream()
                .filter(chamado -> chamado.id().equals(id))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Chamado com ID " + id + " não encontrado.")));
    }

    @Operation(
            summary = "Filtrar chamados por setor",
            description = "Retorna os chamados pertencentes ao setor informado."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Chamados filtrados por setor",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                        [
                            {
                                "id": 1,
                                "titulo": "Computador não liga",
                                "solicitante": "Ana Souza",
                                "setor": "Financeiro",
                                "prioridade": "ALTA",
                                "status": "ABERTO"
                            },
                            {
                                "id": 6,
                                "titulo": "Monitor com tela piscando",
                                "solicitante": "Roberto Dias",
                                "setor": "Financeiro",
                                "prioridade": "MEDIA",
                                "status": "RESOLVIDO"
                            }
                        ]
                        """
                    )
            )
    )
    @GetMapping("/setor")
    public List<Chamado> filtrarPorSetor(@RequestParam String nome) {
        String termo = nome.trim();
        return chamados.stream()
                .filter(chamado -> chamado.setor().equalsIgnoreCase(termo))
                .toList();
    }

    @Operation(
            summary = "Filtrar chamados por prioridade",
            description = "Retorna os chamados que possuem a prioridade informada."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Chamados filtrados por prioridade",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                        [
                            {
                                "id": 1,
                                "titulo": "Computador não liga",
                                "solicitante": "Ana Souza",
                                "setor": "Financeiro",
                                "prioridade": "ALTA",
                                "status": "ABERTO"
                            },
                            {
                                "id": 3,
                                "titulo": "Acesso ao sistema bloqueado",
                                "solicitante": "Juliana Lima",
                                "setor": "TI",
                                "prioridade": "ALTA",
                                "status": "EM_ANDAMENTO"
                            }
                        ]
                        """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "Prioridade inválida")
    @GetMapping("/prioridade")
    public ResponseEntity<?> filtrarPorPrioridade(@RequestParam String nivel) {
        Prioridade prioridade = converterPrioridade(nivel);

        if (prioridade == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", "Prioridade inválida. Use BAIXA, MEDIA ou ALTA."));
        }

        List<Chamado> resultado = chamados.stream()
                .filter(chamado -> chamado.prioridade() == prioridade)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar chamados pelo título",
            description = "Pesquisa chamados cujo título contenha o texto informado."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Resultado da pesquisa",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                        [
                            {
                                "id": 2,
                                "titulo": "Impressora sem conexão",
                                "solicitante": "Carlos Mendes",
                                "setor": "RH",
                                "prioridade": "MEDIA",
                                "status": "EM_ANDAMENTO"
                            },
                            {
                                "id": 10,
                                "titulo": "Configuração de impressora",
                                "solicitante": "Diego Martins",
                                "setor": "Comercial",
                                "prioridade": "BAIXA",
                                "status": "EM_ANDAMENTO"
                            }
                        ]
                        """
                    )
            )
    )
    @GetMapping("/busca")
    public List<Chamado> buscarPorTitulo(@RequestParam String texto) {
        String termo = texto.trim().toLowerCase(Locale.ROOT);
        return chamados.stream()
                .filter(chamado -> chamado.titulo().toLowerCase(Locale.ROOT).contains(termo))
                .toList();
    }

    @Operation(
            summary = "Listar chamados críticos",
            description = "Retorna chamados de prioridade ALTA que ainda não foram resolvidos."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de chamados críticos",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                        [
                            {
                                "id": 1,
                                "titulo": "Computador não liga",
                                "solicitante": "Ana Souza",
                                "setor": "Financeiro",
                                "prioridade": "ALTA",
                                "status": "ABERTO"
                            },
                            {
                                "id": 3,
                                "titulo": "Acesso ao sistema bloqueado",
                                "solicitante": "Juliana Lima",
                                "setor": "TI",
                                "prioridade": "ALTA",
                                "status": "EM_ANDAMENTO"
                            },
                            {
                                "id": 9,
                                "titulo": "Sistema financeiro indisponível",
                                "solicitante": "Beatriz Santos",
                                "setor": "Financeiro",
                                "prioridade": "ALTA",
                                "status": "ABERTO"
                            }
                        ]
                        """
                    )
            )
    )
    @GetMapping("/criticos")
    public List<Chamado> listarCriticos() {
        return chamados.stream()
                .filter(chamado -> chamado.prioridade() == Prioridade.ALTA
                        && chamado.status() != StatusChamado.RESOLVIDO)
                .toList();
    }

    @Operation(
            summary = "Obter resumo dos chamados",
            description = "Retorna a quantidade de chamados abertos, em andamento, resolvidos e o total."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Resumo retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                        {
                            "abertos": 4,
                            "em_andamento": 3,
                            "resolvidos": 3,
                            "total": 10
                        }
                        """
                    )
            )
    )
    @GetMapping("/resumo")
    public Map<String, Long> obterResumo() {
        long abertos = contarPorStatus(StatusChamado.ABERTO);
        long emAndamento = contarPorStatus(StatusChamado.EM_ANDAMENTO);
        long resolvidos = contarPorStatus(StatusChamado.RESOLVIDO);

        return Map.of(
                "abertos", abertos,
                "em_andamento", emAndamento,
                "resolvidos", resolvidos,
                "total", (long) chamados.size()
        );
    }

    @Operation(
            summary = "Cadastrar novo chamado",
            description = "Cadastra um chamado. Caso o status não seja informado, será ABERTO."
    )
    @PostMapping
    public ResponseEntity<?> cadastrar(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do chamado que será cadastrado",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Exemplo de chamado",
                                            summary = "Cadastro de chamado de alta prioridade",
                                            value = """
                    {
                        "titulo": "Computador não liga",
                        "solicitante": "Ana Souza",
                        "setor": "Financeiro",
                        "prioridade": "ALTA",
                        "status": "ABERTO"
                    }
                    """
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody
            NovoChamadoRequest requisicao
    ) {
        if (requisicao.titulo() == null || requisicao.titulo().isBlank()) {
            return erroDeValidacao("O título é obrigatório.");
        }

        if (requisicao.solicitante() == null || requisicao.solicitante().isBlank()) {
            return erroDeValidacao("O solicitante é obrigatório.");
        }

        if (requisicao.setor() == null || requisicao.setor().isBlank()) {
            return erroDeValidacao("O setor é obrigatório.");
        }

        Prioridade prioridade = converterPrioridade(requisicao.prioridade());

        if (prioridade == null) {
            return erroDeValidacao("Prioridade inválida. Use BAIXA, MEDIA ou ALTA.");
        }

        StatusChamado status = StatusChamado.ABERTO;

        if (requisicao.status() != null && !requisicao.status().isBlank()) {
            status = converterStatus(requisicao.status());

            if (status == null) {
                return erroDeValidacao(
                        "Status inválido. Use ABERTO, EM_ANDAMENTO ou RESOLVIDO."
                );
            }
        }

        Chamado novoChamado = new Chamado(
                proximoId.incrementAndGet(),
                requisicao.titulo().trim(),
                requisicao.solicitante().trim(),
                requisicao.setor().trim(),
                prioridade,
                status
        );

        chamados.add(novoChamado);

        return ResponseEntity
                .created(URI.create("/chamados/" + novoChamado.id()))
                .body(novoChamado);
    }

    private long contarPorStatus(StatusChamado status) {
        return chamados.stream()
                .filter(chamado -> chamado.status() == status)
                .count();
    }

    private Prioridade converterPrioridade(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Prioridade.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private StatusChamado converterStatus(String valor) {
        try {
            return StatusChamado.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private ResponseEntity<Map<String, String>> erroDeValidacao(String mensagem) {
        return ResponseEntity.badRequest().body(Map.of("erro", mensagem));
    }
}
