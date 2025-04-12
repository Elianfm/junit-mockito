package com.elianfm.appmockito.ejemplos.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elianfm.appmockito.ejemplos.models.Examen;
import com.elianfm.appmockito.ejemplos.repositories.ExamenRepository;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public Optional<Examen> findExamenByName(String nombre) {
        return examenRepository.findAll().stream()
                .filter(examen -> examen.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

}
