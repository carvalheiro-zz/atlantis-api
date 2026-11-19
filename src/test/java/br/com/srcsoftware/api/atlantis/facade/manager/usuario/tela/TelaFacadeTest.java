package br.com.srcsoftware.api.atlantis.facade.manager.usuario.tela;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.srcsoftware.api.atlantis.dto.manager.usuario.tela.TelaDTO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.atlantis.service.manager.usuario.tela.TelaService;

@ExtendWith(MockitoExtension.class)
class TelaFacadeTest {

    @Mock
    private TelaService telaService;

    @InjectMocks
    private TelaFacade telaFacade;

    @Test
    @DisplayName("Deve salvar Tela com sucesso via Facade")
    void deveSalvarComSucesso() throws ApplicationException {
        TelaDTO dto = new TelaDTO();
        dto.setNome("Dashboard Principal");

        TelaDTO dtoSalvo = new TelaDTO();
        dtoSalvo.setId("1");
        dtoSalvo.setNome("Dashboard Principal");

        when(telaService.salvar(dto)).thenReturn(dtoSalvo);

        TelaDTO resultado = telaFacade.salvar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo("1");
        assertThat(resultado.getNome()).isEqualTo("Dashboard Principal");
        verify(telaService, times(1)).salvar(dto);
    }

    @Test
    @DisplayName("Deve atualizar Tela com sucesso via Facade")
    void deveAtualizarComSucesso() throws ApplicationException {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Atualizado");

        when(telaService.atualizar(dto)).thenReturn(dto);

        TelaDTO resultado = telaFacade.atualizar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Dashboard Atualizado");
        verify(telaService, times(1)).atualizar(dto);
    }

    @Test
    @DisplayName("Deve deletar Tela com sucesso via Facade")
    void deveDeletarComSucesso() throws ApplicationException {
        doNothing().when(telaService).deletar("1");

        telaFacade.deletar("1");

        verify(telaService, times(1)).deletar("1");
    }

    @Test
    @DisplayName("Deve buscar Tela por ID com sucesso via Facade")
    void deveBuscarPorIdComSucesso() throws ApplicationException {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(telaService.buscarPorId("1")).thenReturn(Optional.of(dto));

        Optional<TelaDTO> resultado = telaFacade.buscarPorId("1");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo("1");
        verify(telaService, times(1)).buscarPorId("1");
    }

    @Test
    @DisplayName("Deve buscar Tela por Nome com sucesso via Facade")
    void deveBuscarPorNomeComSucesso() throws ApplicationException {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(telaService.buscarPorNome("Dashboard Principal")).thenReturn(Optional.of(dto));

        Optional<TelaDTO> resultado = telaFacade.buscarPorNome("Dashboard Principal");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Dashboard Principal");
        verify(telaService, times(1)).buscarPorNome("Dashboard Principal");
    }

    @Test
    @DisplayName("Deve listar todas as Telas com sucesso via Facade")
    void deveListarTodasComSucesso() throws ApplicationException {
        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(telaService.listarTodas()).thenReturn(List.of(dto));

        List<TelaDTO> resultado = telaFacade.listarTodas();

        assertThat(resultado).isNotNull().hasSize(1);
        verify(telaService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Deve buscar Telas por filtro paginado com sucesso via Facade")
    void deveBuscarPorFiltroPaginadoComSucesso() throws ApplicationException {
    	AbstractFiltroDTO<TelaDTO> filtro = new AbstractFiltroDTO<TelaDTO>();
        TelaDTO dto = new TelaDTO();
        dto.setNome("Dashboard");

        Page<TelaDTO> paginaDTO = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(telaService.buscarPorFiltroPaginado(filtro)).thenReturn(paginaDTO);

        Page<TelaDTO> resultado = telaFacade.buscarPorFiltroPaginado(filtro);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).hasSize(1);
        verify(telaService, times(1)).buscarPorFiltroPaginado(filtro);
    }

    @Test
    @DisplayName("Deve propagar ApplicationException quando ocorrer erro no Service")
    void devePropagarExcecaoDoService() throws ApplicationException {
        TelaDTO dto = new TelaDTO();
        when(telaService.salvar(dto)).thenThrow(new ApplicationException("Erro ao salvar"));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            telaFacade.salvar(dto);
        });

        assertThat(ex.getMessage()).isEqualTo("Erro ao salvar");
        verify(telaService, times(1)).salvar(dto);
    }
}