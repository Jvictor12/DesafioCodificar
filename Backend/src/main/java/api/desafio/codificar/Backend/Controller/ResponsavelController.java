package api.desafio.codificar.Backend.Controller;

import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.Records.ResponsavelRecord;
import api.desafio.codificar.Backend.Service.ResponsavelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/responsaveis")
public class ResponsavelController {

    private final ResponsavelService service;

    @GetMapping
    public ResponseEntity<Page<ResponsavelRecord>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll(page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsavelRecord> findById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(id));
    }

    @GetMapping("/atribuir")
    public ResponseEntity<UUID> atribuirResponsavel(){
        return ResponseEntity.status(HttpStatus.OK).body(service.atribuirResponsavel());
    }

    @PostMapping
    public ResponseEntity<ResponsavelRecord> create(@Valid @RequestBody Responsavel responsavel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(responsavel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsavelRecord>  update(@PathVariable UUID id, @Valid @RequestBody Responsavel responsavel) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, responsavel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body("Chamado deletado com Sucesso!");
    }
}
