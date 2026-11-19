package br.com.srcsoftware.api.atlantis.repository.usuario.tela;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.com.srcsoftware.api.atlantis.entity.usuario.tela.TelaPO;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroPO;
import jakarta.persistence.criteria.Predicate;

@Repository
public interface TelaRepository extends JpaRepository<TelaPO, Long>, JpaSpecificationExecutor<TelaPO> {

	// Consulta padrão por ID já é fornecida nativamente pelo JpaRepository (findById)

	// Consulta por Nome exato (aproveitando o índice Unique / IDX)
	Optional<TelaPO> findByNome(String nome);

	/**
	 * Helper Specification para consultas dinâmicas baseadas no objeto de filtro.
	 * Suporta atributos simples e futuros relacionamentos encapsulados no Filtro.
	 */
	static Specification<TelaPO> porFiltro(AbstractFiltroPO<TelaPO> filtro) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filtro == null) {
				return criteriaBuilder.conjunction();
			}

			if (filtro.getObjeto() != null) {
				// ID herdado da AbstractPO
				if (filtro.getObjeto().getId() != null) {
					predicates.add(criteriaBuilder.equal(root.get("id"), filtro.getObjeto().getId()));
				}

				// Atributo nome (com suporte a busca parcial case-insensitive ou exata conforme necessidade)
				if (filtro.getObjeto().getNome() != null && !filtro.getObjeto().getNome().isBlank()) {
					predicates.add(criteriaBuilder.like(
							criteriaBuilder.lower(root.get("nome")), 
							"%" + filtro.getObjeto().getNome().toLowerCase() + "%"
							));
				}

				// Atributo query (Boolean)
				if (filtro.getObjeto().getQuery() != null) {
					predicates.add(criteriaBuilder.equal(root.get("query"), filtro.getObjeto().getQuery()));
				}

				// Exemplo de suporte a campos de relacionamento caso existam em outras entidades filtradas:
				/*
	            if (filtro.getOutroPO() != null && filtro.getOutroPO().getId() != null) {
	                predicates.add(criteriaBuilder.equal(root.get("outroPO").get("id"), filtro.getOutroPO().getId()));
	            }
				 */
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	/**
	 * Método padrão para execução de consultas dinâmicas usando o Filtro.
	 */
	default List<TelaPO> findByFiltro(AbstractFiltroPO<TelaPO> filtro) {
		return findAll(porFiltro(filtro));
	}
	
	/**
	 * Método para execução de consultas dinâmicas paginadas usando o Filtro.
	 */
	default Page<TelaPO> findByFiltroPaginado(AbstractFiltroPO<TelaPO> filtro, Pageable pageable) {
		return findAll(porFiltro(filtro), pageable);
	}
	
	
}