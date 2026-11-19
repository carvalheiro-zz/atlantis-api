package br.com.srcsoftware.api.atlantis.repository.manager.usuario.tipousuario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import br.com.srcsoftware.api.atlantis.entity.manager.usuario.tipousuario.TipoUsuarioPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroPO;

@DataJpaTest
@ActiveProfiles("test")
class TipoUsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    private TipoUsuarioPO tipoUsuarioPersistido;

    @BeforeEach
    void setUp() {
        TipoUsuarioPO tipo = new TipoUsuarioPO();
        tipo.setNome("Administrador Master");
        
        // Preenchendo os campos obrigatórios da auditoria herdados
        tipo.setDataOcorrenciaCriacao(LocalDateTime.now());
        tipo.setNomeUsuarioCriacao("sistema.teste");
        
        tipoUsuarioPersistido = entityManager.persistFlushFind(tipo);
    }

    @Test
    @DisplayName("Deve buscar TipoUsuario por ID com sucesso")
    void deveBuscarPorId() {
        Optional<TipoUsuarioPO> encontrado = tipoUsuarioRepository.findById(tipoUsuarioPersistido.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Administrador Master");
    }

    @Test
    @DisplayName("Deve filtrar TipoUsuario utilizando o Specification e o POJO de Filtro")
    void deveFiltrarPorFiltroDinamico() {
    	AbstractFiltroPO<TipoUsuarioPO> filtro = new AbstractFiltroPO<TipoUsuarioPO>(new TipoUsuarioPO(), 0, 10);
        filtro.getObjeto().setNome("Administrador");

        List<TipoUsuarioPO> resultados = tipoUsuarioRepository.findByFiltro(filtro);

        assertThat(resultados)
            .isNotNull()
            .isNotEmpty()
            .extracting(TipoUsuarioPO::getNome)
            .contains("Administrador Master");
    }

    @Test
    @DisplayName("Deve retornar vazio quando o filtro não encontrar registros correspondentes")
    void deveRetornarVazioQuandoFiltroNaoEncontrar() {
    	AbstractFiltroPO<TipoUsuarioPO> filtro = new AbstractFiltroPO<TipoUsuarioPO>(new TipoUsuarioPO(), 0, 10);
        filtro.getObjeto().setNome("Cargo Inexistente");

        List<TipoUsuarioPO> resultados = tipoUsuarioRepository.findByFiltro(filtro);

        assertThat(resultados).isEmpty();
    }

    @Test
    @DisplayName("Deve realizar operações de CRUD (Salvar, Atualizar e Deletar)")
    void deveExecutarCrudComSucesso() {
        // Create / Insert adicional
        TipoUsuarioPO novo = new TipoUsuarioPO();
        novo.setNome("Gerente de Unidade");
        novo.setDataOcorrenciaCriacao(LocalDateTime.now());
        novo.setNomeUsuarioCriacao("sistema.teste");

        TipoUsuarioPO salvo = tipoUsuarioRepository.save(novo);
        assertThat(salvo.getId()).isNotNull();

        // Read
        Optional<TipoUsuarioPO> consultado = tipoUsuarioRepository.findById(salvo.getId());
        assertThat(consultado).isPresent();

        // Update
        consultado.get().setNome("Diretor Regional");
        TipoUsuarioPO atualizado = tipoUsuarioRepository.save(consultado.get());
        assertThat(atualizado.getNome()).isEqualTo("Diretor Regional");

        // Delete
        tipoUsuarioRepository.delete(atualizado);
        Optional<TipoUsuarioPO> deletado = tipoUsuarioRepository.findById(salvo.getId());
        assertThat(deletado).isNotPresent();
    }
}