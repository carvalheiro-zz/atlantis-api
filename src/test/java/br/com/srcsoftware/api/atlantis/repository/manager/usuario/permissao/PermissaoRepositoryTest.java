package br.com.srcsoftware.api.atlantis.repository.manager.usuario.permissao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import br.com.srcsoftware.api.atlantis.entity.manager.usuario.permissao.PermissaoPO;
import br.com.srcsoftware.api.atlantis.entity.manager.usuario.tela.TelaPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroPO;

@DataJpaTest
@ActiveProfiles("test")
class PermissaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PermissaoRepository permissaoRepository;

    private TelaPO telaPersistida;

    @BeforeEach
    void setUp() {
        TelaPO tela = new TelaPO();
        tela.setNome("Dashboard Principal");
        tela.setQuery(Boolean.TRUE);
        telaPersistida = entityManager.persist(tela);

        PermissaoPO perm1 = new PermissaoPO();
        perm1.setAcao("CADASTRAR");
        perm1.setTela(telaPersistida);
        entityManager.persist(perm1);

        PermissaoPO perm2 = new PermissaoPO();
        perm2.setAcao("EDITAR");
        perm2.setTela(telaPersistida);
        entityManager.persist(perm2);

        PermissaoPO perm3 = new PermissaoPO();
        perm3.setAcao("EXCLUIR");
        perm3.setTela(telaPersistida);
        entityManager.persist(perm3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Deve buscar Permissao por ID com sucesso")
    void deveBuscarPorId() {
        PermissaoPO perm = new PermissaoPO();
        perm.setAcao("VISUALIZAR");
        perm.setTela(telaPersistida);
        PermissaoPO salva = entityManager.persistFlushFind(perm);

        Optional<PermissaoPO> encontrado = permissaoRepository.findById(salva.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getAcao()).isEqualTo("VISUALIZAR");
    }

    @Test
    @DisplayName("Deve buscar Permissao por Ação exata com sucesso")
    void deveBuscarPorAcaoExato() {
        Optional<PermissaoPO> encontrado = permissaoRepository.findByAcao("CADASTRAR");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getAcao()).isEqualTo("CADASTRAR");
    }

    @Test
    @DisplayName("Deve filtrar Permissao utilizando o Specification e o POJO de Filtro")
    void deveFiltrarPorFiltroDinamico() {
    	AbstractFiltroPO<PermissaoPO> filtro = new AbstractFiltroPO<PermissaoPO>(new PermissaoPO(), 0, 10);
        filtro.getObjeto().setAcao("CADASTRAR");

        List<PermissaoPO> resultados = permissaoRepository.findByFiltro(filtro);

        assertThat(resultados)
            .isNotNull()
            .hasSize(1)
            .extracting(PermissaoPO::getAcao)
            .contains("CADASTRAR");
    }

    @Test
    @DisplayName("Deve filtrar Permissao de forma paginada utilizando findByFiltroPaginado")
    void deveFiltrarPorFiltroPaginadoComSucesso() {
    	AbstractFiltroPO<PermissaoPO> filtro = new AbstractFiltroPO<PermissaoPO>(new PermissaoPO(), 0, 10);

        Pageable pageable = PageRequest.of(filtro.getPagina(), filtro.getTamanho());
        Page<PermissaoPO> paginaResultado = permissaoRepository.findByFiltroPaginado(filtro, pageable);

        assertThat(paginaResultado).isNotNull();
        assertThat(paginaResultado.getTotalElements()).isEqualTo(3);
        assertThat(paginaResultado.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Deve validar paginação dinâmica com diferentes tamanhos de registros (1, 2 e 3)")
    void deveTestarPaginacaoComDiferentesTamanhos() {
    	AbstractFiltroPO<PermissaoPO> filtro = new AbstractFiltroPO<PermissaoPO>(new PermissaoPO(), 0, 1);

        Pageable pageableTamanho1 = PageRequest.of(0, 1);
        Page<PermissaoPO> pagina1Tamanho1 = permissaoRepository.findByFiltroPaginado(filtro, pageableTamanho1);
        assertThat(pagina1Tamanho1.getTotalPages()).isEqualTo(3);
        assertThat(pagina1Tamanho1.getContent()).hasSize(1);

        Pageable pageableTamanho2 = PageRequest.of(0, 2);
        Page<PermissaoPO> pagina1Tamanho2 = permissaoRepository.findByFiltroPaginado(filtro, pageableTamanho2);
        assertThat(pagina1Tamanho2.getTotalPages()).isEqualTo(2);
        assertThat(pagina1Tamanho2.getContent()).hasSize(2);

        Pageable pageableSegundaPagina = PageRequest.of(1, 2);
        Page<PermissaoPO> pagina2Tamanho2 = permissaoRepository.findByFiltroPaginado(filtro, pageableSegundaPagina);
        assertThat(pagina2Tamanho2.getContent()).hasSize(1);

        Pageable pageableTamanho3 = PageRequest.of(0, 3);
        Page<PermissaoPO> paginaTamanho3 = permissaoRepository.findByFiltroPaginado(filtro, pageableTamanho3);
        assertThat(paginaTamanho3.getTotalPages()).isEqualTo(1);
        assertThat(paginaTamanho3.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Deve retornar vazio quando o filtro não encontrar registros correspondentes")
    void deveRetornarVazioQuandoFiltroNaoEncontrar() {
    	AbstractFiltroPO<PermissaoPO> filtro = new AbstractFiltroPO<PermissaoPO>(new PermissaoPO(), 0, 10);
        filtro.getObjeto().setAcao("ACAO_INEXISTENTE");

        List<PermissaoPO> resultados = permissaoRepository.findByFiltro(filtro);

        assertThat(resultados).isEmpty();
    }

    @Test
    @DisplayName("Deve realizar operações de CRUD (Salvar, Atualizar e Deletar)")
    void deveExecutarCrudComSucesso() {
        PermissaoPO nova = new PermissaoPO();
        nova.setAcao("IMPRIMIR");
        nova.setTela(telaPersistida);

        PermissaoPO salva = permissaoRepository.save(nova);
        assertThat(salva.getId()).isNotNull();

        Optional<PermissaoPO> consultado = permissaoRepository.findById(salva.getId());
        assertThat(consultado).isPresent();

        consultado.get().setAcao("EXPORTAR");
        PermissaoPO atualizado = permissaoRepository.save(consultado.get());
        assertThat(atualizado.getAcao()).isEqualTo("EXPORTAR");

        permissaoRepository.delete(atualizado);
        Optional<PermissaoPO> deletado = permissaoRepository.findById(salva.getId());
        assertThat(deletado).isNotPresent();
    }
}