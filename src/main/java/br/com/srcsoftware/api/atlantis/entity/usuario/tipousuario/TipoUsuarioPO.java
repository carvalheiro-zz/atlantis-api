package br.com.srcsoftware.api.atlantis.entity.usuario.tipousuario;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import br.com.srcsoftware.api.atlantis.entity.abstracts.AuditoriaAbstractPOComEmpresa;
import br.com.srcsoftware.api.atlantis.entity.usuario.permissao.PermissaoPO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(
    name = "modular_tiposusuario",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_modular_tiposusuario_idEmpresa_nome", columnNames = { "idEmpresa", "nome" })
    },
    indexes = {
        @Index(name = "IDX_modular_tiposusuario_nome", columnList = "nome")
    }
)
public class TipoUsuarioPO extends AuditoriaAbstractPOComEmpresa {

    @NotNull(message = "Campo [Nome] é obrigatório!")
    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "modular_permissoestipousuario",
        joinColumns = @JoinColumn(
            name = "idTipoUsuario",
            foreignKey = @ForeignKey(name = "FK_modular_permissoestipousuario_tipousuario")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "idPermissao",
            foreignKey = @ForeignKey(name = "FK_modular_permissoestipousuario_permissao")
        )
    )
    @ToString.Exclude
    private Set<PermissaoPO> permissoes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoUsuarioPO that = (TipoUsuarioPO) o;
        return Objects.equals(getId(), that.getId()) &&
               Objects.equals(getEmpresa(), that.getEmpresa()) &&
               Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmpresa(), nome);
    }
}