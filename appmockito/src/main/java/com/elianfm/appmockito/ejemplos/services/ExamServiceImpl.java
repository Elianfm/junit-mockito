package com.elianfm.appmockito.ejemplos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elianfm.appmockito.ejemplos.models.Exam;
import com.elianfm.appmockito.ejemplos.repositories.ExamRepository;
import com.elianfm.appmockito.ejemplos.repositories.QuestionRepository;

public class ExamServiceImpl implements ExamService {

    private ExamRepository examRepository;
    private QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examenRepository, QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examenRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return examRepository.findAll().stream()
                .filter(examen -> examen.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        Optional<Exam> examOptional = findExamByName(name);

        Exam exam = null;

        if(examOptional.isPresent()) {
            exam = examOptional.get();
            List<String> questions = questionRepository.findQuestionsByExamId(exam.getId());
            exam.setQuestions(questions);
        }
        // Aquí solo usamos la interfaz de questionRepository, no la implementación
        // ya que no nos interesa, por que vamos a usar un mock para testearla

        return exam;
    }

    @Override
    public Exam save(Exam exam) {
        if(!exam.getQuestions().isEmpty()) {
            questionRepository.saveQuestions(exam.getQuestions());
        }

        return examRepository.save(exam);

    }

}
