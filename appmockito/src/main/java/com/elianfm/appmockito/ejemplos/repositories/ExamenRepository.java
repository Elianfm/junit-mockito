package com.elianfm.appmockito.ejemplos.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elianfm.appmockito.ejemplos.models.Examen;

public interface ExamenRepository {
    List<Examen> findAll();
    
}
