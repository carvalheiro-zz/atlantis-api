package br.com.srcsoftware.api.atlantis.repository.usuario.tipousuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.srcsoftware.api.atlantis.entity.usuario.tipousuario.TipoUsuarioPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroPO;
import jakarta.persistence.criteria.Predicate;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuarioPO, Long>, JpaSpecificationExecutor<TipoUsuarioPO> {

	// Consulta por Nome exato (aproveitando o índice Unique / IDX)
    Optional<TipoUsuarioPO> findByNome(String nome);
    
    /**
     * Helper Specification para consultas dinâmicas baseadas no objeto de filtro AbstractFiltroPO<TipoUsuarioPO>.
     * Suporta atributos simples, campos herdados de auditoria/empresa e relacionamentos aninhados.
     */
    static Specification<TipoUsuarioPO> porFiltro(AbstractFiltroPO<TipoUsuarioPO> filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro == null) {
                return criteriaBuilder.conjunction();
            }

            // ID herdado da AbstractPO
            if (filtro.getObjeto().getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), filtro.getObjeto().getId()));
            }

            // Atributo nome com busca parcial case-insensitive
            if (filtro.getObjeto().getNome() != null && !filtro.getObjeto().getNome().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nome")), 
                    "%" + filtro.getObjeto().getNome().toLowerCase() + "%"
                ));
            }

            // Suporte a relacionamento ou outros campos caso preenchidos no filtro
            // Exemplo de tratamento para collections ou campos herdados (como idEmpresa via AuditoriaAbstractPOComEmpresa)
            if (filtro.getObjeto().getPermissoes() != null && !filtro.getObjeto().getPermissoes().isEmpty()) {
                // Join na collection de permissoes para filtrar por itens contidos
                predicates.add(root.join("permissoes").in(filtro.getObjeto().getPermissoes()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Método padrão para execução de consultas dinâmicas usando o Filtro.
     */
    default List<TipoUsuarioPO> findByFiltro(AbstractFiltroPO<TipoUsuarioPO> filtro) {
        return findAll(porFiltro(filtro));
    }
}