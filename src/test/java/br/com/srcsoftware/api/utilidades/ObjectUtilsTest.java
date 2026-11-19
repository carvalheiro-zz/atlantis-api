package br.com.srcsoftware.api.utilidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import br.com.srcsoftware.api.atlantis.utilidades.ObjectUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectUtilsTest {

    @Nested
    @DisplayName("Testes para isNuloOuVazio")
    class IsNuloOuVazioTest {

        @ParameterizedTest(name = "Deve retornar true para objeto considerado nulo/vazio: {0}")
        @MethodSource("proverValoresVazios")
        void deveRetornarTrueParaValoresVazios(Object valor, String descricao) {
            assertThat(ObjectUtils.isNuloOuVazio(valor)).isTrue();
        }

        @ParameterizedTest(name = "Deve retornar false para objeto com valor: {0}")
        @MethodSource("proverValoresPreenchidos")
        void deveRetornarFalseParaValoresPreenchidos(Object valor, String descricao) {
            assertThat(ObjectUtils.isNuloOuVazio(valor)).isFalse();
        }

        private static Stream<Arguments> proverValoresVazios() {
            return Stream.of(
                    Arguments.of(null, "null"),
                    Arguments.of("", "String vazia"),
                    Arguments.of("   ", "String apenas com espaços"),
                    Arguments.of(Collections.emptyList(), "Collection vazia"),
                    Arguments.of(Collections.emptyMap(), "Map vazio"),
                    Arguments.of(Optional.empty(), "Optional empty"),
                    Arguments.of(new String[]{}, "Array de Objetos vazio"),
                    Arguments.of(new int[]{}, "Array de primitivos (int) vazio"),
                    Arguments.of(new byte[]{}, "Array de primitivos (byte) vazio")
            );
        }

        private static Stream<Arguments> proverValoresPreenchidos() {
            return Stream.of(
                    Arguments.of("Texto", "String preenchida"),
                    Arguments.of(List.of("Item"), "Collection com item"),
                    Arguments.of(Map.of("chave", "valor"), "Map com item"),
                    Arguments.of(Optional.of("Valor"), "Optional com valor"),
                    Arguments.of(new String[]{"A"}, "Array de Objetos com elemento"),
                    Arguments.of(new int[]{1}, "Array de primitivos com elemento"),
                    Arguments.of(123, "Integer"),
                    Arguments.of(BigDecimal.TEN, "BigDecimal")
            );
        }
    }

    @Nested
    @DisplayName("Testes para isAllFieldsNull")
    class IsAllFieldsNullTest {

        @Test
        @DisplayName("Deve retornar true se objeto for null")
        void deveRetornarTrueSeObjetoForNull() {
            assertThat(ObjectUtils.isAllFieldsNull(null)).isTrue();
        }

        @Test
        @DisplayName("Deve retornar true se todos os campos forem nulos, brancos ou coleções vazias")
        void deveRetornarTrueSeTodosCamposForemNulos() {
            PessoaDTO dto = new PessoaDTO();
            dto.setNome("   ");
            dto.setIdade(0);
            dto.setAtivo(false);
            dto.setTelefones(Collections.emptyList());

            assertThat(ObjectUtils.isAllFieldsNull(dto)).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false se algum campo da própria classe tiver valor")
        void deveRetornarFalseSeCampoProprioTiverValor() {
            PessoaDTO dto = new PessoaDTO();
            dto.setNome("Gabriel");

            assertThat(ObjectUtils.isAllFieldsNull(dto)).isFalse();
        }

        @Test
        @DisplayName("Deve inspecionar e identificar valor na Superclasse")
        void deveRetornarFalseSeCampoDaSuperclasseTiverValor() {
            PessoaDTO dto = new PessoaDTO();
            dto.setId(10L); // Campo declarado na BaseEntityDTO

            assertThat(ObjectUtils.isAllFieldsNull(dto)).isFalse();
        }

        @Test
        @DisplayName("Deve realizar verificação recursiva em objetos aninhados")
        void deveVerificarRecursivamenteObjetoAninhado() {
            PessoaDTO dto = new PessoaDTO();
            EnderecoDTO endereco = new EnderecoDTO();
            endereco.setRua("Rua das Flores");
            dto.setEndereco(endereco);

            assertThat(ObjectUtils.isAllFieldsNull(dto)).isFalse();
        }

        @Test
        @DisplayName("Deve retornar true se objeto aninhado também estiver limpo")
        void deveRetornarTrueSeObjetoAninhadoEstiverLimpo() {
            PessoaDTO dto = new PessoaDTO();
            dto.setEndereco(new EnderecoDTO()); // Endereco sem atributos preenchidos

            assertThat(ObjectUtils.isAllFieldsNull(dto)).isTrue();
        }
    }

    // Classes auxiliares para teste de reflexão e herança
    static class BaseEntityDTO {
        private Long id;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    static class PessoaDTO extends BaseEntityDTO {
        private String nome;
        private int idade;
        private boolean ativo;
        private List<String> telefones;
        private EnderecoDTO endereco;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public int getIdade() { return idade; }
        public void setIdade(int idade) { this.idade = idade; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
        public List<String> getTelefones() { return telefones; }
        public void setTelefones(List<String> telefones) { this.telefones = telefones; }
        public EnderecoDTO getEndereco() { return endereco; }
        public void setEndereco(EnderecoDTO endereco) { this.endereco = endereco; }
    }

    static class EnderecoDTO {
        private String rua;

        public String getRua() { return rua; }
        public void setRua(String rua) { this.rua = rua; }
    }
}