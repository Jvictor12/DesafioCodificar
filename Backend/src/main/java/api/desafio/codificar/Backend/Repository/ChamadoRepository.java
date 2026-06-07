package api.desafio.codificar.Backend.Repository;

import api.desafio.codificar.Backend.Entity.Chamado;
import api.desafio.codificar.Backend.Entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChamadoRepository extends JpaRepository <Chamado, UUID> {
}
