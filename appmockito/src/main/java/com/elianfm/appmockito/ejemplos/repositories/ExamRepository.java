package com.elianfm.appmockito.ejemplos.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elianfm.appmockito.ejemplos.models.Exam;

public interface ExamRepository {
    List<Exam> findAll();
    Exam save(Exam exam);
}
