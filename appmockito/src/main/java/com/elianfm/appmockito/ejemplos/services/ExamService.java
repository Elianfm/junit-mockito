package com.elianfm.appmockito.ejemplos.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.elianfm.appmockito.ejemplos.models.Exam;

public interface ExamService {
     Optional<Exam> findExamByName(String name);
     Exam findExamByNameWithQuestions(String name);
     Exam save(Exam exam);
}
