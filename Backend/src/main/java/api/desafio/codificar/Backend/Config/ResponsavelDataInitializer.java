package api.desafio.codificar.Backend.Config;

import api.desafio.codificar.Backend.Entity.Responsavel;
import api.desafio.codificar.Backend.Repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResponsavelDataInitializer implements CommandLineRunner {

    private static final List<ResponsavelInicial> RESPONSAVEIS_INICIAIS = List.of(
            new ResponsavelInicial("João", "20431784582"),
            new ResponsavelInicial("Ayesha", "16500693590"),
            new ResponsavelInicial("Frida", "93127331509")
    );

    private final ResponsavelRepository repository;

    @Override
    @Transactional
    public void run(String... args) {
        RESPONSAVEIS_INICIAIS.forEach(responsavelInicial -> {
            final var responsavel = repository.findByNome(responsavelInicial.nome())
                    .orElseGet(() -> Responsavel.builder()
                            .nome(responsavelInicial.nome())
                            .build());

            responsavel.setCpf(responsavelInicial.cpf());
            repository.save(responsavel);
        });
    }

    private record ResponsavelInicial(String nome, String cpf) {
    }
}
