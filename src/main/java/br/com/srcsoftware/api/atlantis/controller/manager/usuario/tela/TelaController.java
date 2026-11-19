package br.com.srcsoftware.api.atlantis.controller.manager.usuario.tela;

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

import br.com.srcsoftware.api.atlantis.dto.manager.usuario.tela.TelaDTO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.facade.manager.usuario.tela.TelaFacade;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.controller.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/telas")
@RequiredArgsConstructor
public class TelaController {

    private final TelaFacade telaFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<TelaDTO>> salvar(@RequestBody TelaDTO dto, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para salvar TelaDTO");
        TelaDTO telaSalva = telaFacade.salvar(dto);
        
        ApiResponse<TelaDTO> response = ApiResponse.success(
            HttpStatus.CREATED.value(),
            "Tela cadastrada com sucesso!",
            telaSalva,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<TelaDTO>> atualizar(@RequestBody TelaDTO dto, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para atualizar TelaDTO");
        TelaDTO telaAtualizada = telaFacade.atualizar(dto);
        
        ApiResponse<TelaDTO> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Tela atualizada com sucesso!",
            telaAtualizada,
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable String id, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para excluir tela com ID: {}", id);
        telaFacade.deletar(id);
        
        ApiResponse<Void> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Tela excluída com sucesso!",
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TelaDTO>> buscarPorId(@PathVariable String id, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para buscar tela por ID: {}", id);
        return telaFacade.buscarPorId(id)
            .map(tela -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Tela encontrada com sucesso!", tela, request.getRequestURI())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.success(HttpStatus.NOT_FOUND.value(), "Nenhuma tela encontrada com o ID informado.", null, request.getRequestURI())));
    }

    @GetMapping("/por-nome")
    public ResponseEntity<ApiResponse<TelaDTO>> buscarPorNome(@RequestParam String nome, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para buscar tela por nome: {}", nome);
        return telaFacade.buscarPorNome(nome)
            .map(tela -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Tela encontrada com sucesso!", tela, request.getRequestURI())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.success(HttpStatus.NOT_FOUND.value(), "Nenhuma tela encontrada com o nome informado.", null, request.getRequestURI())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TelaDTO>>> listarTodas(HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para listar todas as telas");
        List<TelaDTO> telas = telaFacade.listarTodas();
        
        ApiResponse<List<TelaDTO>> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Listagem de telas realizada com sucesso!",
            telas,
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<ApiResponse<Page<TelaDTO>>> buscarPorFiltroPaginado(@RequestBody AbstractFiltroDTO<TelaDTO> filtro, HttpServletRequest request) throws ApplicationException {
        log.info("Controller: Recebida requisição para filtrar telas com paginação");
        Page<TelaDTO> telasPaginadas = telaFacade.buscarPorFiltroPaginado(filtro);
        
        ApiResponse<Page<TelaDTO>> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Filtragem de telas realizada com sucesso!",
            telasPaginadas,
            request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }
}