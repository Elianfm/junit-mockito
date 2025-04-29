package com.elianfm.appmockito.ejemplos.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    private Long id;
    private String name;
    private List<String> questions;


}
