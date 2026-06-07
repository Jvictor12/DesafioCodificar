package api.desafio.codificar.Backend.Service;

import api.desafio.codificar.Backend.Entity.Chamado;
import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.Exceptions.BusinessException;
import api.desafio.codificar.Backend.Exceptions.ResourceNotFoundException;
import api.desafio.codificar.Backend.Records.ResponsavelRecord;
import api.desafio.codificar.Backend.Repository.ChamadoRepository;
import api.desafio.codificar.Backend.Repository.ResponsavelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceTests {

    @Mock
    private ChamadoRepository chamadoRepository;
    @Mock
    private ResponsavelRepository responsavelRepository;
    @InjectMocks
    private ChamadoService chamadoService;
    @InjectMocks
    private ResponsavelService responsavelService;

    @Test
    void chamadoFindByIdReturnsEntityAndThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        Chamado chamado = new Chamado();
        when(chamadoRepository.findById(id)).thenReturn(Optional.of(chamado));

        assertSame(chamado, chamadoService.findById(id));

        when(chamadoRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> chamadoService.findById(id));
    }

    @Test
    void chamadoFindAllBuildsPagedAndUnpagedRequests() {
        when(chamadoRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        chamadoService.findAll(2, 5, "titulo", "desc");
        chamadoService.findAll(-1, -1, "titulo", "asc");

        verify(chamadoRepository, times(2)).findAll(any(Pageable.class));
    }

    @Test
    void chamadoSaveUpdateAndDeleteDelegateToRepository() {
        UUID id = UUID.randomUUID();
        Chamado chamado = new Chamado();
        when(chamadoRepository.save(chamado)).thenReturn(chamado);

        assertSame(chamado, chamadoService.save(chamado));
        when(chamadoRepository.existsById(id)).thenReturn(true);
        assertSame(chamado, chamadoService.update(id, chamado));
        assertEquals(id, chamado.getId());

        chamadoService.delete(id);
        verify(chamadoRepository).deleteById(id);
    }

    @Test
    void chamadoUpdateThrowsWhenEntityDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(chamadoRepository.existsById(id)).thenReturn(false);

        assertThrows(BusinessException.class, () -> chamadoService.update(id, new Chamado()));
        verify(chamadoRepository, never()).save(any());
    }

    @Test
    void responsavelFindByIdMapsEntityAndThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        Responsavel responsavel = responsavel(id);
        when(responsavelRepository.findById(id)).thenReturn(Optional.of(responsavel));

        ResponsavelRecord result = responsavelService.findById(id);
        assertEquals(id, result.id());
        assertEquals("Nome", result.nome());
        assertSame(responsavel.getChamados(), result.chamados());

        when(responsavelRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> responsavelService.findById(id));
    }

    @Test
    void responsavelFindAllMapsPage() {
        Responsavel responsavel = responsavel(UUID.randomUUID());
        when(responsavelRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(responsavel)));

        var result = responsavelService.findAll(0, 10, "nome", "asc");

        assertEquals(1, result.getTotalElements());
        assertEquals(responsavel.getId(), result.getContent().getFirst().id());
    }

    @Test
    void atribuirResponsavelReturnsIdAndThrowsWhenNoneExists() {
        Responsavel responsavel = responsavel(UUID.randomUUID());
        when(responsavelRepository.findResponsavelComMenosChamado()).thenReturn(responsavel);
        assertEquals(responsavel.getId(), responsavelService.atribuirResponsavel());

        when(responsavelRepository.findResponsavelComMenosChamado()).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, responsavelService::atribuirResponsavel);
    }

    @Test
    void responsavelSaveUpdateAndDeleteCoverAllBranches() {
        UUID id = UUID.randomUUID();
        Responsavel responsavel = responsavel(id);
        when(responsavelRepository.save(responsavel)).thenReturn(responsavel);

        assertEquals(id, responsavelService.save(responsavel).id());
        when(responsavelRepository.existsById(id)).thenReturn(true);
        assertEquals(id, responsavelService.update(id, responsavel).id());
        responsavelService.delete(id);
        verify(responsavelRepository).deleteById(id);

        UUID missingId = UUID.randomUUID();
        when(responsavelRepository.existsById(missingId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> responsavelService.update(missingId, responsavel));
        responsavelService.delete(missingId);
        verify(responsavelRepository, never()).deleteById(missingId);
    }

    @Test
    void abstractServiceBuildsSortAndBothPageableForms() {
        TestService service = new TestService();
        Sort sort = service.sort("nome", "desc");

        assertEquals(Sort.Direction.DESC, sort.getOrderFor("nome").getDirection());
        assertEquals(3, service.pageable(3, 7, sort).getPageNumber());
        assertEquals(Integer.MAX_VALUE, service.pageable(-1, -1, sort).getPageSize());
        assertEquals(Integer.MAX_VALUE, service.pageable(0, -1, sort).getPageSize());
    }

    private Responsavel responsavel(UUID id) {
        Responsavel responsavel = Responsavel.builder()
                .nome("Nome")
                .cpf("20431784582")
                .chamados(List.of(new Chamado()))
                .build();
        responsavel.setId(id);
        return responsavel;
    }

    private static class TestService extends AbstractService {
        Sort sort(String field, String direction) {
            return buildSort(field, direction);
        }

        Pageable pageable(Integer page, Integer size, Sort sort) {
            return buildPageable(page, size, sort);
        }
    }
}
