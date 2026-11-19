package br.com.srcsoftware.api.atlantis.entity.manager.empresa;

import java.time.LocalTime;

import org.hibernate.type.TrueFalseConverter;

import br.com.srcsoftware.api.atlantis.entity.abstracts.AuditoriaAbstractPO;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
    name = "modular_empresas",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_modular_empresas_cnpj", columnNames = { "cnpj" }),
        @UniqueConstraint(name = "UK_modular_empresas_codigo", columnNames = { "codigo" })
    },
    indexes = {
        @Index(name = "IDX_modular_empresas_cnpj", columnList = "cnpj"),
        @Index(name = "IDX_modular_empresas_codigo", columnList = "codigo"),
        @Index(name = "IDX_modular_empresas_idEmpresaMae", columnList = "idEmpresaMae")
    }
)
public class EmpresaPO extends AuditoriaAbstractPO {

    /** ENDERECO */
    @NotNull(message = "Campo [Logradouro] é obrigatório!")
    @Column(name = "logradouro", nullable = false, length = 100)
    private String logradouro;

    @NotNull(message = "Campo [Número] é obrigatório!")
    @Column(name = "numeroEndereco", nullable = false, length = 5)
    private String numeroEndereco;

    @NotNull(message = "Campo [Bairro] é obrigatório!")
    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @NotNull(message = "Campo [CEP] é obrigatório!")
    @Column(name = "cep", nullable = false, length = 12)
    private String cep;

    @NotNull(message = "Campo [Cidade] é obrigatório!")
    @Column(name = "cidade", nullable = false, length = 50)
    private String cidade;

    @NotNull(message = "Campo [Estado] é obrigatório!")
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "complemento", nullable = true, length = 50)
    private String complemento;

    /** PESSOA */
    @NotNull(message = "Campo [Razão Social] é obrigatório!")
    @Column(name = "razaoSocial", nullable = false, length = 100)
    private String razaoSocial;

    @NotNull(message = "Campo [Telefone 1] é obrigatório!")
    @Column(name = "telefone1", nullable = false, length = 20)
    private String telefone1;

    @Column(name = "telefone2", nullable = true, length = 20)
    private String telefone2;

    @Column(name = "telefone3", nullable = true, length = 20)
    private String telefone3;

    @Column(name = "email", nullable = true, length = 50)
    private String email;

    @Column(name = "imagem", nullable = true, length = 200)
    private String imagem;

    /** PESSOA JURIDICA */
    @NotNull(message = "Campo [CNPJ] é obrigatório!")
    @Column(name = "cnpj", nullable = false, length = 18)
    private String cnpj;

    @Column(name = "nomeFantasia", nullable = true, length = 100)
    private String nomeFantasia;

    @Column(name = "inscricaoEstadual", nullable = true, length = 20)
    private String inscricaoEstadual;

    @Column(name = "inscricaoMunicipal", nullable = true, length = 20)
    private String inscricaoMunicipal;

    @Column(name = "nomeResponsavel", nullable = true, length = 70)
    private String nomeResponsavel;

    @NotNull(message = "Campo [Cargo responsável] é obrigatório!")
    @Column(name = "cargoResponsavel", nullable = false, length = 20)
    private String cargoResponsavel;

    @Column(name = "emailResponsavel", nullable = true, length = 70)
    private String emailResponsavel;

    /** EMPRESA */
    @NotNull(message = "Campo [Código] é obrigatório!")
    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;

    @NotNull(message = "Campo [Ativo?] é obrigatório!")
    @Convert(converter = TrueFalseConverter.class)
    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @NotNull(message = "Campo [Nome sistema] é obrigatório!")
    @Column(name = "nomeSistema", nullable = false, length = 20)
    private String nomeSistema;

    @NotNull(message = "Campo [Versão sistema] é obrigatório!")
    @Column(name = "versaoSistema", nullable = false, length = 15)
    private String versaoSistema;

    @NotNull(message = "Campo [Hora fechamento] é obrigatório!")
    @Column(name = "horaFechamento", nullable = false)
    private LocalTime horaFechamento;

    @NotNull(message = "Campo [Hora abertura] é obrigatório!")
    @Column(name = "horaAbertura", nullable = false)
    private LocalTime horaAbertura;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
        name = "idEmpresaMae",
        nullable = true,
        foreignKey = @ForeignKey(name = "FK_modular_empresas_idEmpresaMae")
    )
    private EmpresaPO empresa;
}