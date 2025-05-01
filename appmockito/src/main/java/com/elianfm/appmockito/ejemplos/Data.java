package com.elianfm.appmockito.ejemplos;

import java.util.Arrays;
import java.util.List;

import com.elianfm.appmockito.ejemplos.models.Exam;

public class Data {
    public final static List<Exam> DATA = Arrays.asList(
            new Exam(1L, "Matematicas", Data.QUESTIONS),
            new Exam(2L, "Historia", Data.QUESTIONS),
            new Exam(3L, "Geografia", Data.QUESTIONS),
            new Exam(4L, "Ciencias", Data.QUESTIONS),
            new Exam(5L, "Lengua", Data.QUESTIONS),
            new Exam(6L, "Ingles", Data.QUESTIONS),
            new Exam(7L, "Arte", Data.QUESTIONS),
            new Exam(8L, "Educacion Fisica", Data.QUESTIONS),
            new Exam(9L, "Musica", Data.QUESTIONS),
            new Exam(10L, "Programacion", Data.QUESTIONS),
            new Exam(11L, "Quimica", Data.QUESTIONS),
            new Exam(12L, "Biologia", Data.QUESTIONS),
            new Exam(13L, "Fisica", Data.QUESTIONS),
            new Exam(14L, "Etica", Data.QUESTIONS),
            new Exam(15L, "Civica", Data.QUESTIONS));

    public final static List<Exam> DATA_ID_NULL = Arrays.asList(null,
            new Exam(null, "Matematicas", Data.QUESTIONS),
            new Exam(null, "Historia", Data.QUESTIONS),
            new Exam(null, "Geografia", Data.QUESTIONS),
            new Exam(null, "Ciencias", Data.QUESTIONS),
            new Exam(null, "Lengua", Data.QUESTIONS),
            new Exam(null, "Ingles", Data.QUESTIONS),
            new Exam(null, "Arte", Data.QUESTIONS),
            new Exam(null, "Educacion Fisica", Data.QUESTIONS),
            new Exam(null, "Musica", Data.QUESTIONS),
            new Exam(null, "Programacion", Data.QUESTIONS),
            new Exam(null, "Quimica", Data.QUESTIONS),
            new Exam(null, "Biologia", Data.QUESTIONS),
            new Exam(null, "Fisica", Data.QUESTIONS),
            new Exam(null, "Etica", Data.QUESTIONS),
            new Exam(null, "Civica", Data.QUESTIONS));

    public final static List<String> QUESTIONS = Arrays.asList("Pregunta 1", "Pregunta 2", "Pregunta 3",
            "Pregunta 4", "Pregunta 5", "Pregunta 6", "Pregunta 7", "Pregunta 8", "Pregunta 9", "Pregunta 10",
            "Pregunta 11", "Pregunta 12", "Pregunta 13", "Pregunta 14", "Pregunta 15");

    public final static Exam EXAM = new Exam(null, "Electrónica", Data.QUESTIONS);
}
