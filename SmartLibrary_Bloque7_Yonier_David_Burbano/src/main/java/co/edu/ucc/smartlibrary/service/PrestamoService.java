package co.edu.ucc.smartlibrary.service;

import co.edu.ucc.smartlibrary.dto.PrestamoRequest;
import co.edu.ucc.smartlibrary.dto.PrestamoResponse;
import co.edu.ucc.smartlibrary.exception.LibroNoDisponibleException;
import co.edu.ucc.smartlibrary.exception.RecursoNoEncontradoException;
import co.edu.ucc.smartlibrary.model.Estudiante;
import co.edu.ucc.smartlibrary.model.Libro;
import co.edu.ucc.smartlibrary.model.Prestamo;
import co.edu.ucc.smartlibrary.repository.EstudianteRepository;
import co.edu.ucc.smartlibrary.repository.LibroRepository;
import co.edu.ucc.smartlibrary.repository.PrestamoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoService {
    private final EstudianteRepository estudianteRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public PrestamoService(EstudianteRepository estudianteRepository,
                           LibroRepository libroRepository,
                           PrestamoRepository prestamoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Transactional
    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante", request.estudianteId()));
        Libro libro = libroRepository.findByIdConBloqueo(request.libroId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro", request.libroId()));

        // R1 y R5: un libro no puede tener más de un préstamo activo.
        if (!libro.estaDisponible() ||
                prestamoRepository.existePrestamoActivo(libro.getId())) {
            throw new LibroNoDisponibleException(libro.getId());
        }

        // R2 y R3: registrar estudiante, libro y fecha; luego marcar el libro prestado.
        libro.marcarPrestado();
        libroRepository.save(libro);
        Prestamo prestamo = new Prestamo(estudiante, libro, LocalDate.now());
        Prestamo guardado = prestamoRepository.save(prestamo);
        return PrestamoResponse.desde(guardado);
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPrestamos() {
        return prestamoRepository.findAll().stream().map(PrestamoResponse::desde).toList();
    }
}
