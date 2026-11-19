package br.com.srcsoftware.api.atlantis.service.usuario.tipousuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.srcsoftware.api.atlantis.dto.usuario.tipousuario.TipoUsuarioDTO;
import br.com.srcsoftware.api.atlantis.entity.usuario.tipousuario.TipoUsuarioPO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.exceptions.ModelFactoryException;
import br.com.srcsoftware.api.atlantis.repository.usuario.tipousuario.TipoUsuarioRepository;
import br.com.srcsoftware.api.atlantis.utilidades.ModelFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipoUsuarioService {
	
	@Value("${app.paginacao.tamanho-padrao:10}")
    private int tamanhoPadraoPagina;

    private final TipoUsuarioRepository repository;
    private final ModelFactory<TipoUsuarioPO, TipoUsuarioDTO> modelFactory;
    
    @Transactional
    public TipoUsuarioDTO salvar(TipoUsuarioDTO dto) throws ApplicationException {
        log.info("Iniciando salvamento de TipoUsuarioDTO: {}", dto);
        try {
            TipoUsuarioPO TipoUsuario = modelFactory.getPO(TipoUsuarioPO.class, dto);
            modelFactory.validarNotNull(TipoUsuario);
            
            TipoUsuarioPO TipoUsuarioSalva = repository.save(TipoUsuario);
            log.info("TipoUsuarioPO salva com sucesso: {}", TipoUsuarioSalva);
            
            return modelFactory.getDTO(TipoUsuarioDTO.class, TipoUsuarioSalva);
        } catch (ApplicationException e) {
            log.error("Erro de validação ao salvar TipoUsuarioPO: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Erro de violação de integridade ao salvar TipoUsuarioPO: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro inesperado ao converter DTO em PO ou PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter DTO em PO ou PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao salvar TipoUsuarioPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao salvar TipoUsuario: " + e.getMessage(), e);
        }
    }

    @Transactional
    public TipoUsuarioDTO atualizar(TipoUsuarioDTO dto)  {
        log.info("Iniciando atualização de TipoUsuarioPO via DTO: {}", dto);
        try {
            if (dto.getId() == null || dto.getId().isBlank()) {
                throw new ApplicationException("O ID da TipoUsuario é obrigatório para atualização!");
            }

            TipoUsuarioPO TipoUsuario = modelFactory.getPO(TipoUsuarioPO.class, dto);
            modelFactory.validarNotNull(TipoUsuario);
            
            if (!repository.existsById(TipoUsuario.getId())) {
                throw new ApplicationException("TipoUsuario não encontrada com o ID: " + dto.getId());
            }

            TipoUsuarioPO TipoUsuarioAtualizada = repository.save(TipoUsuario);
            log.info("TipoUsuarioPO atualizada com sucesso: {}", TipoUsuarioAtualizada);
            
            return modelFactory.getDTO(TipoUsuarioDTO.class, TipoUsuarioAtualizada);
        } catch (ApplicationException e) {
            log.error("Erro de validação ao atualizar TipoUsuarioPO: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Erro de violação de integridade ao atualizar TipoUsuarioPO: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro inesperado ao converter DTO em PO ou PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter DTO em PO ou PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar TipoUsuarioPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao atualizar TipoUsuario: " + e.getMessage(), e);
        }
    }
      

    @Transactional
    public void deletar(String id) throws ApplicationException {
        log.info("Iniciando exclusão de TipoUsuarioPO com ID: {}", id);
        try {
            if (id == null || id.isBlank()) {
                throw new ApplicationException("O ID da TipoUsuario é obrigatório para exclusão!");
            }
            
            Long idLong = Long.valueOf(id);
            TipoUsuarioPO existente = repository.findById(idLong)
                .orElseThrow(() -> new ApplicationException("TipoUsuario não encontrada para exclusão com o ID: " + id));

            repository.delete(existente);
            log.info("TipoUsuarioPO excluída com sucesso com ID: {}", id);
            
        } catch (ApplicationException e) {
            log.error("Erro ao excluir TipoUsuarioPO: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido para exclusão: {}", id);
            throw new ApplicationException("O ID informado é inválido: " + id, e);
        } catch (Exception e) {
            log.error("Erro inesperado ao excluir TipoUsuarioPO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao excluir TipoUsuario: " + e.getMessage(), e);
        }
    }

    public Optional<TipoUsuarioDTO> buscarPorId(String id) throws ApplicationException {
        log.info("Buscando TipoUsuarioPO por ID: {}", id);
        try {
            if (id == null || id.isBlank()) {
                throw new ApplicationException("O ID da TipoUsuario não pode ser nulo!");
            }
            
            Long idLong = Long.valueOf(id);
            Optional<TipoUsuarioPO> resultado = repository.findById(idLong);
            
            log.info("Resultado da busca por ID [{}]: {}", id, resultado.orElse(null));
            
            if (resultado.isEmpty()) {
                return Optional.empty();
            }
            
            TipoUsuarioDTO dto = modelFactory.getDTO(TipoUsuarioDTO.class, resultado.get());
            return Optional.of(dto);
            
        } catch (ApplicationException e) {
            log.error("Erro de validação ao buscar TipoUsuarioPO por ID: {}", e.getMessage());
            throw e;
        } catch (NumberFormatException e) {
            log.error("Formato de ID inválido para busca: {}", id);
            throw new ApplicationException("O ID informado é inválido: " + id, e);
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar TipoUsuarioPO por ID: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao buscar TipoUsuario por ID: " + e.getMessage(), e);
        }
    }

    public Optional<TipoUsuarioDTO> buscarPorNome(String nome) throws ApplicationException {
        log.info("Buscando TipoUsuarioPO por nome exato: {}", nome);
        try {
            if (nome == null || nome.isBlank()) {
                throw new ApplicationException("O nome da TipoUsuario é obrigatório para a consulta!");
            }
            
            Optional<TipoUsuarioPO> resultado = repository.findByNome(nome);
            log.info("Resultado da busca por nome [{}]: {}", nome, resultado.orElse(null));
            
            if (resultado.isEmpty()) {
                return Optional.empty();
            }
            
            TipoUsuarioDTO dto = modelFactory.getDTO(TipoUsuarioDTO.class, resultado.get());
            return Optional.of(dto);
            
        } catch (ApplicationException e) {
            log.error("Erro de validação ao buscar TipoUsuarioPO por nome: {}", e.getMessage());
            throw e;
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter PO em DTO: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter PO em DTO: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar TipoUsuarioPO por nome: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao buscar TipoUsuario por nome: " + e.getMessage(), e);
        }
    }

    public List<TipoUsuarioDTO> listarTodas() throws ApplicationException {
        log.info("Listando todas as instâncias de TipoUsuarioPO");
        try {
            List<TipoUsuarioPO> resultados = repository.findAll();
            log.info("Total de registros encontrados: {}", resultados.size());
            
            ArrayList<TipoUsuarioDTO> dtosRetorno = new ArrayList<>();
            for (TipoUsuarioPO poCorrente : resultados) {
                dtosRetorno.add(this.modelFactory.getDTO(TipoUsuarioDTO.class, poCorrente));
            }

            return dtosRetorno;          
        } catch (ModelFactoryException e) {
            log.error("Erro ao converter POs em DTOs: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao converter lista de POs em DTOs: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Erro ao listar todas as TipoUsuarios: {}", e.getMessage(), e);
            throw new ApplicationException("Erro ao listar TipoUsuarios: " + e.getMessage(), e);
        }
    }
            
}