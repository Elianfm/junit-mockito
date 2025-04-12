package com.elianfm.appmockito.ejemplos.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Examen {

    private Long id;
    private String nombre;
    private List<String> preguntas;


}
