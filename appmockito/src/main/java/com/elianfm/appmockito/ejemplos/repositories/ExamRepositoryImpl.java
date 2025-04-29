package com.elianfm.appmockito.ejemplos.repositories;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.elianfm.appmockito.ejemplos.models.Exam;

public class ExamRepositoryImpl implements ExamRepository {

    @Override
    public List<Exam> findAll() {
        // return Collections.emptyList(); // Simulamos una base de datos vacía 
        // (NO recomdable hacer esto solo para pruebas)

        return Arrays.asList(new Exam(1L, "Matematicas", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(2L, "Historia", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(3L, "Geografia", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(4L, "Ciencias", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(5L, "Lengua", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(6L, "Ingles", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(7L, "Arte", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(8L, "Educacion Fisica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(9L, "Musica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(10L, "Programacion", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(11L, "Quimica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(12L, "Biologia", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(13L, "Fisica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(14L, "Etica", Arrays.asList("Pregunta 1", "Pregunta 2")),
                new Exam(15L, "Civica", Arrays.asList("Pregunta 1", "Pregunta 2")));

        // Al realizar pruebas unitarias, el codigo de anterior funcionara, pero si
        // queremos realizar pruebas con otros datos, podemos usar Mockito para
        // simular el comportamiento de la base de datos. En este caso, podemos
        // crear un mock de la clase ExamenRepository y devolver una lista de examenes
        // o una lista vacía o lo que queramos. Esto nos permite probar el
        // comportamiento de la clase ExamenService sin depender de la base de datos.
        // Para ello, podemos usar la anotacion @Mock de Mockito y la anotacion
        // @InjectMocks para inyectar el mock en la clase ExamenService.
    }

    @Override
    public Exam save(Exam exam) {
        // No implementamos el metodo save, ya que no es necesario para el ejemplo
        // (se va a usar un mock para testearlo)
        return exam;
    }

}
