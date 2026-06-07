package api.desafio.codificar.Backend.Controller;

import api.desafio.codificar.Backend.Entity.Chamado;
import api.desafio.codificar.Backend.Service.ChamadoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chamados")
@Tag(name = "Autenticação", description = "CRUD de chamados")
public class ChamadoController {

    private final ChamadoService service;

    @GetMapping
    public ResponseEntity<Page<Chamado>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chamado> findById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Chamado> create(@Valid @RequestBody Chamado chamado) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(chamado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chamado>  update(@PathVariable UUID id, @Valid @RequestBody Chamado chamado) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, chamado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deletado com Sucesso!");
    }
}
