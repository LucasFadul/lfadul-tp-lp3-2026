package py.edu.uc.lp3.persistence;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TntRepository extends JpaRepository<TntEntity, String> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select t from TntEntity t where t.id = :id")
	Optional<TntEntity> buscarParaDetonar(@Param("id") String id);
}
