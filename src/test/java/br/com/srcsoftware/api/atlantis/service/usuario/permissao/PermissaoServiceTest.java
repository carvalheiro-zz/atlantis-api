package br.com.srcsoftware.api.atlantis.service.usuario.permissao;

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

import br.com.srcsoftware.api.atlantis.dto.usuario.permissao.PermissaoDTO;
import br.com.srcsoftware.api.atlantis.entity.usuario.permissao.PermissaoPO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.atlantis.repository.usuario.permissao.PermissaoRepository;
import br.com.srcsoftware.api.atlantis.service.usuario.permissao.PermissaoService;
import br.com.srcsoftware.api.atlantis.utilidades.ModelFactory;

@ExtendWith(MockitoExtension.class)
class PermissaoServiceTest {

    @Mock
    private PermissaoRepository repository;

    @Mock
    private ModelFactory<PermissaoPO, PermissaoDTO> modelFactory;

    @InjectMocks
    private PermissaoService permissaoService;

    @Test
    @DisplayName("Deve buscar Permissao por ID com sucesso")
    void deveBuscarPorId() throws Exception {
        PermissaoPO po = new PermissaoPO();
        po.setId(1L);
        po.setAcao("CADASTRAR");

        PermissaoDTO dto = new PermissaoDTO();
        dto.setId("1");
        dto.setAcao("CADASTRAR");

        when(repository.findById(1L)).thenReturn(Optional.of(po));
        when(modelFactory.getDTO(PermissaoDTO.class, po)).thenReturn(dto);

        Optional<PermissaoDTO> encontrado = permissaoService.buscarPorId("1");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getAcao()).isEqualTo("CADASTRAR");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar Permissao por Ação exata com sucesso")
    void deveBuscarPorAcaoExato() throws Exception {
        PermissaoPO po = new PermissaoPO();
        po.setId(1L);
        po.setAcao("CADASTRAR");

        PermissaoDTO dto = new PermissaoDTO();
        dto.setId("1");
        dto.setAcao("CADASTRAR");

        when(repository.findByAcao("CADASTRAR")).thenReturn(Optional.of(po));
        when(modelFactory.getDTO(PermissaoDTO.class, po)).thenReturn(dto);

        Optional<PermissaoDTO> encontrado = permissaoService.buscarPorAcao("CADASTRAR");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getAcao()).isEqualTo("CADASTRAR");
        verify(repository, times(1)).findByAcao("CADASTRAR");
    }

    @Test
    @DisplayName("Deve filtrar Permissao de forma paginada utilizando buscarPorFiltroPaginado")
    void deveFiltrarPorFiltroPaginadoComSucesso() throws Exception {
        PermissaoDTO filtroDtoObj = new PermissaoDTO();
        filtroDtoObj.setAcao("CADASTRAR");
        AbstractFiltroDTO<PermissaoDTO> filtroDTO = new AbstractFiltroDTO<PermissaoDTO>(filtroDtoObj, 0, 10);
        
        PermissaoPO poFiltro = new PermissaoPO();
        poFiltro.setAcao("CADASTRAR");

        PermissaoPO poResultado = new PermissaoPO();
        poResultado.setId(1L);
        poResultado.setAcao("CADASTRAR");

        PermissaoDTO dtoResultado = new PermissaoDTO();
        dtoResultado.setId("1");
        dtoResultado.setAcao("CADASTRAR");

        when(modelFactory.getPO(eq(PermissaoPO.class), any())).thenReturn(poFiltro);
        
        Page<PermissaoPO> paginaPO = new PageImpl<>(List.of(poResultado), PageRequest.of(0, 10), 1);
        when(repository.findByFiltroPaginado(any(), any())).thenReturn(paginaPO);
        when(modelFactory.getDTO(PermissaoDTO.class, poResultado)).thenReturn(dtoResultado);

        Page<PermissaoDTO> paginaResultado = permissaoService.buscarPorFiltroPaginado(filtroDTO);

        assertThat(paginaResultado).isNotNull();
        assertThat(paginaResultado.getTotalElements()).isEqualTo(1);
        assertThat(paginaResultado.getContent())
            .isNotEmpty()
            .extracting(PermissaoDTO::getAcao)
            .contains("CADASTRAR");
    }

    @Test
    @DisplayName("Deve validar paginação dinâmica com diferentes tamanhos de registros (1, 2 e 3)")
    void deveTestarPaginacaoComDiferentesTamanhos() throws Exception {
    	AbstractFiltroDTO<PermissaoDTO> filtroDTO = new AbstractFiltroDTO<PermissaoDTO>(new PermissaoDTO(), 0, 1);

        PermissaoPO po1 = new PermissaoPO();
        po1.setId(1L);
        po1.setAcao("CADASTRAR");

        PermissaoPO po2 = new PermissaoPO();
        po2.setId(2L);
        po2.setAcao("EDITAR");

        PermissaoDTO dto1 = new PermissaoDTO();
        dto1.setId("1");
        dto1.setAcao("CADASTRAR");

        when(modelFactory.getPO(eq(PermissaoPO.class), any())).thenReturn(new PermissaoPO());

        Page<PermissaoPO> paginaTamanho1 = new PageImpl<>(List.of(po1), PageRequest.of(0, 1), 3);
        when(repository.findByFiltroPaginado(any(), eq(PageRequest.of(0, 1)))).thenReturn(paginaTamanho1);
        when(modelFactory.getDTO(PermissaoDTO.class, po1)).thenReturn(dto1);

        Page<PermissaoDTO> resultado1 = permissaoService.buscarPorFiltroPaginado(filtroDTO);
        assertThat(resultado1.getTotalPages()).isEqualTo(3);
        assertThat(resultado1.getContent()).hasSize(1);

        filtroDTO.setTamanho(2);
        Page<PermissaoPO> paginaTamanho2 = new PageImpl<>(List.of(po1, po2), PageRequest.of(0, 2), 3);
        when(repository.findByFiltroPaginado(any(), eq(PageRequest.of(0, 2)))).thenReturn(paginaTamanho2);
        when(modelFactory.getDTO(PermissaoDTO.class, po2)).thenReturn(new PermissaoDTO());

        Page<PermissaoDTO> resultado2 = permissaoService.buscarPorFiltroPaginado(filtroDTO);
        assertThat(resultado2.getTotalPages()).isEqualTo(2);
        assertThat(resultado2.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Deve retornar vazio quando o filtro não encontrar registros correspondentes")
    void deveRetornarVazioQuandoFiltroNaoEncontrar() throws Exception {
        PermissaoDTO filtroDtoObj = new PermissaoDTO();
        filtroDtoObj.setAcao("ACAO_INEXISTENTE");
        AbstractFiltroDTO<PermissaoDTO> filtroDTO = new AbstractFiltroDTO<PermissaoDTO>(filtroDtoObj, 0, 10);

        when(modelFactory.getPO(eq(PermissaoPO.class), any())).thenReturn(new PermissaoPO());
        
        Page<PermissaoPO> paginaVazia = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(repository.findByFiltroPaginado(any(), any())).thenReturn(paginaVazia);

        Page<PermissaoDTO> resultado = permissaoService.buscarPorFiltroPaginado(filtroDTO);

        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve realizar operações de CRUD (Salvar, Atualizar e Deletar)")
    void deveExecutarCrudComSucesso() throws Exception {
        PermissaoDTO dtoNova = new PermissaoDTO();
        dtoNova.setAcao("IMPRIMIR");

        PermissaoPO poNova = new PermissaoPO();
        poNova.setAcao("IMPRIMIR");

        PermissaoPO poSalva = new PermissaoPO();
        poSalva.setId(1L);
        poSalva.setAcao("IMPRIMIR");

        PermissaoDTO dtoSalva = new PermissaoDTO();
        dtoSalva.setId("1");
        dtoSalva.setAcao("IMPRIMIR");

        when(modelFactory.getPO(PermissaoPO.class, dtoNova)).thenReturn(poNova);
        doNothing().when(modelFactory).validarNotNull(poNova);
        when(repository.save(poNova)).thenReturn(poSalva);
        when(modelFactory.getDTO(PermissaoDTO.class, poSalva)).thenReturn(dtoSalva);

        PermissaoDTO resultadoSalvar = permissaoService.salvar(dtoNova);
        assertThat(resultadoSalvar.getId()).isNotNull();

        dtoSalva.setAcao("EXPORTAR");
        when(modelFactory.getPO(PermissaoPO.class, dtoSalva)).thenReturn(poSalva);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(poSalva)).thenReturn(poSalva);
        when(modelFactory.getDTO(PermissaoDTO.class, poSalva)).thenReturn(dtoSalva);

        PermissaoDTO resultadoAtualizar = permissaoService.atualizar(dtoSalva);
        assertThat(resultadoAtualizar.getAcao()).isEqualTo("EXPORTAR");

        when(repository.findById(1L)).thenReturn(Optional.of(poSalva));
        doNothing().when(repository).delete(poSalva);

        permissaoService.deletar("1");
        verify(repository, times(1)).delete(poSalva);
    }

    @Test
    @DisplayName("Deve lançar ApplicationException ao tentar atualizar com ID nulo ou em branco")
    void deveLancarExcecaoAoAtualizarComIdInvalido() {
        PermissaoDTO dto = new PermissaoDTO();
        dto.setAcao("CADASTRAR");

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            permissaoService.atualizar(dto);
        });

        assertThat(ex.getMessage()).contains("O ID da permissao é obrigatório para atualização!");
    }
}