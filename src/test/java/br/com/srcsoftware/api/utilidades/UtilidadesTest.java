package br.com.srcsoftware.api.utilidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.srcsoftware.api.atlantis.utilidades.Utilidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilidadesTest {

    @Nested
    @DisplayName("Testes para removerEspacosExcedentes")
    class RemoverEspacosExcedentesTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        void deveRetornarNullOuVazioConformeEntrada(String input) {
            if (input == null) {
                assertThat(Utilidades.removerEspacosExcedentes(null)).isNull();
            } else {
                assertThat(Utilidades.removerEspacosExcedentes(input)).isEmpty();
            }
        }

        @Test
        @DisplayName("Deve remover múltiplos espaços internos, no início e no fim")
        void deveSanitizarEspacosExcedentes() {
            String input = "   Java   21   Spring   Boot   ";
            String resultado = Utilidades.removerEspacosExcedentes(input);

            assertThat(resultado).isEqualTo("Java 21 Spring Boot");
        }
    }

    @Nested
    @DisplayName("Testes para parseBigDecimal (String -> BigDecimal)")
    class ParseBigDecimalFromStringTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "R$", "R$ "})
        void deveRetornarNullParaEntradasInvalidasOuVazias(String value) {
            assertThat(Utilidades.parseBigDecimal(value)).isNull();
        }

        @ParameterizedTest
        @CsvSource({
                "'123,45', 123.45",
                "'1.234,56', 1234.56",
                "'1234,56', 1234.56",
                "'R$ 1.234,56', 1234.56",
                "'$ 1.234,56', 1234.56",
                "'R$ 50,00', 50.00"
        })
        void deveConverterDiferentesFormatosParaBigDecimal(String input, BigDecimal esperado) {
            BigDecimal resultado = Utilidades.parseBigDecimal(input);
            assertThat(resultado).isEqualByComparingTo(esperado);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para String não numérica")
        void deveLancarExcecaoParaTextoInvalido() {
            assertThatThrownBy(() -> Utilidades.parseBigDecimal("ABC"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Valor monetário/numérico inválido");
        }
    }

    @Nested
    @DisplayName("Testes para parseBigDecimal (BigDecimal -> String)")
    class ParseBigDecimalToStringTest {

        @Test
        @DisplayName("Deve retornar null quando BigDecimal for null")
        void deveRetornarNullSeBigDecimalForNull() {
            assertThat(Utilidades.parseBigDecimal((BigDecimal) null)).isNull();
            assertThat(Utilidades.parseBigDecimalComMoeda(null)).isNull();
        }

        @Test
        @DisplayName("Deve formatar BigDecimal em String numérica PT-BR (sem moeda)")
        void deveFormatarParaStringNumerica() {
            BigDecimal valor = new BigDecimal("1234.5");
            String resultado = Utilidades.parseBigDecimal(valor);

            assertThat(resultado).isEqualTo("1.234,50");
        }

        @Test
        @DisplayName("Deve formatar BigDecimal em String monetária PT-BR (com R$)")
        void deveFormatarParaStringMonetaria() {
            BigDecimal valor = new BigDecimal("1234.56");
            String resultado = Utilidades.parseBigDecimalComMoeda(valor);

            // Substitui o caractere de espaço não-quebrável \u00A0 comum no NumberFormat por espaço simples
            String resultadoSanitizado = resultado.replace("\u00A0", " ");

            assertThat(resultadoSanitizado).contains("R$").contains("1.234,56");
        }
    }

    @Nested
    @DisplayName("Testes para Geração de Identificadores Temporais")
    class IdentificadorTemporalTest {

        @Test
        @DisplayName("Deve converter LocalDateTime em milissegundosEpoch")
        void deveConverterLocalDateTimeParaMilisegundos() {
            LocalDateTime now = LocalDateTime.now();
            Long millis = Utilidades.getLocalDateTimeInMilisegundos(now);

            assertThat(millis).isNotNull().isPositive();
        }

        @Test
        @DisplayName("Deve gerar código grande com caracteres numéricos")
        void deveGerarCodigoGrande() {
            String codigo = Utilidades.generateCodigoGrande();

            assertThat(codigo)
                    .isNotNull()
                    .isNotBlank()
                    .containsOnlyDigits();
        }
    }
}