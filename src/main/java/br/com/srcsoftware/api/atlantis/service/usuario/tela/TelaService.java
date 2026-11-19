package br.com.srcsoftware.api.atlantis.service.usuario.tela;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.srcsoftware.api.atlantis.dto.usuario.tela.TelaDTO;
import br.com.srcsoftware.api.atlantis.entity.usuario.tela.TelaPO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.exceptions.ModelFactoryException;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroPO;
import br.com.srcsoftware.api.atlantis.repository.usuario.tela.TelaRepository;
import br.com.srcsoftware.api.atlantis.utilidades.ModelFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TelaService {
	
	@Value("${app.paginacao.tamanho-padrao:10}")
    private int tamanhoPadraoPagina;

    private final TelaRepository repository;
    private final ModelFactory<TelaPO, TelaDTO> modelFactory;
    
    @Transactional
    public TelaDTO salvar(TelaDTO dto) throws ApplicationException {
        log.info("Iniciando salvamento de TelaDTO: {}", dto);
        try {
            TelaPO tela = modelFactory.getPO(TelaPO.class, dto);
            modelFactory.validarNotNull(tela);
            
            TelaPO telaSalva = repository.save(tela);
            log.info("TelaPO salva com sucesso: {}", telaSalva);
            
            return modelFactory.getDTO(TelaDTO.class, telaSalva);
        } catch (ApplicationException e) {
            log.error("Erro de validação ao salvar TelaPO: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Erro de violação de integridade ao salvar TelaPO: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro inesperado ao converter DTO em PO ou PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter DTO em PO ou PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao salvar TelaPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao salvar Tela: " + e.getMessage(), e);
        }
    }

    @Transactional
    public TelaDTO atualizar(TelaDTO dto)  {
        log.info("Iniciando atualização de TelaPO via DTO: {}", dto);
        try {
            if (dto.getId() == null || dto.getId().isBlank()) {
                throw new ApplicationException("O ID da tela é obrigatório para atualização!");
            }

            TelaPO tela = modelFactory.getPO(TelaPO.class, dto);
            modelFactory.validarNotNull(tela);
            
            if (!repository.existsById(tela.getId())) {
                throw new ApplicationException("Tela não encontrada com o ID: " + dto.getId());
            }

            TelaPO telaAtualizada = repository.save(tela);
            log.info("TelaPO atualizada com sucesso: {}", telaAtualizada);
            
            return modelFactory.getDTO(TelaDTO.class, telaAtualizada);
        } catch (ApplicationException e) {
            log.error("Erro de validação ao atualizar TelaPO: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Erro de violação de integridade ao atualizar TelaPO: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro inesperado ao converter DTO em PO ou PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter DTO em PO ou PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar TelaPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao atualizar Tela: " + e.getMessage(), e);
        }
    }
      

    @Transactional
    public void deletar(String id) throws ApplicationException {
        log.info("Iniciando exclusão de TelaPO com ID: {}", id);
        try {
            if (id == null || id.isBlank()) {
                throw new ApplicationException("O ID da tela é obrigatório para exclusão!");
            }
            
            Long idLong = Long.valueOf(id);
            TelaPO existente = repository.findById(idLong)
                .orElseThrow(() -> new ApplicationException("Tela não encontrada para exclusão com o ID: " + id));

            repository.delete(existente);
            log.info("TelaPO excluída com sucesso com ID: {}", id);
            
        } catch (ApplicationException e) {
            log.error("Erro ao excluir TelaPO: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido para exclusão: {}", id);
            throw new ApplicationException("O ID informado é inválido: " + id, e);
        } catch (Exception e) {
            log.error("Erro inesperado ao excluir TelaPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao excluir Tela: " + e.getMessage(), e);
        }
    }

    public Optional<TelaDTO> buscarPorId(String id) throws ApplicationException {
        log.info("Buscando TelaPO por ID: {}", id);
        try {
            if (id == null || id.isBlank()) {
                throw new ApplicationException("O ID da tela não pode ser nulo!");
            }
            
            Long idLong = Long.valueOf(id);
            Optional<TelaPO> resultado = repository.findById(idLong);
            
            log.info("Resultado da busca por ID [{}]: {}", id, resultado.orElse(null));
            
            if (resultado.isEmpty()) {
                return Optional.empty();
            }
            
            TelaDTO dto = modelFactory.getDTO(TelaDTO.class, resultado.get());
            return Optional.of(dto);
            
        } catch (ApplicationException e) {
            log.error("Erro de validação ao buscar TelaPO por ID: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido para busca: {}", id);
            throw new ApplicationException("O ID informado é inválido: " + id, e);
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar TelaPO por ID: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao buscar Tela por ID: " + e.getMessage(), e);
        }
    }

    public Optional<TelaDTO> buscarPorNome(String nome) throws ApplicationException {
        log.info("Buscando TelaPO por nome exato: {}", nome);
        try {
            if (nome == null || nome.isBlank()) {
                throw new ApplicationException("O nome da tela é obrigatório para a consulta!");
            }
            
            Optional<TelaPO> resultado = repository.findByNome(nome);
            log.info("Resultado da busca por nome [{}]: {}", nome, resultado.orElse(null));
            
            if (resultado.isEmpty()) {
                return Optional.empty();
            }
            
            TelaDTO dto = modelFactory.getDTO(TelaDTO.class, resultado.get());
            return Optional.of(dto);
            
        } catch (ApplicationException e) {
            log.error("Erro de validação ao buscar TelaPO por nome: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar TelaPO por nome: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao buscar Tela por nome: " + e.getMessage(), e);
        }
    }

    public List<TelaDTO> listarTodas() throws ApplicationException {
        log.info("Listando todas as instâncias de TelaPO");
        try {
            List<TelaPO> resultados = repository.findAll();
            log.info("Total de registros encontrados: {}", resultados.size());
            
            ArrayList<TelaDTO> dtosRetorno = new ArrayList<>();
            for (TelaPO poCorrente : resultados) {
                dtosRetorno.add(this.modelFactory.getDTO(TelaDTO.class, poCorrente));
            }

            return dtosRetorno;          
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter POs em DTOs: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter lista de POs em DTOs: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro ao listar todas as telas: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao listar telas: " + e.getMessage(), e);
        }
    }
        
    public Page<TelaDTO> buscarPorFiltroPaginado(AbstractFiltroDTO<TelaDTO> filtroDTO) throws ApplicationException {
        log.info("Service: Buscando telas por filtro dinâmico com paginação: {}", filtroDTO);
        try {
            TelaPO filtroPoObj = (filtroDTO != null && filtroDTO.getObjeto() != null)
                    ? modelFactory.getPO(TelaPO.class, filtroDTO.getObjeto())
                    : null;

            AbstractFiltroPO<TelaPO> filtroPO = new AbstractFiltroPO<TelaPO>();
            filtroPO.setObjeto(filtroPoObj);

            // Extrai a página e o tamanho enviados pelo front-end (com fallback seguro caso venha nulo)
            int pagina = (filtroDTO != null) ? filtroDTO.getPagina() : 0;
            int tamanho = (filtroDTO != null && filtroDTO.getTamanho() > 0) ? filtroDTO.getTamanho() : tamanhoPadraoPagina;
            
            Pageable pageable = PageRequest.of(pagina, tamanho);

            // Executa a busca paginada no repositório
            Page<TelaPO> paginaPO = repository.findByFiltroPaginado(filtroPO, pageable);

            // Converte a lista de POs da página atual em DTOs
            List<TelaDTO> dtosRetorno = new ArrayList<>();
            for (TelaPO poCorrente : paginaPO.getContent()) {
                dtosRetorno.add(this.modelFactory.getDTO(TelaDTO.class, poCorrente));
            }

            log.info("Total de registros encontrados: {}, Página atual: {}, Total de páginas: {}", 
                    paginaPO.getTotalElements(), paginaPO.getNumber(), paginaPO.getTotalPages());

            // Retorna o Page estruturado contendo a lista convertida e os metadados de paginação
            return new PageImpl<>(dtosRetorno, pageable, paginaPO.getTotalElements());

        } catch (ModelFactoryException e) {
            log.error("Erro ao converter POs filtrados paginados em DTOs: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter telas filtradas: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao filtrar Telas com paginação: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao filtrar telas: " + e.getMessage(), e);
        }
    }
}