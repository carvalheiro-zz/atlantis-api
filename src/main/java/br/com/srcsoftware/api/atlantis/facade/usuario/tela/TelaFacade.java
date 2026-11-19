package br.com.srcsoftware.api.atlantis.facade.usuario.tela;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.srcsoftware.api.atlantis.dto.usuario.tela.TelaDTO;
import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.atlantis.repository.pojo.filtro.abstracts.AbstractFiltroDTO;
import br.com.srcsoftware.api.atlantis.service.usuario.tela.TelaService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelaFacade {

    private final TelaService telaService;

    public TelaDTO salvar(TelaDTO dto) throws ApplicationException {
        return telaService.salvar(dto);
    }

    public TelaDTO atualizar(TelaDTO dto) throws ApplicationException {
        return telaService.atualizar(dto);
    }

    public void deletar(String id) throws ApplicationException {
        telaService.deletar(id);
    }

    public Optional<TelaDTO> buscarPorId(String id) throws ApplicationException {
        return telaService.buscarPorId(id);
    }

    public Optional<TelaDTO> buscarPorNome(String nome) throws ApplicationException {
        return telaService.buscarPorNome(nome);
    }

    public List<TelaDTO> listarTodas() throws ApplicationException {
        return telaService.listarTodas();
    }

    public Page<TelaDTO> buscarPorFiltroPaginado(AbstractFiltroDTO<TelaDTO> filtroDTO) throws ApplicationException {
        return telaService.buscarPorFiltroPaginado(filtroDTO);
    }
}