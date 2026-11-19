package br.com.srcsoftware.api.atlantis.entity.usuario.permissao;

import java.util.Objects;

import br.com.srcsoftware.api.atlantis.entity.abstracts.AbstractPO;
import br.com.srcsoftware.api.atlantis.entity.usuario.tela.TelaPO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    name = "modular_permissoes",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_modular_permissoes_acao_idTela", columnNames = { "acao", "idTela" })
    },
    indexes = {
        @Index(name = "IDX_modular_permissoes_acao", columnList = "acao"),
        @Index(name = "IDX_modular_permissoes_idTela", columnList = "idTela")
    }
)
public class PermissaoPO extends AbstractPO {

    @NotNull(message = "Campo [Ação] é obrigatório!")
    @Column(name = "acao", nullable = false, length = 30)
    private String acao;

    @NotNull(message = "Campo [Tela] é obrigatório!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "idTela",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_modular_permissoes_tela")
    )
    @ToString.Exclude
    private TelaPO tela;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissaoPO that = (PermissaoPO) o;
        return Objects.equals(getId(), that.getId()) &&
               Objects.equals(acao, that.acao) &&
               Objects.equals(tela, that.tela);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), acao, tela);
    }
}