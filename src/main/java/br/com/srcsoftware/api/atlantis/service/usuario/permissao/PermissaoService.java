package br.com.srcsoftware.api.atlantis.service.usuario.permissao;

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

import br.com.srcsoftware.api.atlantis.dto.usuario.permissao.PermissaoDTO;
import br.com.srcsoftware.api.atlantis.entity.usuario.permissao.PermissaoPO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.exceptions.ModelFactoryException;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroPO;
import br.com.srcsoftware.api.atlantis.repository.usuario.permissao.PermissaoRepository;
import br.com.srcsoftware.api.atlantis.utilidades.ModelFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PermissaoService {
	
	@Value("${app.paginacao.tamanho-padrao:10}")
    private int tamanhoPadraoPagina;
	
    private final PermissaoRepository repository;
    private final ModelFactory<PermissaoPO, PermissaoDTO> modelFactory;

    @Transactional
    public PermissaoDTO salvar(PermissaoDTO dto) throws ApplicationException {
        log.info("Iniciando salvamento de PermissaoDTO: {}", dto);
        try {            
            
            PermissaoPO permissao = modelFactory.getPO(PermissaoPO.class, dto);
            modelFactory.validarNotNull(permissao);
            
            PermissaoPO permissaoSalva = repository.save(permissao);
            log.info("PermissaoPO salva com sucesso: {}", permissaoSalva);
            
            return modelFactory.getDTO(PermissaoDTO.class, permissaoSalva);
        } catch (ApplicationException e) {
            log.error("Erro de validação ao salvar PermissaoPO: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Erro de violação de integridade ao salvar PermissaoPO: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro inesperado ao converter DTO em PO ou PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter DTO em PO ou PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao salvar PermissaoPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao salvar Permissao: " + e.getMessage(), e);
        }
    }

    @Transactional
    public PermissaoDTO atualizar(PermissaoDTO dto) throws ApplicationException {
        log.info("Iniciando atualização de PermissaoPO via DTO: {}", dto);
        try {

            if (dto.getId() == null || dto.getId().isBlank()) {
                throw new ApplicationException("O ID da permissao é obrigatório para atualização!");
            }

            PermissaoPO permissao = modelFactory.getPO(PermissaoPO.class, dto);
            modelFactory.validarNotNull(permissao);
            
            if (!repository.existsById(permissao.getId())) {
                throw new ApplicationException("Permissao não encontrada com o ID: " + dto.getId());
            }

            PermissaoPO permissaoAtualizada = repository.save(permissao);
            log.info("PermissaoPO atualizada com sucesso: {}", permissaoAtualizada);
            
            return modelFactory.getDTO(PermissaoDTO.class, permissaoAtualizada);
        } catch (ApplicationException e) {
            log.error("Erro de validação ao atualizar PermissaoPO: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Erro de violação de integridade ao atualizar PermissaoPO: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro inesperado ao converter DTO em PO ou PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter DTO em PO ou PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar PermissaoPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao atualizar Permissao: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deletar(String id) throws ApplicationException {
        log.info("Iniciando exclusão de PermissaoPO com ID: {}", id);
        try {
            if (id == null || id.isBlank()) {
                throw new ApplicationException("O ID da permissao é obrigatório para exclusão!");
            }
            
            Long idLong = Long.valueOf(id);
            PermissaoPO existente = repository.findById(idLong)
                .orElseThrow(() -> new ApplicationException("Permissao não encontrada para exclusão com o ID: " + id));

            repository.delete(existente);
            log.info("PermissaoPO excluída com sucesso com ID: {}", id);
            
        } catch (ApplicationException e) {
            log.error("Erro ao excluir PermissaoPO: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido para exclusão: {}", id);
            throw new ApplicationException("O ID informado é inválido: " + id, e);
        } catch (Exception e) {
            log.error("Erro inesperado ao excluir PermissaoPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao excluir Permissao: " + e.getMessage(), e);
        }
    }

    public Optional<PermissaoDTO> buscarPorId(String id) throws ApplicationException {
        log.info("Buscando PermissaoPO por ID: {}", id);
        try {
            if (id == null || id.isBlank()) {
                throw new ApplicationException("O ID da permissao não pode ser nulo!");
            }
            
            Long idLong = Long.valueOf(id);
            Optional<PermissaoPO> resultado = repository.findById(idLong);
            
            log.info("Resultado da busca por ID [{}]: {}", id, resultado.orElse(null));
            
            if (resultado.isEmpty()) {
                return Optional.empty();
            }
            
            PermissaoDTO dto = modelFactory.getDTO(PermissaoDTO.class, resultado.get());
            return Optional.of(dto);
            
        } catch (ApplicationException e) {
            log.error("Erro de validação ao buscar PermissaoPO por ID: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido para busca: {}", id);
            throw new ApplicationException("O ID informado é inválido: " + id, e);
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar PermissaoPO por ID: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao buscar Permissao por ID: " + e.getMessage(), e);
        }
    }

    public Optional<PermissaoDTO> buscarPorAcao(String acao) throws ApplicationException {
        log.info("Buscando PermissaoPO por acao exato: {}", acao);
        try {
            if (acao == null || acao.isBlank()) {
                throw new ApplicationException("O acao da permissao é obrigatório para a consulta!");
            }
            
            Optional<PermissaoPO> resultado = repository.findByAcao(acao);
            log.info("Resultado da busca por acao [{}]: {}", acao, resultado.orElse(null));
            
            if (resultado.isEmpty()) {
                return Optional.empty();
            }
            
            PermissaoDTO dto = modelFactory.getDTO(PermissaoDTO.class, resultado.get());
            return Optional.of(dto);
            
        } catch (ApplicationException e) {
            log.error("Erro de validação ao buscar PermissaoPO por acao: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar PermissaoPO por acao: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao buscar Permissao por acao: " + e.getMessage(), e);
        }
    }

    public List<PermissaoDTO> listarTodas() throws ApplicationException {
        log.info("Listando todas as instâncias de PermissaoPO");
        try {
            List<PermissaoPO> resultados = repository.findAll();
            log.info("Total de registros encontrados: {}", resultados.size());
            
            ArrayList<PermissaoDTO> dtosRetorno = new ArrayList<>();
            for (PermissaoPO poCorrente : resultados) {
                dtosRetorno.add(this.modelFactory.getDTO(PermissaoDTO.class, poCorrente));
            }

            return dtosRetorno;          
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter POs em DTOs: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter lista de POs em DTOs: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro ao listar todas as permissaos: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao listar permissaos: " + e.getMessage(), e);
        }
    }

    public List<PermissaoDTO> buscarPorFiltro(AbstractFiltroPO<PermissaoDTO> filtro) throws ApplicationException {
        log.info("Buscando Permissaos por filtro dinâmico: {}", filtro);
        try {
        	        
        	PermissaoPO filtroPoObj = (filtro != null && filtro.getObjeto() != null)
                    ? modelFactory.getPO(PermissaoPO.class, filtro.getObjeto())
                    : null;

            AbstractFiltroPO<PermissaoPO> filtroPO = new AbstractFiltroPO<PermissaoPO>();
            filtroPO.setObjeto(filtroPoObj);
        	
            List<PermissaoPO> resultados = repository.findByFiltro(filtroPO);
            log.info("Total de registros encontrados pelo filtro: {}", resultados.size());
            
            ArrayList<PermissaoDTO> dtosRetorno = new ArrayList<>();
            for (PermissaoPO poCorrente : resultados) {
                dtosRetorno.add(this.modelFactory.getDTO(PermissaoDTO.class, poCorrente));
            }

            return dtosRetorno;    
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter POs filtrados em DTOs: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter POs filtrados em DTOs: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao filtrar Permissaos: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao filtrar Permissaos: " + e.getMessage(), e);
        }
    }
    
    public Page<PermissaoDTO> buscarPorFiltroPaginado(AbstractFiltroDTO<PermissaoDTO> filtroDTO) throws ApplicationException {
        log.info("Service: Buscando permissaos por filtro dinâmico com paginação: {}", filtroDTO);
        try {
            PermissaoPO filtroPoObj = (filtroDTO != null && filtroDTO.getObjeto() != null)
                    ? modelFactory.getPO(PermissaoPO.class, filtroDTO.getObjeto())
                    : null;

            AbstractFiltroPO<PermissaoPO> filtroPO = new AbstractFiltroPO<PermissaoPO>();
            filtroPO.setObjeto(filtroPoObj);

            // Extrai a página e o tamanho enviados pelo front-end (com fallback seguro caso venha nulo)
            int pagina = (filtroDTO != null) ? filtroDTO.getPagina() : 0;
            int tamanho = (filtroDTO != null && filtroDTO.getTamanho() > 0) ? filtroDTO.getTamanho() : tamanhoPadraoPagina;
            
            Pageable pageable = PageRequest.of(pagina, tamanho);

            // Executa a busca paginada no repositório
            Page<PermissaoPO> paginaPO = repository.findByFiltroPaginado(filtroPO, pageable);

            // Converte a lista de POs da página atual em DTOs
            List<PermissaoDTO> dtosRetorno = new ArrayList<>();
            for (PermissaoPO poCorrente : paginaPO.getContent()) {
                dtosRetorno.add(this.modelFactory.getDTO(PermissaoDTO.class, poCorrente));
            }

            log.info("Total de registros encontrados: {}, Página atual: {}, Total de páginas: {}", 
                    paginaPO.getTotalElements(), paginaPO.getNumber(), paginaPO.getTotalPages());

            // Retorna o Page estruturado contendo a lista convertida e os metadados de paginação
            return new PageImpl<>(dtosRetorno, pageable, paginaPO.getTotalElements());

        } catch (ModelFactoryException e) {
            log.error("Erro ao converter POs filtrados paginados em DTOs: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter permissoes filtradas: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao filtrar Permissaos com paginação: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao filtrar permissoes: " + e.getMessage(), e);
        }
    }    
}