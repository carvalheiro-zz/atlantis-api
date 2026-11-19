package br.com.srcsoftware.api.atlantis.entity.usuario.tela;

import java.util.Objects;

import org.hibernate.type.TrueFalseConverter;

import br.com.srcsoftware.api.atlantis.entity.abstracts.AbstractPO;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
    name = "modular_telas",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_modular_telas_nome", columnNames = { "nome" })
    },
    indexes = {
        @Index(name = "IDX_modular_telas_nome", columnList = "nome")
    }
)
public class TelaPO extends AbstractPO {

    @NotNull(message = "Campo [Nome] é obrigatório!")
    @Column(name = "nome", nullable = false, length = 45)
    private String nome;

    @Convert(converter = TrueFalseConverter.class)
    @Column(name = "query", nullable = true)
    private Boolean query;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelaPO telaPO = (TelaPO) o;
        return Objects.equals(getId(), telaPO.getId()) &&
               Objects.equals(nome, telaPO.nome) &&
               Objects.equals(query, telaPO.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), nome, query);
    }
}