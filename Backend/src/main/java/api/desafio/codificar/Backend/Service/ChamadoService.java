package api.desafio.codificar.Backend.Service;

import api.desafio.codificar.Backend.Entity.Chamado;
import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.Exceptions.BusinessException;
import api.desafio.codificar.Backend.Exceptions.ResourceNotFoundException;
import api.desafio.codificar.Backend.Repository.ChamadoRepository;
import api.desafio.codificar.Backend.Repository.ResponsavelRepository;
import api.desafio.codificar.Backend.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChamadoService extends AbstractService{

    private final ChamadoRepository repository;
    private final ResponsavelRepository responsavelRepository;

    @Transactional(readOnly = true)
    public Chamado findById(UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Chamado não encontrado no sistema"));
    }

    @Transactional(readOnly = true)
    public Page<Chamado> findAll(Integer page, Integer size, String sort, String direction) {
        final var pageableSort = buildSort(sort, direction);
        final var pageable = buildPageable(page, size, pageableSort);
        return repository.findAll(pageable);
    }

    @Transactional
    public Chamado save(Chamado chamado) {

        return repository.save(chamado);
    }

    @Transactional
    public Chamado update(UUID id, Chamado chamado) {

        if (!repository.existsById(id)) throw new BusinessException("Chamado não encontrado no sistema");
        chamado.setId(id);
        return repository.save(chamado);
    }

    @Transactional
    public void delete(UUID id) {
        Optional<Chamado> chamado = repository.findById(id);

        if (chamado.isEmpty()) throw new BusinessException("Chamado não encontrado no sistema");

        if (chamado.get().getStatus().equals(Status.Fechado)) {
            repository.deleteById(id);
        } else {
            throw new BusinessException("Somente chamados fechados podem ser deletados");
        }
    }

}
