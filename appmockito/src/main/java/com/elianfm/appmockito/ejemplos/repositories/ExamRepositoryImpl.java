package com.elianfm.appmockito.ejemplos.repositories;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.elianfm.appmockito.ejemplos.Data;
import com.elianfm.appmockito.ejemplos.models.Exam;

public class ExamRepositoryImpl implements ExamRepository {

    @Override
    public List<Exam> findAll() {
        // return Collections.emptyList(); // Simulamos una base de datos vacía 
        // (NO recomdable hacer esto solo para pruebas)
        System.out.println("Buscando todos los examenes");
        return Data.DATA;

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
        System.out.println("Guardando examen: " + exam);
        return Data.DATA.get(0);
    }

}
