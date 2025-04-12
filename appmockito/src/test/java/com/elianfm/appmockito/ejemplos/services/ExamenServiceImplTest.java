package com.elianfm.appmockito.ejemplos.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import com.elianfm.appmockito.ejemplos.models.Examen;
import com.elianfm.appmockito.ejemplos.repositories.ExamenRepository;
import com.elianfm.appmockito.ejemplos.repositories.ExamenRepositoryImpl;

public class ExamenServiceImplTest {
    @Test
    void testFindExamenByName() {
        // ExamenRepository repository = new ExamenRepositoryImpl();
        // No se recomienda usar el repositorio real, ya que no es una prueba unitaria
        // y no queremos depender de la base de datos. En su lugar, debemos usar un mock
        // de la clase ExamenRepository y devolver una lista de examenes o una lista
        // vacía o lo que queramos.

        List<Examen> data = Arrays.asList(new Examen(1L, "Matematicas", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(2L, "Historia", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(3L, "Geografia", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(4L, "Ciencias", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(5L, "Lengua", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(6L, "Ingles", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(7L, "Arte", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(8L, "Educacion Fisica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(9L, "Musica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(10L, "Programacion", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(11L, "Quimica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(12L, "Biologia", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(13L, "Fisica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(14L, "Etica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Examen(15L, "Civica", Arrays.asList("Pregunta 1", "Pregunta 2")));

        // Aqui creamos un mock de la clase ExamenRepository y le decimos que cuando se
        // llame al metodo findAll(), devuelva la lista de examenes que hemos creado
        // anteriormente. Nota: Nunca se invoca el método real findAll() de la
        // clase ExamenRepositoryImpl, ya que la idea es simular el comportamiento con
        // Mockito.
        ExamenRepository repository = mock(ExamenRepositoryImpl.class);
        when(repository.findAll()).thenReturn(data);
        // Nota 2: No se pueden simular los metodos estaticos ni los metodos private ni
        // los métodos final

        ExamenService service = new ExamenServiceImpl(repository);

        Optional<Examen> examen = service.findExamenByName("Matematicas");

        assertTrue(examen.isPresent(), "El examen debe estar presente");
        assertNotNull(examen, "El examen no debe ser nulo");
        assertEquals(1L, examen.orElseThrow().getId(), "El id del examen debe ser 1");
        assertEquals("Matematicas", examen.orElseThrow().getNombre(), "El nombre del examen debe ser Matematicas");
    }

    @Test
    void findExamenByNameEmptyList(){
        // Aqui creamos un mock de la clase ExamenRepository y le decimos que cuando se
        // llame al metodo findAll(), devuelva una lista vacia
        ExamenRepository repository = mock(ExamenRepositoryImpl.class);
        when(repository.findAll()).thenReturn(Arrays.asList());

        ExamenService service = new ExamenServiceImpl(repository);

        Optional<Examen> examen = service.findExamenByName("Matematicas");

        assertFalse(examen.isPresent(), "El examen no debe estar presente");
        assertEquals(Optional.empty(), examen, "El examen debe ser vacio");
    }
}
