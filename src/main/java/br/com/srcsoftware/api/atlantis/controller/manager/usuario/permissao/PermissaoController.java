package br.com.srcsoftware.api.atlantis.controller.manager.usuario.permissao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.srcsoftware.api.atlantis.dto.manager.usuario.permissao.PermissaoDTO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.facade.manager.usuario.permissao.PermissaoFacade;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.controller.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/permissoes")
@RequiredArgsConstructor
public class PermissaoController {

    private final PermissaoFacade permissaoFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissaoDTO>> salvar(@RequestBody PermissaoDTO dto, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para salvar PermissaoDTO");
        PermissaoDTO permissaoSalva = permissaoFacade.salvar(dto);
        
        ApiResponse<PermissaoDTO> response = ApiResponse.success(
            HttpStatus.CREATED.value(),
            "Permissao cadastrada com sucesso!",
            permissaoSalva,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PermissaoDTO>> atualizar(@RequestBody PermissaoDTO dto, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para atualizar PermissaoDTO");
        PermissaoDTO permissaoAtualizada = permissaoFacade.atualizar(dto);
        
        ApiResponse<PermissaoDTO> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Permissao atualizada com sucesso!",
            permissaoAtualizada,
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable String id, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para excluir permissao com ID: {}", id);
        permissaoFacade.deletar(id);
        
        ApiResponse<Void> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Permissao excluída com sucesso!",
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissaoDTO>> buscarPorId(@PathVariable String id, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para buscar permissao por ID: {}", id);
        return permissaoFacade.buscarPorId(id)
            .map(permissao -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Permissao encontrada com sucesso!", permissao, request.getRequestURI())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.success(HttpStatus.NOT_FOUND.value(), "Nenhuma permissao encontrada com o ID informado.", null, request.getRequestURI())));
    }

    @GetMapping("/por-acao")
    public ResponseEntity<ApiResponse<PermissaoDTO>> buscarPorNome(@RequestParam String acao, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para buscar permissao por nome: {}", acao);
        return permissaoFacade.buscarPorNome(acao)
            .map(permissao -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Permissao encontrada com sucesso!", permissao, request.getRequestURI())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.success(HttpStatus.NOT_FOUND.value(), "Nenhuma permissao encontrada com o nome informado.", null, request.getRequestURI())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissaoDTO>>> listarTodas(HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para listar todas as permissoes");
        List<PermissaoDTO> permissoes = permissaoFacade.listarTodas();
        
        ApiResponse<List<PermissaoDTO>> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Listagem de permissoes realizada com sucesso!",
            permissoes,
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<ApiResponse<Page<PermissaoDTO>>> buscarPorFiltroPaginado(@RequestBody AbstractFiltroDTO<PermissaoDTO> filtro, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para filtrar permissoes com paginação");
        Page<PermissaoDTO> permissoesPaginadas = permissaoFacade.buscarPorFiltroPaginado(filtro);
        
        ApiResponse<Page<PermissaoDTO>> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Filtragem de permissoes realizada com sucesso!",
            permissoesPaginadas,
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }
}