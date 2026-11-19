package br.com.srcsoftware.api.atlantis.repository.manager.usuario.permissao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.srcsoftware.api.atlantis.entity.manager.usuario.permissao.PermissaoPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroPO;
import jakarta.persistence.criteria.Predicate;

@Repository
public interface PermissaoRepository extends JpaRepository<PermissaoPO, Long>, JpaSpecificationExecutor<PermissaoPO> {

	Optional<PermissaoPO> findByAcao(String acao);
	
	/**
	 * Helper Specification para consultas dinâmicas baseadas no objeto de filtro AbstractFiltroPO<PermissaoPO>JO.
	 * Suporta atributos simples e relacionamentos aninhados (como o objeto TelaPO e seu ID).
	 */
	static Specification<PermissaoPO> porFiltro(AbstractFiltroPO<PermissaoPO> filtro) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filtro == null) {
				return criteriaBuilder.conjunction();
			}

			if (filtro.getObjeto() != null) {
				// ID herdado (seja via AbstractPO ou campo direto no filtro)
				if (filtro.getObjeto().getId() != null) {
					predicates.add(criteriaBuilder.equal(root.get("id"), filtro.getObjeto().getId()));
				}

				// Atributo acao com busca parcial case-insensitive
				if (filtro.getObjeto().getAcao() != null && !filtro.getObjeto().getAcao().isBlank()) {
					predicates.add(criteriaBuilder.like(
							criteriaBuilder.lower(root.get("acao")), 
							"%" + filtro.getObjeto().getAcao().toLowerCase() + "%"
							));
				}

				// Atributo de relacionamento aninhado (TelaPO)
				if (filtro.getObjeto().getTela() != null) {
					if (filtro.getObjeto().getTela().getId() != null) {
						predicates.add(criteriaBuilder.equal(root.get("tela").get("id"), filtro.getObjeto().getTela().getId()));
					}
					if (filtro.getObjeto().getTela().getNome() != null && !filtro.getObjeto().getTela().getNome().isBlank()) {
						predicates.add(criteriaBuilder.like(
								criteriaBuilder.lower(root.get("tela").get("nome")), 
								"%" + filtro.getObjeto().getTela().getNome().toLowerCase() + "%"
								));
					}
				}

			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	/**
	 * Método padrão para execução de consultas dinâmicas usando o Filtro.
	 */
	default List<PermissaoPO> findByFiltro(AbstractFiltroPO<PermissaoPO> filtro) {
		return findAll(porFiltro(filtro));
	}
	
	/**
	 * Método para execução de consultas dinâmicas paginadas usando o Filtro.
	 */
	default Page<PermissaoPO> findByFiltroPaginado(AbstractFiltroPO<PermissaoPO> filtro, Pageable pageable) {
		return findAll(porFiltro(filtro), pageable);
	}
}