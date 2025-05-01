package com.elianfm.appmockito.ejemplos.repositories;

import java.util.List;

import com.elianfm.appmockito.ejemplos.Data;

public class QuestionRepositoryImpl implements QuestionRepository {

    @Override
    public List<String> findQuestionsByExamId(Long id) {
        System.out.println("Buscando preguntas por id de examen: " + id);
        return Data.QUESTIONS;
    }

    @Override
    public void saveQuestions(List<String> question) {
        System.out.println("Guardando preguntas: " + question);
    }

   

}
