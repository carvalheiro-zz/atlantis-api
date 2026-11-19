package br.com.srcsoftware.api.atlantis.service.manager.usuario.tela;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import br.com.srcsoftware.api.atlantis.entity.manager.usuario.tela.TelaPO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.repository.manager.usuario.tela.TelaRepository;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.atlantis.utilidades.ModelFactory;

@ExtendWith(MockitoExtension.class)
class TelaServiceTest {

    @Mock
    private TelaRepository repository;

    @Mock
    private ModelFactory<TelaPO, TelaDTO> modelFactory;

    @InjectMocks
    private TelaService telaService;

    @Test
    @DisplayName("Deve buscar Tela por ID com sucesso")
    void deveBuscarPorId() throws Exception {
        TelaPO po = new TelaPO();
        po.setId(1L);
        po.setNome("Dashboard Principal");

        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(repository.findById(1L)).thenReturn(Optional.of(po));
        when(modelFactory.getDTO(TelaDTO.class, po)).thenReturn(dto);

        Optional<TelaDTO> encontrado = telaService.buscarPorId("1");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Dashboard Principal");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar Tela por Nome exato com sucesso")
    void deveBuscarPorNomeExato() throws Exception {
        TelaPO po = new TelaPO();
        po.setId(1L);
        po.setNome("Dashboard Principal");

        TelaDTO dto = new TelaDTO();
        dto.setId("1");
        dto.setNome("Dashboard Principal");

        when(repository.findByNome("Dashboard Principal")).thenReturn(Optional.of(po));
        when(modelFactory.getDTO(TelaDTO.class, po)).thenReturn(dto);

        Optional<TelaDTO> encontrado = telaService.buscarPorNome("Dashboard Principal");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Dashboard Principal");
        verify(repository, times(1)).findByNome("Dashboard Principal");
    }

    @Test
    @DisplayName("Deve filtrar Tela de forma paginada utilizando buscarPorFiltroPaginado")
    void deveFiltrarPorFiltroPaginadoComSucesso() throws Exception {
        TelaDTO filtroDtoObj = new TelaDTO();
        filtroDtoObj.setQuery("true");
        AbstractFiltroDTO<TelaDTO> filtroDTO = new AbstractFiltroDTO<TelaDTO>(filtroDtoObj, 0, 10);

        TelaPO poFiltro = new TelaPO();
        poFiltro.setQuery(Boolean.TRUE);

        TelaPO poResultado = new TelaPO();
        poResultado.setId(1L);
        poResultado.setNome("Dashboard Principal");
        poResultado.setQuery(Boolean.TRUE);

        TelaDTO dtoResultado = new TelaDTO();
        dtoResultado.setId("1");
        dtoResultado.setNome("Dashboard Principal");

        when(modelFactory.getPO(eq(TelaPO.class), any())).thenReturn(poFiltro);
        
        Page<TelaPO> paginaPO = new PageImpl<>(List.of(poResultado), PageRequest.of(0, 10), 1);
        when(repository.findByFiltroPaginado(any(), any())).thenReturn(paginaPO);
        when(modelFactory.getDTO(TelaDTO.class, poResultado)).thenReturn(dtoResultado);

        Page<TelaDTO> paginaResultado = telaService.buscarPorFiltroPaginado(filtroDTO);

        assertThat(paginaResultado).isNotNull();
        assertThat(paginaResultado.getTotalElements()).isEqualTo(1);
        assertThat(paginaResultado.getContent())
            .isNotEmpty()
            .extracting(TelaDTO::getNome)
            .contains("Dashboard Principal");
    }

    @Test
    @DisplayName("Deve validar paginação dinâmica com diferentes tamanhos de registros (1, 2 e 3)")
    void deveTestarPaginacaoComDiferentesTamanhos() throws Exception {
    	AbstractFiltroDTO<TelaDTO> filtroDTO = new AbstractFiltroDTO<TelaDTO>(new TelaDTO(), 0, 1);

        TelaPO po1 = new TelaPO();
        po1.setId(1L);
        po1.setNome("Dashboard Principal");

        TelaPO po2 = new TelaPO();
        po2.setId(2L);
        po2.setNome("Relatório de Vendas");

        TelaDTO dto1 = new TelaDTO();
        dto1.setId("1");
        dto1.setNome("Dashboard Principal");

        when(modelFactory.getPO(eq(TelaPO.class), any())).thenReturn(new TelaPO());

        // Simulação para tamanho 1 (Total de 3 páginas simuladas no repositório)
        Page<TelaPO> paginaTamanho1 = new PageImpl<>(List.of(po1), PageRequest.of(0, 1), 3);
        when(repository.findByFiltroPaginado(any(), eq(PageRequest.of(0, 1)))).thenReturn(paginaTamanho1);
        when(modelFactory.getDTO(TelaDTO.class, po1)).thenReturn(dto1);

        Page<TelaDTO> resultado1 = telaService.buscarPorFiltroPaginado(filtroDTO);
        assertThat(resultado1.getTotalPages()).isEqualTo(3);
        assertThat(resultado1.getContent()).hasSize(1);

        // Simulação para tamanho 2
        filtroDTO.setTamanho(2);
        Page<TelaPO> paginaTamanho2 = new PageImpl<>(List.of(po1, po2), PageRequest.of(0, 2), 3);
        when(repository.findByFiltroPaginado(any(), eq(PageRequest.of(0, 2)))).thenReturn(paginaTamanho2);
        when(modelFactory.getDTO(TelaDTO.class, po2)).thenReturn(new TelaDTO());

        Page<TelaDTO> resultado2 = telaService.buscarPorFiltroPaginado(filtroDTO);
        assertThat(resultado2.getTotalPages()).isEqualTo(2);
        assertThat(resultado2.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Deve retornar vazio quando o filtro não encontrar registros correspondentes")
    void deveRetornarVazioQuandoFiltroNaoEncontrar() throws Exception {
        TelaDTO filtroDtoObj = new TelaDTO();
        filtroDtoObj.setNome("Tela Inexistente");
        AbstractFiltroDTO<TelaDTO> filtroDTO = new AbstractFiltroDTO<TelaDTO>(filtroDtoObj, 0, 10);

        when(modelFactory.getPO(eq(TelaPO.class), any())).thenReturn(new TelaPO());
        
        Page<TelaPO> paginaVazia = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(repository.findByFiltroPaginado(any(), any())).thenReturn(paginaVazia);

        Page<TelaDTO> resultado = telaService.buscarPorFiltroPaginado(filtroDTO);

        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve realizar operações de CRUD (Salvar, Atualizar e Deletar)")
    void deveExecutarCrudComSucesso() throws Exception {
        // --- SALVAR ---
        TelaDTO dtoNova = new TelaDTO();
        dtoNova.setNome("Relatório Financeiro");
        dtoNova.setQuery("false");

        TelaPO poNova = new TelaPO();
        poNova.setNome("Relatório Financeiro");

        TelaPO poSalva = new TelaPO();
        poSalva.setId(1L);
        poSalva.setNome("Relatório Financeiro");

        TelaDTO dtoSalva = new TelaDTO();
        dtoSalva.setId("1");
        dtoSalva.setNome("Relatório Financeiro");

        when(modelFactory.getPO(TelaPO.class, dtoNova)).thenReturn(poNova);
        doNothing().when(modelFactory).validarNotNull(poNova);
        when(repository.save(poNova)).thenReturn(poSalva);
        when(modelFactory.getDTO(TelaDTO.class, poSalva)).thenReturn(dtoSalva);

        TelaDTO resultadoSalvar = telaService.salvar(dtoNova);
        assertThat(resultadoSalvar.getId()).isNotNull();

        // --- ATUALIZAR ---
        dtoSalva.setNome("Relatório Financeiro Atualizado");
        when(modelFactory.getPO(TelaPO.class, dtoSalva)).thenReturn(poSalva);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(poSalva)).thenReturn(poSalva);
        when(modelFactory.getDTO(TelaDTO.class, poSalva)).thenReturn(dtoSalva);

        TelaDTO resultadoAtualizar = telaService.atualizar(dtoSalva);
        assertThat(resultadoAtualizar.getNome()).isEqualTo("Relatório Financeiro Atualizado");

        // --- DELETAR ---
        when(repository.findById(1L)).thenReturn(Optional.of(poSalva));
        doNothing().when(repository).delete(poSalva);

        telaService.deletar("1");
        verify(repository, times(1)).delete(poSalva);
    }

    @Test
    @DisplayName("Deve lançar ApplicationException ao tentar atualizar com ID nulo ou em branco")
    void deveLancarExcecaoAoAtualizarComIdInvalido() {
        TelaDTO dto = new TelaDTO();
        dto.setNome("Teste");

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            telaService.atualizar(dto);
        });

        assertThat(ex.getMessage()).contains("O ID da tela é obrigatório para atualização!");
    }
}