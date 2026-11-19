package br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbstractFiltroPO<T> {
	
	private T objeto;
	
	private int pagina; // Spring usa base 0
    private int tamanho;
}