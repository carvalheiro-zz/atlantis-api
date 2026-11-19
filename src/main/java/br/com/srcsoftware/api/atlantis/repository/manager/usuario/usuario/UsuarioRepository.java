package br.com.srcsoftware.api.atlantis.repository.manager.usuario.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.srcsoftware.api.atlantis.entity.manager.usuario.usuario.UsuarioPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroPO;
import jakarta.persistence.criteria.Predicate;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioPO, Long>, JpaSpecificationExecutor<UsuarioPO> {

    // Consulta por Login exato (aproveitando o índice Unique / IDX)
    Optional<UsuarioPO> findByLogin(String login);

    /**
     * Helper Specification para consultas dinâmicas baseadas no objeto de filtro AbstractFiltroPO<UsuarioPO>.
     * Suporta atributos simples, campos de auditoria e relacionamentos aninhados (TipoUsuarioPO).
     */
    static Specification<UsuarioPO> porFiltro(AbstractFiltroPO<UsuarioPO> filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro == null) {
                return criteriaBuilder.conjunction();
            }

            // ID herdado da AbstractPO
            if (filtro.getObjeto().getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filtro.getObjeto().getId()));
            }

            // Login com busca parcial case-insensitive
            if (filtro.getObjeto().getLogin() != null && !filtro.getObjeto().getLogin().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("login")), 
                    "%" + filtro.getObjeto().getLogin().toLowerCase() + "%"
                ));
            }

            // Email com busca parcial case-insensitive
            if (filtro.getObjeto().getEmail() != null && !filtro.getObjeto().getEmail().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), 
                    "%" + filtro.getObjeto().getEmail().toLowerCase() + "%"
                ));
            }

            // Status (Boolean)
            if (filtro.getObjeto().getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filtro.getObjeto().getStatus()));
            }

            // Relacionamento aninhado (TipoUsuarioPO)
            if (filtro.getObjeto().getTipoUsuario() != null) {
                if (filtro.getObjeto().getTipoUsuario().getId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("tipoUsuario").get("id"), filtro.getObjeto().getTipoUsuario().getId()));
                }
                if (filtro.getObjeto().getTipoUsuario().getNome() != null && !filtro.getObjeto().getTipoUsuario().getNome().isBlank()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("tipoUsuario").get("nome")), 
                        "%" + filtro.getObjeto().getTipoUsuario().getNome().toLowerCase() + "%"
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Método padrão para execução de consultas dinâmicas usando o Filtro.
     */
    default List<UsuarioPO> findByFiltro(AbstractFiltroPO<UsuarioPO> filtro) {
        return findAll(porFiltro(filtro));
    }
}