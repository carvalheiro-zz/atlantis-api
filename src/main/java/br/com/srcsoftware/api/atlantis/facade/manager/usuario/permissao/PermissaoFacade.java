package br.com.srcsoftware.api.atlantis.facade.manager.usuario.permissao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.srcsoftware.api.atlantis.dto.manager.usuario.permissao.PermissaoDTO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.repository.pojo.manager.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.atlantis.service.manager.usuario.permissao.PermissaoService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissaoFacade {

    private final PermissaoService permissaoService;

    public PermissaoDTO salvar(PermissaoDTO dto) throws ApplicationException {
        return permissaoService.salvar(dto);
    }

    public PermissaoDTO atualizar(PermissaoDTO dto) throws ApplicationException {
        return permissaoService.atualizar(dto);
    }

    public void deletar(String id) throws ApplicationException {
        permissaoService.deletar(id);
    }

    public Optional<PermissaoDTO> buscarPorId(String id) throws ApplicationException {
        return permissaoService.buscarPorId(id);
    }

    public Optional<PermissaoDTO> buscarPorNome(String acao) throws ApplicationException {
        return permissaoService.buscarPorAcao(acao);
    }

    public List<PermissaoDTO> listarTodas() throws ApplicationException {
        return permissaoService.listarTodas();
    }

    public Page<PermissaoDTO> buscarPorFiltroPaginado(AbstractFiltroDTO<PermissaoDTO> filtroDTO) throws ApplicationException {
        return permissaoService.buscarPorFiltroPaginado(filtroDTO);
    }
}