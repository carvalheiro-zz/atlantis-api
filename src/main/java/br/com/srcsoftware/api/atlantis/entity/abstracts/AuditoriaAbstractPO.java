package br.com.srcsoftware.api.atlantis.entity.abstracts;

import java.time.LocalDateTime;

import br.com.srcsoftware.api.atlantis.utilidades.ObjectUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe base abstrata para entidades que possuem controle e registro de auditoria.
 * 
 * @author Gabriel Damiani Carvalheiro <gabriel.carvalheiro@sigma.com.br>
 * @since 10/05/2012
 * @version 2.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@MappedSuperclass
public abstract class AuditoriaAbstractPO extends AbstractPO {

    @NotNull(message = "Campo [Usuario criação] é obrigatório!")
    @Column(name = "nomeUsuarioCriacao", nullable = false, length = 100)
    private String nomeUsuarioCriacao;

    @Column(name = "nomeUsuarioAlteracao", nullable = true, length = 100)
    private String nomeUsuarioAlteracao;

    @NotNull(message = "Campo [Data criação] é obrigatório!")
    @Column(name = "dataOcorrenciaCriacao", nullable = false)
    private LocalDateTime dataOcorrenciaCriacao;

    @Column(name = "dataOcorrenciaAlteracao", nullable = true)
    private LocalDateTime dataOcorrenciaAlteracao;

    /**
     * Define os dados de auditoria automaticamente na inserção ou alteração.
     * 
     * @param nomeUsuario Nome do usuário logado
     * @param dataOcorrencia Data/Hora da operação
     */
    public void setarDadosAuditoria(String nomeUsuario, LocalDateTime dataOcorrencia) {
        if (ObjectUtils.isNuloOuVazio(getDataOcorrenciaCriacao())) {
            setNomeUsuarioCriacao(nomeUsuario);
            setDataOcorrenciaCriacao(dataOcorrencia);
        } else {
            setNomeUsuarioAlteracao(nomeUsuario);
            setDataOcorrenciaAlteracao(dataOcorrencia);
        }
    }
}