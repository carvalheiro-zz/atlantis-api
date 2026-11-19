package br.com.srcsoftware.api.atlantis.repository.usuario.tela;

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

import br.com.srcsoftware.api.atlantis.entity.usuario.tela.TelaPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroPO;
import br.com.srcsoftware.api.atlantis.repository.usuario.tela.TelaRepository;

@DataJpaTest
@ActiveProfiles("test")
class TelaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TelaRepository telaRepository;

    @BeforeEach
    void setUp() {
        // Cadastra massa inicial para testes de paginação
        TelaPO tela1 = new TelaPO();
        tela1.setNome("Dashboard Principal");
        tela1.setQuery(Boolean.TRUE);
        entityManager.persist(tela1);

        TelaPO tela2 = new TelaPO();
        tela2.setNome("Relatório de Vendas");
        tela2.setQuery(Boolean.FALSE);
        entityManager.persist(tela2);

        TelaPO tela3 = new TelaPO();
        tela3.setNome("Cadastro de Usuários");
        tela3.setQuery(Boolean.TRUE);
        entityManager.persist(tela3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Deve buscar Tela por ID com sucesso")
    void deveBuscarPorId() {
        TelaPO tela = new TelaPO();
        tela.setNome("Teste ID");
        TelaPO salva = entityManager.persistFlushFind(tela);

        Optional<TelaPO> encontrado = telaRepository.findById(salva.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Teste ID");
    }

    @Test
    @DisplayName("Deve buscar Tela por Nome exato com sucesso")
    void deveBuscarPorNomeExato() {
        Optional<TelaPO> encontrado = telaRepository.findByNome("Dashboard Principal");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Dashboard Principal");
    }

    @Test
    @DisplayName("Deve filtrar Tela utilizando o Specification e o POJO de Filtro")
    void deveFiltrarPorFiltroDinamico() {
    	AbstractFiltroPO<TelaPO> filtro = new AbstractFiltroPO<TelaPO>(new TelaPO(), 0, 10);        
    	filtro.getObjeto().setQuery(Boolean.TRUE);

        List<TelaPO> resultados = telaRepository.findByFiltro(filtro);

        assertThat(resultados)
            .isNotNull()
            .hasSize(2); // Dashboard Principal e Cadastro de Usuários
    }

    @Test
    @DisplayName("Deve filtrar Tela de forma paginada utilizando findByFiltroPaginado")
    void deveFiltrarPorFiltroPaginadoComSucesso() {
    	AbstractFiltroPO<TelaPO> filtro = new AbstractFiltroPO<TelaPO>(new TelaPO(), 0, 10);

        Pageable pageable = PageRequest.of(filtro.getPagina(), filtro.getTamanho());
        Page<TelaPO> paginaResultado = telaRepository.findByFiltroPaginado(filtro, pageable);

        assertThat(paginaResultado).isNotNull();
        assertThat(paginaResultado.getTotalElements()).isEqualTo(3);
        assertThat(paginaResultado.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Deve validar paginação dinâmica com diferentes tamanhos de registros (1, 2 e 3)")
    void deveTestarPaginacaoComDiferentesTamanhos() {
        AbstractFiltroPO<TelaPO> filtro = new AbstractFiltroPO<TelaPO>(new TelaPO(), 0, 1);

        // Testando tamanho 1 por página (espera-se 3 páginas no total para 3 registros)
        Pageable pageableTamanho1 = PageRequest.of(0, 1);
        Page<TelaPO> pagina1Tamanho1 = telaRepository.findByFiltroPaginado(filtro, pageableTamanho1);
        assertThat(pagina1Tamanho1.getTotalPages()).isEqualTo(3);
        assertThat(pagina1Tamanho1.getContent()).hasSize(1);

        // Testando tamanho 2 por página (espera-se 2 páginas no total)
        Pageable pageableTamanho2 = PageRequest.of(0, 2);
        Page<TelaPO> pagina1Tamanho2 = telaRepository.findByFiltroPaginado(filtro, pageableTamanho2);
        assertThat(pagina1Tamanho2.getTotalPages()).isEqualTo(2);
        assertThat(pagina1Tamanho2.getContent()).hasSize(2);

        // Testando página 1 com tamanho 2 (segunda página, deve conter o último registro)
        Pageable pageableSegundaPagina = PageRequest.of(1, 2);
        Page<TelaPO> pagina2Tamanho2 = telaRepository.findByFiltroPaginado(filtro, pageableSegundaPagina);
        assertThat(pagina2Tamanho2.getContent()).hasSize(1);

        // Testando tamanho 3 por página (espera-se 1 página contendo todos)
        Pageable pageableTamanho3 = PageRequest.of(0, 3);
        Page<TelaPO> paginaTamanho3 = telaRepository.findByFiltroPaginado(filtro, pageableTamanho3);
        assertThat(paginaTamanho3.getTotalPages()).isEqualTo(1);
        assertThat(paginaTamanho3.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Deve retornar vazio quando o filtro não encontrar registros correspondentes")
    void deveRetornarVazioQuandoFiltroNaoEncontrar() {
    	AbstractFiltroPO<TelaPO> filtro = new AbstractFiltroPO<TelaPO>(new TelaPO(), 0, 10);        
    	filtro.getObjeto().setNome("Tela Inexistente");

        List<TelaPO> resultados = telaRepository.findByFiltro(filtro);

        assertThat(resultados).isEmpty();
    }

    @Test
    @DisplayName("Deve realizar operações de CRUD (Salvar, Atualizar e Deletar)")
    void deveExecutarCrudComSucesso() {
        TelaPO nova = new TelaPO();
        nova.setNome("Relatório Financeiro");
        nova.setQuery(Boolean.FALSE);

        TelaPO salva = telaRepository.save(nova);
        assertThat(salva.getId()).isNotNull();

        Optional<TelaPO> consultado = telaRepository.findById(salva.getId());
        assertThat(consultado).isPresent();

        consultado.get().setNome("Relatório Financeiro Atualizado");
        consultado.get().setQuery(Boolean.TRUE);
        TelaPO atualizado = telaRepository.save(consultado.get());
        assertThat(atualizado.getNome()).isEqualTo("Relatório Financeiro Atualizado");
        assertThat(atualizado.getQuery()).isTrue();

        telaRepository.delete(atualizado);
        Optional<TelaPO> deletado = telaRepository.findById(salva.getId());
        assertThat(deletado).isNotPresent();
    }
}