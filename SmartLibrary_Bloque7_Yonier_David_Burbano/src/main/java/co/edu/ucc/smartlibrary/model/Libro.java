package co.edu.ucc.smartlibrary.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private boolean disponible;

    protected Libro() { }

    public Libro(String titulo, boolean disponible) {
        this.titulo = titulo;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean estaDisponible() { return disponible; }

    // R3: el cambio de estado pertenece a la entidad que conoce su disponibilidad.
    public void marcarPrestado() {
        this.disponible = false;
    }

    // Soporta el cierre de un préstamo histórico; no se expone como endpoint en este taller.
    public void marcarDisponible() {
        this.disponible = true;
    }
}
