package br.com.srcsoftware.api.atlantis.repository.manager.usuario.usuario;

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

import br.com.srcsoftware.api.atlantis.entity.manager.empresa.EmpresaPO;
import br.com.srcsoftware.api.atlantis.entity.manager.usuario.tipousuario.TipoUsuarioPO;
import br.com.srcsoftware.api.atlantis.entity.manager.usuario.usuario.UsuarioPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroPO;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioPO usuarioPersistido;
    private TipoUsuarioPO tipoUsuarioPersistido;

    @BeforeEach
    void setUp() {
        TipoUsuarioPO tipoUsuario = new TipoUsuarioPO();
        tipoUsuario.setNome("Administrador");
        // Preenchendo os campos obrigatórios da auditoria herdados
        tipoUsuario.setDataOcorrenciaCriacao(LocalDateTime.now());
        tipoUsuario.setNomeUsuarioCriacao("sistema.teste");

        tipoUsuarioPersistido = entityManager.persist(tipoUsuario);
        
        UsuarioPO usuario = new UsuarioPO();
        usuario.setLogin("admin");
        usuario.setSenha("123456");
        usuario.setEmail("admin@atlantis.com.br");
        usuario.setStatus(Boolean.TRUE);
        usuario.setTipoUsuario(tipoUsuarioPersistido);
        usuario.setEmpresa(new EmpresaPO());
        usuario.getEmpresa().setId(1L);
        
        // Preenchendo os campos obrigatórios da auditoria herdados
        usuario.setDataOcorrenciaCriacao(LocalDateTime.now());
        usuario.setNomeUsuarioCriacao("sistema.teste");

        usuarioPersistido = entityManager.persistFlushFind(usuario);
    }

    @Test
    @DisplayName("Deve buscar Usuario por ID com sucesso")
    void deveBuscarPorId() {
        Optional<UsuarioPO> encontrado = usuarioRepository.findById(usuarioPersistido.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getLogin()).isEqualTo("admin");
        assertThat(encontrado.get().getTipoUsuario()).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar Usuario por Login exato com sucesso")
    void deveBuscarPorLoginExato() {
        Optional<UsuarioPO> encontrado = usuarioRepository.findByLogin("admin");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(usuarioPersistido.getId());
    }

    @Test
    @DisplayName("Deve filtrar Usuario utilizando o Specification com atributos simples e relacionamento")
    void deveFiltrarPorFiltroDinamico() {
    	AbstractFiltroPO<UsuarioPO> filtro = new AbstractFiltroPO<UsuarioPO>(new UsuarioPO(), 0, 10);
        filtro.getObjeto().setLogin("admin");
        filtro.getObjeto().setStatus(Boolean.TRUE);
        
        TipoUsuarioPO tipoFiltro = new TipoUsuarioPO();
        tipoFiltro.setId(tipoUsuarioPersistido.getId());
        filtro.getObjeto().setTipoUsuario(tipoFiltro);

        List<UsuarioPO> resultados = usuarioRepository.findByFiltro(filtro);

        assertThat(resultados)
            .isNotNull()
            .isNotEmpty()
            .extracting(UsuarioPO::getLogin)
            .contains("admin");
    }

    @Test
    @DisplayName("Deve retornar vazio quando o filtro não encontrar registros correspondentes")
    void deveRetornarVazioQuandoFiltroNaoEncontrar() {
    	AbstractFiltroPO<UsuarioPO> filtro = new AbstractFiltroPO<UsuarioPO>(new UsuarioPO(), 0, 10);
        filtro.getObjeto().setLogin("inexistente");

        List<UsuarioPO> resultados = usuarioRepository.findByFiltro(filtro);

        assertThat(resultados).isEmpty();
    }

    @Test
    @DisplayName("Deve realizar operações de CRUD (Salvar, Atualizar e Deletar)")
    void deveExecutarCrudComSucesso() {
        // Create / Insert adicional
        UsuarioPO novo = new UsuarioPO();
        novo.setLogin("operador");
        novo.setSenha("abcdef");
        novo.setEmail("operador@atlantis.com.br");
        novo.setStatus(Boolean.TRUE);
        novo.setTipoUsuario(tipoUsuarioPersistido);
        novo.setEmpresa(new EmpresaPO());
        novo.getEmpresa().setId(1L);
        // Preenchendo os campos obrigatórios da auditoria herdados
        novo.setDataOcorrenciaCriacao(LocalDateTime.now());
        novo.setNomeUsuarioCriacao("sistema.teste");

        UsuarioPO salvo = usuarioRepository.save(novo);
        assertThat(salvo.getId()).isNotNull();

        // Read
        Optional<UsuarioPO> consultado = usuarioRepository.findById(salvo.getId());
        assertThat(consultado).isPresent();

        // Update
        consultado.get().setEmail("novo.email@atlantis.com.br");
        UsuarioPO atualizado = usuarioRepository.save(consultado.get());
        assertThat(atualizado.getEmail()).isEqualTo("novo.email@atlantis.com.br");

        // Delete
        usuarioRepository.delete(atualizado);
        Optional<UsuarioPO> deletado = usuarioRepository.findById(salvo.getId());
        assertThat(deletado).isNotPresent();
    }
}