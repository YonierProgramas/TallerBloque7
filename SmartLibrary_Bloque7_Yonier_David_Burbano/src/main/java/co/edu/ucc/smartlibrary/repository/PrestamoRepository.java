package co.edu.ucc.smartlibrary.repository;

import co.edu.ucc.smartlibrary.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    @Query("select case when count(p) > 0 then true else false end " +
           "from Prestamo p where p.libro.id = :libroId and p.fechaDevolucion is null")
    boolean existePrestamoActivo(@Param("libroId") Long libroId);
}
