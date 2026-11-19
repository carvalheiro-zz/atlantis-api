# PROMPT DE REFATORAÇÃO DE ENTIDADES JPA
```text
Aja como um Arquiteto de Software Java Especialista em Spring Boot 3.3+ (Java 21) e JPA/Hibernate. 
Aplique as seguintes regras de Clean Code e convenções de mapeamento para as entidades Java anexadas/coladas abaixo:

1. ESTRUTURA E LOMBOK:
   - Adicione @Getter, @Setter, @NoArgsConstructor e @AllArgsConstructor onde aplicável.
   - Substitua o método toString() manual por @ToString(callSuper = true) do Lombok.
   - Adicione @ToString.Exclude em todos os atributos de relacionamento (@ManyToOne, @OneToMany, @OneToOne) para evitar LazyInitializationException e StackOverflowError.
   - Mantenha ou ajuste equals() e hashCode() com base no ID herdado da AbstractPO (usando Objects.equals e Objects.hash).

2. MA PEAMENTO DE BANCO DE DADOS E CONSTRAINTS:
   - Declare explicitamente os nomes das colunas com name = "..." na anotação @Column.
   - Em @Table, defina e nomeie explicitamente as Unique Constraints seguindo o padrão: UK_<nome_tabela>_<coluna(s)>.
   - Defina e nomeie os Índices em @Table usando o padrão: IDX_<nome_tabela>_<coluna(s)>.
   - Em relacionamentos (@ManyToOne, @OneToOne, etc.), utilize FetchType.LAZY por padrão.
   - Nomeie explicitamente as Foreign Keys usando foreignKey = @ForeignKey(name = "FK_<tabela_origem>_<tabela_destino>").

3. PADRÕES DE PROJETO:
   - Remova métodos ou importações obsoletas.
   - Garanta compatibilidade com PhysicalNamingStrategyStandardImpl (preservando a nomenclatura camelCase do banco).

Entregue apenas o código Java totalmente refatorado e pronto para produção para cada classe enviada.
```
---


# PROMPT DE CRIAÇÃO DE REPOSITORY JPA

Para criar esse prompt padrão e direto, podemos estruturá-lo como um **Prompt Mestre**. Sempre que você precisar gerar um novo `Repository` (ou outra classe padrão do projeto), basta colar o código da Entidade (`*PO.java`) e da Entidade de Filtro (`*Filtro.java`) junto com o texto abaixo.

Você pode salvar o seguinte bloco em um arquivo de texto para reutilizá-lo sempre que quiser:

```text
Atue como um Arquiteto de Software Java Especialista em Spring Boot 3.3+ (Java 21) e Spring Data JPA.
Preciso que você crie a classe Repository correspondente à Entidade e ao Filtro fornecidos abaixo, seguindo estritamente os padrões do nosso projeto (Spring Boot 3.3.4, Java 21, Lombok, Padrão de Nomenclatura, JpaSpecificationExecutor).

REQUISITOS DO REPOSITORY:
1. Herdar de JpaRepository<[NomeEntidade]PO, Long> e JpaSpecificationExecutor<[NomeEntidade]PO>.
2. Anotar com @Repository.
3. Incluir métodos padrão de CRUD nativos do Spring Data.
4. Incluir método de consulta padrão por ID (findById nativo).
5. Incluir um método default ou helper usando Specification (JpaSpecificationExecutor) chamado findByFiltro([NomeEntidade]Filtro filtro) que faça consultas dinâmicas considerando todos os atributos não nulos preenchidos na classe de Filtro, incluindo suporte robusto a campos de relacionamento aninhados (ex: filtro.getRelacionamento().getId()).
Deve ter os filtros referentes ao @OneToOne, @OneToMeny, @ManyToOne e @ManyToMany. A consunta deve conter a possibilidade de consultar por todos os campos do relacionamento considerando todos os atributos não nulos preenchidos exceto os de relacionamento.
6. Manter o código limpo, otimizado para performance e compatível com as versões LTS atuais.
7. Criar Script de CREATE da classe Entity PO para acrescentar no arquivo /src/test/resources/schema.sql.
8. Criar a classe de teste unitario. 

Aqui estão as classes de referência para esta geração:
[Cole o código da sua Entity PO aqui]
[Cole o código da sua Entity Filtro aqui]

```



### Dica Prática

Se preferir, você também pode usar o recurso de **Skills (habilidades)** do nosso ambiente para salvar este comportamento permanentemente, permitindo que você apenas diga *"Crie o Repository para a classe X"* sem precisar colar o prompt gigante todas as vezes. Deseja que eu crie uma skill personalizada para isso para você?


---

# PROMPT DE CRIAÇÃO DE SERVICE
```
Atue como um Arquiteto de Software Sênior especializado em Spring Boot 3.3.4 (Java 21). Preciso que você crie uma nova classe Service e sua respectiva classe de Testes Unitários, seguindo rigorosamente os mesmos padrões arquiteturais, de código e de testes aplicados na PermissaoService e na TelaService.

Por favor, siga estritamente os seguintes critérios:

1. **Arquitetura & Padrões do Service:**
   - Anote a classe com `@Service` e `@Transactional(readOnly = true)`, aplicando `@Transactional` puro (`@Transactional`) nas operações de escrita (`salvar`, `atualizar`, `deletar`).
   - Injeção de dependências estritamente via construtor (`@RequiredArgsConstructor` do Lombok) para o Repository correspondente, o `ModelFactory` genérico (`ModelFactory<[NomeEntidade]PO, [NomeEntidade]DTO>`) e dependências necessárias.
   - Utilize `@Slf4j` para logs estruturados informando parâmetros de entrada e retornos.

2. **Validação de Campos Obrigatórios (ModelFactory):**
   - Nas operações de escrita (`salvar`, `atualizar`), após converter o DTO para PO utilizando a factory, invoque obrigatoriamente a validação genérica de restrições por Bean Validation integrada na factory: `[nome]ModelFactory.validarNotNull(po);`.
   - Capture a exceção `ModelFactoryException` nos blocos `try-catch` dos métodos de escrita e relance-a como `ApplicationException` com a causa raiz adequada.

3. **Assinatura e Comportamento dos Métodos:**
   - Métodos de escrita (`salvar`, `atualizar`) recebem e retornam o `[NomeEntidade]DTO`.
   - O método de atualização deve validar se o ID não é nulo/vazio, converter para `Long` (tratando `NumberFormatException`) e verificar se o registro existe (`existsById`) antes de prosseguir.
   - Métodos de busca por ID e exclusão aceitam `String id`, fazem a conversão segura para `Long`, tratam exceções de formato e manipulam `Optional` corretamente.
   - Listagens (`listarTodas`) e buscas por filtro (`buscarPorFiltro`) iteram sobre a lista de POs e convertem cada item para DTO usando o `ModelFactory`.

4. **Classe de Testes Unitários correspondente:**
   - Utilize `@ExtendWith(MockitoExtension.class)`.
   - Mocke o Repository e o `ModelFactory` com `@Mock`, injetando-os com `@InjectMocks` na classe de Service.
   - Escreva testes unitários cobrindo fluxos de sucesso (salvar, atualizar, deletar, buscar por ID, listar, filtrar) e fluxos de exceção (validações nulas, IDs inválidos, falhas na factory), utilizando asserções limpas e JUnit 5 (`assertThrows`, `assertEquals`, etc.).

Aqui estão os detalhes da entidade/módulo que preciso implementar agora:
- **Nome do Recurso / Entidade:** [INSERIR NOME AQUI, ex: Cliente, Produto, Usuario]
- **Classes envolvidas:** [INSERIR NOMES DO PO, DTO, Repository e Filtro, se houver]
```

---

# PROMPT DE CRIAÇÃO DE FACADE

```
Atue como um Arquiteto de Software Java Especialista em Spring Boot 3.3+ (Java 21) e Spring Data JPA.
Preciso que você crie a classe Facade correspondente ao Service fornecido, seguindo estritamente os padrões arquiteturais do nosso projeto (Spring Boot 3.3.4, Java 21, Lombok, DTOs baseados exclusivamente em String, e delegação limpa).

REQUISITOS OBRIGATÓRIOS DO FACADE:
1. Anotar a classe com @Service (ou @Component) e @RequiredArgsConstructor para injeção de dependência via construtor.
2. Injetar apenas a classe [NomeEntidade]Service correspondente.
3. Os métodos do Facade devem atuar como uma camada de fachada pura, repassando as chamadas diretamente para o Service e retornando os tipos esperados ([NomeEntidade]DTO, `Optional<[NomeEntidade]DTO>`, `List<[NomeEntidade]DTO>`, `void`, etc.).
4. Não duplicar lógica de logs estruturados ou medição de tempo (`System.nanoTime()`) nos métodos do Facade, pois isso já é tratado automaticamente pelo nosso `FacadePerformanceAspect` via AOP.
5. Propagar as exceções customizadas (como `ApplicationException`) lançadas pelo Service.
6. Criar também a classe de teste unitário correspondente (`[NomeEntidade]FacadeTest`) utilizando JUnit 5 e Mockito, cobrindo os cenários de sucesso e a propagação de exceções do Service.

Aqui estão as classes de referência para esta geração:
- Service de Origem: [COLAR AQUI A CLASSE SERVICE]
- DTO Correspondente: [COLAR AQUI A CLASSE DTO]
- Objeto de Filtro (se houver): [COLAR AQUI O POJO DE FILTRO]

```

---


# PROMPT DE CRIAÇÃO DE CONTROLLER

```
Atue como um Arquiteto de Software Sênior especializado em Spring Boot 3.3.4 (Java 21). Preciso que você crie uma nova classe Controller e sua respectiva classe de Testes Unitários, seguindo rigorosamente os mesmos padrões arquiteturais, de código e de testes aplicados na TelaController.

Por favor, siga estritamente os seguintes critérios:

1. **Arquitetura & Padrões do Controller:**
   - Use as anotações `@RestController`, `@RequestMapping("/api/v1/{recurso}")`, `@RequiredArgsConstructor` (Lombok) e `@Slf4j`.
   - Injeção de dependências estritamente via construtor (através do `@RequiredArgsConstructor`), delegando toda a regra de negócio e mapeamento exclusivamente para a respectiva `Facade`.
   - Adicione logs informativos (`log.info`) no início de cada método mapeado.

2. **Padronização de Respostas (ApiResponse):**
   - Todas as respostas de sucesso devem retornar `ResponseEntity<ApiResponse<T>>` ou `ResponseEntity<ApiResponse<List<T>>>` utilizando o wrapper `ApiResponse.success(...)`.
   - Repasses de status HTTP corretos: 
     - `201 CREATED` para operações de cadastro (`POST`).
     - `200 OK` para atualizações (`PUT`), listagens (`GET`), filtragens (`POST`) e exclusões (`DELETE`).
     - `404 NOT_FOUND` utilizando `.map(...).orElseGet(...)` para buscas por ID ou atributos únicos específicos (como "por-nome") quando o recurso não for localizado.
   - Sempre repasse `request.getRequestURI()` para o método de criação do `ApiResponse`.

3. **Classe de Testes Unitários correspondente:**
   - Utilize `@WebMvcTest(NomeDoController.class)`.
   - Utilize `@Autowired private MockMvc mockMvc;`, `@Autowired private ObjectMapper objectMapper;` e `@MockBean private NomeDaFacade facade;`.
   - Escreva testes unitários cobrindo todos os fluxos de sucesso e exceção/retorno 404 (quando aplicável), validando status HTTP, mensagens da `ApiResponse`, o JSON de dados retornados (`jsonPath`) e o caminho da requisição (`path`).
   - Utilize `@DisplayName` descritivos e asserções limpas com Mockito (`verify`, `when`).

Aqui estão os detalhes da entidade/módulo que preciso implementar agora:
- **Nome do Recurso / Entidade:** [INSERIR NOME AQUI, ex: Cliente, Produto, Usuario]
- **Endpoints desejados:** [EX: Salvar, Atualizar, Deletar por ID, Buscar por ID, Listar todos, etc.]
```

---


# Boas Práticas: Carregamento de Relacionamentos LAZY (Spring Data JPA)

Documentação de orientação para o gerenciamento de relacionamentos `FetchType.LAZY` no ecossistema Spring Data JPA e Hibernate 6.x.

---

## Sumário

* [Visão Geral](https://www.google.com/search?q=%23vis%C3%A3o-geral)
* [1. Padrão Service com @Transactional(readOnly = true)](https://www.google.com/search?q=%231-padr%C3%A3o-service-com-transactionalreadonly--true)
* [2. Padrão DTO Projections (Interface Projections)](https://www.google.com/search?q=%232-padr%C3%A3o-dto-projections-interface-projections)
* [Guia Rápido de Decisão](https://www.google.com/search?q=%23guia-r%C3%A1pido-de-decis%C3%A3o)

---

## Visão Geral

Com a padronização do mapeamento de entidades utilizando `FetchType.LAZY` para otimização de consultas e prevenção do problema de *N+1 SELECTs*, o acesso tardio a atributos associados fora de um contexto de persistência ativo pode disparar exceções do tipo `LazyInitializationException`.

Este guia estabelece as abordagens recomendadas para consulta e manipulação de grafos de objetos na camada de serviço e API.

---

## 1. Padrão Service com `@Transactional(readOnly = true)`

Indicado para fluxos da camada de negócio em que a entidade domain precisa ser manipulada integralmente.

### Funcionamento

A anotação `@Transactional(readOnly = true)` mantém o `EntityManager` e a conexão do banco abertos durante toda a execução do método no `@Service`. Quando um getter de relacionamento `LAZY` é invocado, o Hibernate realiza o carregamento sob demanda (*lazy loading*) transparente na mesma sessão.

### Exemplo de Implementação

```java
package br.com.srcsoftware.atlantis.atlantis.service;

import br.com.srcsoftware.atlantis.atlantis.entity.modular.manager.usuario.UsuarioPO;
import br.com.srcsoftware.atlantis.atlantis.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public String obterNomeEmpresaDoUsuario(Long idUsuario) {
        UsuarioPO usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + idUsuario));

        if (usuario.getEmpresa() != null) {
            return usuario.getEmpresa().getRazaoSocial();
        }

        return "Sem empresa associada";
    }
}

```

#### Vantagens:

* Preserva o ciclo de vida completo da entidade para regras de negócio.
* O atributo `readOnly = true` otimiza o contexto de persistência desativando a checagem de alteração de estado (*dirty checking*).

---

## 2. Padrão DTO Projections (Interface Projections)

Indicado para operações de leitura, relatórios, tabelas de listagem e endpoints REST que exigem alto desempenho.

### Funcionamento

O Spring Data JPA projeta os resultados diretamente da consulta SQL para uma interface Java. O Hibernate compõe a instrução SQL buscando apenas as colunas estritamente declaradas nos métodos da interface, eliminando a carga de entidades não utilizadas na memória da JVM.

### Exemplo de Implementação

#### A. Definindo a Interface de Projeção:

```java
package br.com.srcsoftware.atlantis.atlantis.repository.projection;

public interface UsuarioResumoProjection {

    Long getId();

    String getNome();

    // Mapeamento automático via aliasing do Spring Data para o campo da entidade relacionada
    String getEmpresaRazaoSocial();
}

```

#### B. Declarando o Método no Repository:

```java
package br.com.srcsoftware.atlantis.atlantis.repository;

import br.com.srcsoftware.atlantis.atlantis.entity.modular.manager.usuario.UsuarioPO;
import br.com.srcsoftware.atlantis.atlantis.repository.projection.UsuarioResumoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<UsuarioPO, Long> {

    List<UsuarioResumoProjection> findByAtivoTrue();
}

```

#### C. Consumindo na Camada REST Controller:

```java
package br.com.srcsoftware.atlantis.atlantis.controller;

import br.com.srcsoftware.atlantis.atlantis.repository.UsuarioRepository;
import br.com.srcsoftware.atlantis.atlantis.repository.projection.UsuarioResumoProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/resumo")
    public ResponseEntity<List<UsuarioResumoProjection>> listarResumoAtivos() {
        List<UsuarioResumoProjection> lista = usuarioRepository.findByAtivoTrue();
        return ResponseEntity.ok(lista);
    }
}

```

#### Vantagens:

* **Desempenho Otimizado:** Executa projeção SQL enxuta diretamente no banco de dados.
* **Isolamento de Estado:** Isento do lançamento de `LazyInitializationException`.
* **Clean Code:** Elimina a necessidade de conversores/mapeadores manuais (`EntityMapper`) para consultas de leitura.

---

## Guia Rápido de Decisão

| Cenário de Uso | Ação do Método / Camada | Estratégia Recomendada |

* **Alteração de estado, persistência e regras de negócio** Use `@Transactional` *(readOnly = false)* no **Service** |
* **Consulta com necessidade do objeto completo e grafos em memória** Use `@Transactional(readOnly = true)` no **Service** |
* **Leitura de Alta Performance / Endpoints REST, busca, paginação e relatórios** Use Interface Projections (DTOs) no Repository |

---

## Criação do BANCO DE DADOS
```
CREATE SCHEMA `atlantis` DEFAULT CHARACTER SET utf8 ;
```
---
