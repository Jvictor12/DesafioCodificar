package api.desafio.codificar.Backend.Repository;

import api.desafio.codificar.Backend.Entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, UUID> {

    Optional<Responsavel> findByNome(String nome);

    @Query( value = """
        SELECT r.*
        FROM tb_responsaveis r
        LEFT JOIN tb_chamados c
            ON c.responsavel_id = r.id
        GROUP BY r.id
        ORDER BY COUNT(c.id)
        LIMIT 1
        """, nativeQuery = true)
    public Responsavel findResponsavelComMenosChamado();
}
