package py.edu.uc.lp3.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface EntidadRepository extends JpaRepository<EntidadEntity, String> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select e from EntidadEntity e where e.id = :id")
	Optional<EntidadEntity> buscarParaExplosion(@Param("id") String id);
}
