package br.com.srcsoftware.api.atlantis.entity.abstracts;

import br.com.srcsoftware.api.atlantis.utilidades.ObjectUtils;
import br.com.srcsoftware.api.atlantis.utilidades.Utilidades;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe base abstrata para todas as entidades persistentes do sistema.
 * 
 * @author Gabriel Damiani Carvalheiro <gabriel.carvalheiro@sigma.com.br>
 * @since 02/12/2011
 * @version 2.0
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Transient
    private String idTemp;

    public String getIdTemp() {
        if (ObjectUtils.isNuloOuVazio(this.idTemp)) {
            this.idTemp = Utilidades.gerarIdentificadorTemporalUploads() + Utilidades.generateCodigoGrande();
        }
        return this.idTemp;
    }

    public boolean isEmpty() {
        return ObjectUtils.isAllFieldsNull(this);
    }
}