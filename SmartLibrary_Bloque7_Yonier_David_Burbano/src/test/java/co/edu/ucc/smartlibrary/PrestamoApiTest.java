package co.edu.ucc.smartlibrary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.ucc.smartlibrary.model.Libro;
import co.edu.ucc.smartlibrary.model.Prestamo;
import co.edu.ucc.smartlibrary.repository.EstudianteRepository;
import co.edu.ucc.smartlibrary.repository.LibroRepository;
import co.edu.ucc.smartlibrary.repository.PrestamoRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PrestamoApiTest {
    @Autowired MockMvc mvc;
    @Autowired LibroRepository libros;
    @Autowired EstudianteRepository estudiantes;
    @Autowired PrestamoRepository prestamos;

    @Test
    void registrarLibroDisponibleDevuelve201YCambiaDisponibilidad() throws Exception {
        mvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":1,\"libroId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estudianteId").value(1))
                .andExpect(jsonPath("$.libroId").value(1))
                .andExpect(jsonPath("$.fechaPrestamo").isNotEmpty());
        assertThat(prestamos.count()).isEqualTo(1L);
        assertThat(libros.findById(1L).orElseThrow().estaDisponible()).isFalse();
    }

    @Test
    void impedirSegundoPrestamoActivoDevuelve409YNoCreaOtro() throws Exception {
        String cuerpo = "{\"estudianteId\":1,\"libroId\":1}";
        mvc.perform(post("/api/prestamos").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/prestamos").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value(409));
        assertThat(prestamos.count()).isEqualTo(1L);
    }

    @Test
    void prestamoDeLibroMarcadoNoDisponibleDevuelve409() throws Exception {
        mvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":1,\"libroId\":3}"))
                .andExpect(status().isConflict());
        assertThat(prestamos.count()).isZero();
    }

    @Test
    void estudianteInexistenteDevuelve404() throws Exception {
        mvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":999,\"libroId\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Estudiante")));
        assertThat(prestamos.count()).isZero();
    }

    @Test
    void libroInexistenteDevuelve404() throws Exception {
        mvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":1,\"libroId\":999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Libro")));
        assertThat(prestamos.count()).isZero();
    }

    @Test
    void consultarPrestamosDevuelve200() throws Exception {
        mvc.perform(get("/api/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void solicitudSinIdentificadoresDevuelve400YSinCreacion() throws Exception {
        mvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        assertThat(prestamos.count()).isZero();
    }

    @Test
    void unPrestamoHistoricoPermiteUnoNuevo() throws Exception {
        var estudiante = estudiantes.findById(1L).orElseThrow();
        var libro = libros.findById(1L).orElseThrow();
        Prestamo anterior = new Prestamo(estudiante, libro, LocalDate.now().minusDays(7));
        anterior.registrarDevolucion(LocalDate.now().minusDays(1));
        prestamos.save(anterior);

        mvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":2,\"libroId\":1}"))
                .andExpect(status().isCreated());
        assertThat(prestamos.count()).isEqualTo(2L);
        assertThat(prestamos.existePrestamoActivo(1L)).isTrue();
    }

    @Test
    void unRegistroActivoImpidePrestarAunqueElFlagSeaVerdadero() throws Exception {
        var estudiante = estudiantes.findById(1L).orElseThrow();
        var libro = libros.findById(1L).orElseThrow(); // disponible=true por los datos de prueba
        prestamos.save(new Prestamo(estudiante, libro, LocalDate.now()));

        mvc.perform(post("/api/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":2,\"libroId\":1}"))
                .andExpect(status().isConflict());
        assertThat(prestamos.count()).isEqualTo(1L);
    }
}
