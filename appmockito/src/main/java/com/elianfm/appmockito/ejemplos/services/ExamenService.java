package com.elianfm.appmockito.ejemplos.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.elianfm.appmockito.ejemplos.models.Examen;

public interface ExamenService {
     Optional<Examen> findExamenByName(String nombre);
}
