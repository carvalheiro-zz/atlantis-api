package br.com.srcsoftware.api.atlantis.dto.abstracts;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.srcsoftware.api.atlantis.dto.manager.empresa.EmpresaDTO;
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
public abstract class AbstractDTOComEmpresa extends AbstractDTO{

	@JsonIgnore
	private EmpresaDTO empresa;

}
