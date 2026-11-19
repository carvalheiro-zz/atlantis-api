package br.com.srcsoftware.api.atlantis.entity.usuario.usuario;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.type.TrueFalseConverter;

import br.com.srcsoftware.api.atlantis.entity.abstracts.AuditoriaAbstractPOComEmpresa;
import br.com.srcsoftware.api.atlantis.entity.usuario.permissao.PermissaoPO;
import br.com.srcsoftware.api.atlantis.entity.usuario.tipousuario.TipoUsuarioPO;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
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
    name = "modular_usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_modular_usuarios_login_senha", columnNames = { "login", "senha" }),
        @UniqueConstraint(name = "UK_modular_usuarios_login_idEmpresa", columnNames = { "login", "idEmpresa" })
    },
    indexes = {
        @Index(name = "IDX_modular_usuarios_login", columnList = "login"),
        @Index(name = "IDX_modular_usuarios_idTipoUsuario", columnList = "idTipoUsuario")
    }
)
public class UsuarioPO extends AuditoriaAbstractPOComEmpresa {

    @NotNull(message = "O campo Login é obrigatório!")
    @Column(name = "login", nullable = false, length = 18)
    private String login;

    @NotNull(message = "O campo senha é obrigatório!")
    @Column(name = "senha", nullable = false, length = 50)
    private String senha;

    @Column(name = "senhaTemporaria", nullable = true, length = 60)
    private String senhaTemporaria;

    @NotNull(message = "O campo email é obrigatório!")
    @Email(message = "Email inválido!")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Convert(converter = TrueFalseConverter.class)
    @NotNull(message = "Campo [Status] é obrigatório!")
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull(message = "O campo Tipo Usuário é obrigatório!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "idTipoUsuario",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_modular_usuarios_tipousuario")
    )
    @ToString.Exclude
    private TipoUsuarioPO tipoUsuario;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "modular_permissoesusuario",
        joinColumns = @JoinColumn(
            name = "idUsuario",
            foreignKey = @ForeignKey(name = "FK_modular_permissoesusuario_usuario")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "idPermissao",
            foreignKey = @ForeignKey(name = "FK_modular_permissoesusuario_permissao")
        )
    )
    @ToString.Exclude
    private Set<PermissaoPO> permissoes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioPO usuarioPO = (UsuarioPO) o;
        return Objects.equals(getId(), usuarioPO.getId()) &&
               Objects.equals(login, usuarioPO.login) &&
               Objects.equals(senha, usuarioPO.senha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), login, senha);
    }
}