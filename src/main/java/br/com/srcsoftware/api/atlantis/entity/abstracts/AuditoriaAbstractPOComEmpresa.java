package br.com.srcsoftware.api.atlantis.entity.abstracts;

import br.com.srcsoftware.api.atlantis.entity.manager.empresa.EmpresaPO;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe base abstrata que combina dados de auditoria e associação a uma Empresa.
 * 
 * @author Gabriel Damiani Carvalheiro <gabriel.carvalheiro@sigma.com.br>
 * @since 10/05/2012
 * @version 2.0
 */
@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public abstract class AuditoriaAbstractPOComEmpresa extends AuditoriaAbstractPO {
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
        name = "idEmpresa", 
        nullable = true,
        foreignKey = @ForeignKey
    )
    private EmpresaPO empresa;
}