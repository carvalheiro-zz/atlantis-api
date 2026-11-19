package br.com.srcsoftware.api.atlantis.dto.usuario.tipousuario;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import br.com.srcsoftware.api.atlantis.dto.abstracts.AuditoriaAbstractDTOComEmpresa;
import br.com.srcsoftware.api.atlantis.dto.usuario.permissao.PermissaoDTO;
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
public class TipoUsuarioDTO extends AuditoriaAbstractDTOComEmpresa {

	private String id;
    private String nome;
    private Set<PermissaoDTO> permissoes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoUsuarioDTO that = (TipoUsuarioDTO) o;
        return Objects.equals(getId(), that.getId()) &&
               Objects.equals(getEmpresa(), that.getEmpresa()) &&
               Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmpresa(), nome);
    }
}