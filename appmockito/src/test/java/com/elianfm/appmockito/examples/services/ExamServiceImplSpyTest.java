package com.elianfm.appmockito.examples.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.elianfm.appmockito.ejemplos.models.Exam;
import com.elianfm.appmockito.ejemplos.repositories.ExamRepositoryImpl;
import com.elianfm.appmockito.ejemplos.repositories.QuestionRepositoryImpl;
import com.elianfm.appmockito.ejemplos.services.ExamServiceImpl;

@ExtendWith(MockitoExtension.class) 
public class ExamServiceImplSpyTest {

    // Podemos usar la anotación @Spy para crear un spy de un objeto en lugar de
    // usar spy()
    @Spy
    private ExamRepositoryImpl examRepository;

    @Spy
    private QuestionRepositoryImpl questionRepository;

    @InjectMocks
    private ExamServiceImpl examService;

    // Spy, es un tipo de mock que permite llamar al método real de un objeto
    // y al mismo tiempo simular el comportamiento de algunos métodos. Es un
    // clon del objeto real pero con características de un mock.
    @Test
    void testSpy(){
        // Si usaramos un mock, tendríamos que simular el comportamiento de todos
        // los métodos de la clase ExamenRepositoryImpl, pero con spy podemos
        // llamar al método real y al mismo tiempo simular el comportamiento de
        // algunos métodos. Para spy hay que usar clases concretas y no
        // interfaces o clases abstractas.
        
        // Aquí usamos la anotación @Spy para crear el spy de la clase, por eso
        // se comentan las siguientes líneas.
        //ExamRepositoryImpl examRepository = spy(ExamRepositoryImpl.class);
        //QuestionRepositoryImpl questionRepository = spy(QuestionRepositoryImpl.class);
        //ExamServiceImpl examService = new ExamServiceImpl(examRepository, questionRepository);

        // Podemos hacer un híbrido entre un mock y un spy, es decir, podemos
        // simular el comportamiento de algunos métodos y al mismo tiempo llamar
        // al método real de otros métodos. En este caso, estamos simulando el
        // comportamiento del método findAll() de la clase ExamenRepositoryImpl y
        // llamando al método real findQuestionsByExamId() de la clase
        // QuestionRepositoryImpl. Pero es importante que usemos el método
        // dowhen en lugar de when, ya que cuando usamos when estamos llamando al
        // método real incluso si lo estamos simulando. 
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        doReturn(Data.QUESTIONS).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Matematicas");
        assertEquals(1L, exam.getId(), "El id del examen debe ser 1");
        assertEquals("Matematicas", exam.getName(), "El nombre del examen debe ser Matematicas");
        assertEquals(15, exam.getQuestions().size(), "El examen debe tener 15 preguntas");
        assertTrue(exam.getQuestions().contains("Pregunta 1"), "El examen debe tener la pregunta 1");
        
        verify(examRepository, times(1)).findAll();
        verify(questionRepository, times(1)).findQuestionsByExamId(anyLong());
    }
}
