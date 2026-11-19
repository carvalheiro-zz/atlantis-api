package br.com.srcsoftware.api.utilidades;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.srcsoftware.api.atlantis.dto.usuario.tela.TelaDTO;
import br.com.srcsoftware.api.atlantis.entity.usuario.tela.TelaPO;
import br.com.srcsoftware.api.atlantis.exceptions.ModelFactoryException;
import br.com.srcsoftware.api.atlantis.utilidades.ModelFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ExtendWith(MockitoExtension.class)
class ModelFactoryTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private ModelFactory<TelaPO, TelaDTO> modelFactory;

    private TelaPO telaPO;
    private TelaDTO telaDTO;

    @BeforeEach
    void setUp() {
        telaPO = new TelaPO();
        telaPO.setId(1L);
        telaPO.setNome("  Dashboard Principal  "); // Testando remoção de espaços do StringAdapter
        telaPO.setQuery(Boolean.TRUE);

        telaDTO = new TelaDTO();
        telaDTO.setId("1");
        telaDTO.setNome("Dashboard Principal");
        telaDTO.setQuery("true");
    }

    @Test
    @DisplayName("Deve converter DTO para PO com sucesso aplicando adaptadores")
    void deveConverterDtoParaPoComSucesso() throws Exception {
        TelaPO poConvertido = modelFactory.getPO(TelaPO.class, telaDTO);

        assertThat(poConvertido).isNotNull();
        assertThat(poConvertido.getId()).isEqualTo(1L);
        assertThat(poConvertido.getNome()).isEqualTo("Dashboard Principal");
        assertThat(poConvertido.getQuery()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar nulo ao tentar converter DTO nulo para PO")
    void deveRetornarNuloAoConverterDtoNulo() throws Exception {
        TelaPO poConvertido = modelFactory.getPO(TelaPO.class, null);
        assertThat(poConvertido).isNull();
    }

    @Test
    @DisplayName("Deve converter PO para DTO com sucesso")
    void deveConverterPoParaDtoComSucesso() throws Exception {
        TelaDTO dtoConvertido = modelFactory.getDTO(TelaDTO.class, telaPO);

        assertThat(dtoConvertido).isNotNull();
        assertThat(dtoConvertido.getId()).isEqualTo("1");
        assertThat(dtoConvertido.getNome()).isEqualTo("Dashboard Principal");
        assertThat(dtoConvertido.getQuery()).isEqualTo("true");
    }

    @Test
    @DisplayName("Deve retornar nulo ao tentar converter PO nulo para DTO")
    void deveRetornarNuloAoConverterPoNulo() throws Exception {
        TelaDTO dtoConvertido = modelFactory.getDTO(TelaDTO.class, null);
        assertThat(dtoConvertido).isNull();
    }

    @Test
    @DisplayName("Deve serializar PO para String JSON com sucesso")
    void deveConverterPoParaJsonComSucesso() throws Exception {
        String json = modelFactory.toJSON(telaPO);

        assertThat(json).isNotNull();
        assertThat(json).contains("Dashboard Principal");
    }

    @Test
    @DisplayName("Deve retornar nulo ao serializar PO nulo para JSON")
    void deveRetornarNuloAoConverterPoNuloParaJson() throws Exception {
        String json = modelFactory.toJSON(null);
        assertThat(json).isNull();
    }

    @Test
    @DisplayName("Deve desserializar String JSON para PO com sucesso")
    void deveConverterJsonParaPoComSucesso() throws Exception {
        String json = "{\"id\":\"1\",\"nome\":\"Dashboard Principal\",\"query\":true}";
        TelaPO po = modelFactory.fromJSON(TelaPO.class, json);

        assertThat(po).isNotNull();
        assertThat(po.getNome()).isEqualTo("Dashboard Principal");
    }

    @Test
    @DisplayName("Deve retornar nulo ao desserializar JSON nulo ou em branco")
    void deveRetornarNuloAoConverterJsonVazio() throws Exception {
        assertThat(modelFactory.fromJSON(TelaPO.class, null)).isNull();
        assertThat(modelFactory.fromJSON(TelaPO.class, "   ")).isNull();
    }

    @Test
    @DisplayName("Deve validar objeto com sucesso quando não houver violações de Bean Validation")
    void deveValidarObjetoComSucesso() {
        when(validator.validate(telaPO)).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> modelFactory.validarNotNull(telaPO));
    }

    @Test
    @DisplayName("Deve lançar ModelFactoryException quando o objeto a ser validado for nulo")
    void deveLancarExcecaoAoValidarObjetoNulo() {
        ModelFactoryException ex = assertThrows(ModelFactoryException.class, () -> {
            modelFactory.validarNotNull(null);
        });

        assertThat(ex.getMessage()).contains("O objeto a ser validado não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve lançar ModelFactoryException quando houver violações de restrições de validação")
    void deveLancarExcecaoQuandoHouverViolacoesDeValidacao() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<TelaPO> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Campo [Nome] é obrigatório!");

        Set<ConstraintViolation<TelaPO>> violations = new HashSet<>();
        violations.add(violation);

        when(validator.validate(telaPO)).thenReturn(violations);

        ModelFactoryException ex = assertThrows(ModelFactoryException.class, () -> {
            modelFactory.validarNotNull(telaPO);
        });

        assertThat(ex.getMessage()).contains("Erro de validação: Campo [Nome] é obrigatório!");
    }
}