package co.edu.ucc.smartlibrary.repository;

import co.edu.ucc.smartlibrary.model.Libro;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Bloqueo durante el préstamo: evita dos préstamos activos simultáneos.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Libro l where l.id = :id")
    Optional<Libro> findByIdConBloqueo(@Param("id") Long id);
}
