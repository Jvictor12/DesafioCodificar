package api.desafio.codificar.Backend.Service;

import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.Exceptions.ResourceNotFoundException;
import api.desafio.codificar.Backend.Records.ResponsavelRecord;
import api.desafio.codificar.Backend.Repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ResponsavelService extends AbstractService {

    private final ResponsavelRepository repository;

    @Transactional(readOnly = true)
    public ResponsavelRecord findById(UUID id) {
        return toResponsavelRecord(repository.findById(id).orElseThrow( ()->
            new ResourceNotFoundException("Responsável não encontrado no sistema")
        ));
    }

    @Transactional(readOnly = true)
    public Page<ResponsavelRecord> findAll(Integer page, Integer size, String sort, String direction) {
        final var pageableSort = buildSort(sort, direction);
        final var pageable = buildPageable(page, size, pageableSort);
        final var responsaveis = repository.findAll(pageable);
        return responsaveis.map(this::toResponsavelRecord);
    }

    @Transactional(readOnly = true)
    public UUID atribuirResponsavel() {
        final var responsavel = repository.findResponsavelComMenosChamado();
        if (responsavel == null) {
            throw new ResourceNotFoundException("Nenhum responsavel encontrado no sistema");
        }
        return responsavel.getId();
    }

    @Transactional
    public ResponsavelRecord save(Responsavel responsavel) {
        return toResponsavelRecord(repository.save(responsavel));
    }

    @Transactional
    public ResponsavelRecord update(UUID id, Responsavel responsavel) {

        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Responsavel não encontrado");
        }

        return toResponsavelRecord(repository.save(responsavel));
    }

    @Transactional
    public void delete(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    private ResponsavelRecord toResponsavelRecord(Responsavel responsavel) {
        return new ResponsavelRecord(
                responsavel.getId(),
                responsavel.getNome(),
                responsavel.getChamados()
        );
    }
}
