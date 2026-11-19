package br.com.srcsoftware.api.atlantis.controller.usuario.tela;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.srcsoftware.api.atlantis.controller.usuario.tela.TelaController;
import br.com.srcsoftware.api.atlantis.dto.usuario.tela.TelaDTO;
import br.com.srcsoftware.api.atlantis.facade.usuario.tela.TelaFacade;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroDTO;

@WebMvcTest(TelaController.class)
class TelaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TelaFacade telaFacade;

    @Test
    @DisplayName("Deve salvar Tela com sucesso e retornar status 201 CREATED")
    void deveSalvarComSucesso() throws Exception {
        TelaDTO dto = new TelaDTO();
        dto.setNome("Dashboard Principal");

        TelaDTO dtoSalvo = new TelaDTO();
        dtoSalvo.setId("1");
        dtoSalvo.setNome("Dashboard Principal");

        when(telaFacade.salvar(any(TelaDTO.class))).thenReturn(dtoSalvo);

        mockMvc.perform(post("/api/v1/telas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Tela cadastrada com sucesso!"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.nome").value("Dashboard Principal"));

        verify(telaFacade, times(1)).salvar(any(TelaDTO.class));
    }

    @Test
    @DisplayName("Deve atualizar Tela com sucesso e retornar status 200 OK")
    void deveAtualizarComSucesso() throws Exception {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Atualizado");

        when(telaFacade.atualizar(any(TelaDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/telas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Tela atualizada com sucesso!"))
                .andExpect(jsonPath("$.data.nome").value("Dashboard Atualizado"));

        verify(telaFacade, times(1)).atualizar(any(TelaDTO.class));
    }

    @Test
    @DisplayName("Deve excluir Tela com sucesso e retornar status 200 OK")
    void deveDeletarComSucesso() throws Exception {
        doNothing().when(telaFacade).deletar("1");

        mockMvc.perform(delete("/api/v1/telas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Tela excluída com sucesso!"));

        verify(telaFacade, times(1)).deletar("1");
    }

    @Test
    @DisplayName("Deve buscar Tela por ID com sucesso e retornar status 200 OK")
    void deveBuscarPorIdComSucesso() throws Exception {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(telaFacade.buscarPorId("1")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/telas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value("1"));

        verify(telaFacade, times(1)).buscarPorId("1");
    }

    @Test
    @DisplayName("Deve retornar status 404 NOT_FOUND quando buscar por ID inexistente")
    void deveRetornarNotFoundQuandoIdInexistente() throws Exception {
        when(telaFacade.buscarPorId("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/telas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(telaFacade, times(1)).buscarPorId("99");
    }

    @Test
    @DisplayName("Deve buscar Tela por Nome com sucesso e retornar status 200 OK")
    void deveBuscarPorNomeComSucesso() throws Exception {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(telaFacade.buscarPorNome("Dashboard Principal")).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/telas/por-nome")
                .param("nome", "Dashboard Principal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.nome").value("Dashboard Principal"));

        verify(telaFacade, times(1)).buscarPorNome("Dashboard Principal");
    }

    @Test
    @DisplayName("Deve listar todas as Telas com sucesso e retornar status 200 OK")
    void deveListarTodasComSucesso() throws Exception {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(telaFacade.listarTodas()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/telas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].id").value("1"));

        verify(telaFacade, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Deve filtrar Telas de forma paginada com sucesso e retornar status 200 OK")
    void deveFiltrarPaginadoComSucesso() throws Exception {
    	AbstractFiltroDTO<TelaDTO> filtro = new AbstractFiltroDTO<TelaDTO>();
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        var pagina = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(telaFacade.buscarPorFiltroPaginado(any(AbstractFiltroDTO.class))).thenReturn(pagina);

        mockMvc.perform(post("/api/v1/telas/filtrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].nome").value("Dashboard Principal"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(telaFacade, times(1)).buscarPorFiltroPaginado(any(AbstractFiltroDTO.class));
    }
}