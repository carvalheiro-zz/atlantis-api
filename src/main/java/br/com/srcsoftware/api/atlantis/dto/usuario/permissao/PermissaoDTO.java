package br.com.srcsoftware.api.atlantis.dto.usuario.permissao;

import java.util.Objects;

import br.com.srcsoftware.api.atlantis.dto.abstracts.AbstractDTO;
import br.com.srcsoftware.api.atlantis.dto.usuario.tela.TelaDTO;
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
public class PermissaoDTO extends AbstractDTO {

	private String id;
    private String acao;
    private TelaDTO tela;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissaoDTO that = (PermissaoDTO) o;
        return Objects.equals(getId(), that.getId()) &&
               Objects.equals(acao, that.acao) &&
               Objects.equals(tela, that.tela);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), acao, tela);
    }
}