package com.elianfm.appmockito.examples.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import com.elianfm.appmockito.ejemplos.models.Exam;
import com.elianfm.appmockito.ejemplos.repositories.ExamRepository;
import com.elianfm.appmockito.ejemplos.repositories.ExamRepositoryImpl;
import com.elianfm.appmockito.ejemplos.repositories.QuestionRepository;
import com.elianfm.appmockito.ejemplos.services.ExamService;
import com.elianfm.appmockito.ejemplos.services.ExamServiceImpl;

// Esta anotación permite usar Mockito sin necesidad de inicializar los mocks manualmente
@ExtendWith(MockitoExtension.class) 
public class ExamServiceImplTest {
    // Para evitar el uso del @BeforeEach setup(), podemos usar inyeccion
    // de dependencias con @Mock y @InjectMocks

    @Mock
    ExamRepository repository;

    @Mock
    QuestionRepository questionRepository;

    @InjectMocks // inyecta los dos mocks en el servicio (tiene que ser 
    // una implementación y no una interfaz)
    ExamServiceImpl service;

    @BeforeEach
    void setUp() {
        // Inicializamos los mocks, esto es necesario para que Mockito pueda
        // inyectar los mocks en el servicio. Si no lo hacemos, los mocks seran null
        // y no podremos usarlos en las pruebas. Se comenta también porque
        // podemos usar la anotación @ExtendWith(MockitoExtension.class) en la clase de prueba
        // y no es necesario inicializarlos manualmente.
        //MockitoAnnotations.openMocks(this);
        
        
        // Lo comentamos porque usamos inyección de dependencias con @Mock y
        // @InjectMocks
        /* repository = mock(ExamRepository.class);
        questionRepository = mock(QuestionRepository.class);
        service = new ExamServiceImpl(repository, questionRepository);  */

    }


    @Test
    void testFindExamenByName() {
        // ExamenRepository repository = new ExamenRepositoryImpl();
        // No se recomienda usar el repositorio real, ya que no es una prueba unitaria
        // y no queremos depender de la base de datos. En su lugar, debemos usar un mock
        // de la clase ExamenRepository y devolver una lista de examenes o una lista
        // vacía o lo que queramos.

        // Aqui creamos un mock de la clase ExamenRepository y le decimos que cuando se
        // llame al metodo findAll(), devuelva la lista de examenes que hemos creado
        // anteriormente. Nota: Nunca se invoca el método real findAll() de la
        // clase ExamenRepositoryImpl, ya que la idea es simular el comportamiento con
        // Mockito.
        when(repository.findAll()).thenReturn(Data.DATA);
        // Nota 2: No se pueden simular los metodos estaticos, los metodos private ni
        // los métodos final

        Optional<Exam> examen = service.findExamByName("Matematicas");

        assertTrue(examen.isPresent(), "El examen debe estar presente");
        assertNotNull(examen, "El examen no debe ser nulo");
        assertEquals(1L, examen.orElseThrow().getId(), "El id del examen debe ser 1");
        assertEquals("Matematicas", examen.orElseThrow().getName(), "El nombre del examen debe ser Matematicas");
    }

    @Test
    void findExamenByNameEmptyList(){
        // Aqui creamos un mock de la clase ExamenRepository y le decimos que cuando se
        // llame al metodo findAll(), devuelva una lista vacia
        when(repository.findAll()).thenReturn(Arrays.asList());

        Optional<Exam> examen = service.findExamByName("Matematicas");
        assertFalse(examen.isPresent(), "El examen no debe estar presente");
        assertEquals(Optional.empty(), examen, "El examen debe ser vacio");
    }

    @Test
    void testExamQuestions() {
        when(repository.findAll()).thenReturn(Data.DATA);

        // Aquí usamos anyLong() para indicar que no nos importa el valor del id del examen
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam examen = service.findExamByNameWithQuestions("Matematicas");
        assertEquals(15, examen.getQuestions().size(), "El examen debe tener 15 preguntas");

        assertTrue(examen.getQuestions().contains("Pregunta 1"), "El examen debe tener la pregunta 1");
        assertFalse(examen.getQuestions().contains("Pregunta 16"), "El examen no debe tener la pregunta 16");
    }

    // Verify, este método se usa para verificar que un método ha sido llamado una vez o
    // varias veces. También se puede usar para verificar que un método no ha sido llamado.
    @Test
    void testExamQuestionsVerify() {
        when(repository.findAll()).thenReturn(Data.DATA);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam examen = service.findExamByNameWithQuestions("Matematicas");

        // Verificamos que el método findQuestionsByExamId() ha sido llamado una vez
        verify(questionRepository, times(1)).findQuestionsByExamId(examen.getId());

        // Verificamos que el método findAll() ha sido llamado una vez
        verify(repository, times(1)).findAll();
    }

    @Test
    void testNoExistExamVerify() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam examen = service.findExamByNameWithQuestions("Matematicas II");

        assertNull(examen);
        
        // Verificamos que el método findQuestionsByExamId() no ha sido llamado
        // ya que el examen no existe 
        verify(repository, times(1)).findAll();
        verify(questionRepository, never()).findQuestionsByExamId(anyLong());
    }


    // Acá probamos el método save() de la clase ExamenServiceImpl, que guarda un examen
    // en la base de datos y guarda las preguntas del examen en la base de datos
    @Test
    void testSaveExam() {
        // *** Given -> son las precondiciones para la prueba

        // Answer es una interfaz que permite simular el comportamiento de un método
        // cuando se llama a un método de un mock. En este caso, estamos simulando el
        // comportamiento del método save() de la clase ExamenRepository.
        when(repository.save(any(Exam.class))).then(new Answer<Exam>() {
            Long secuencia = 16L; // Simulamos una secuencia de ids para los examenes

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam exam = invocation.getArgument(0); // Obtenemos el examen que se pasa como argumento
                exam.setId(secuencia++); 
                return exam; 
            }
        });

        // *** When -> cuando se ejecuta el método que queremos probar
        Exam exam = service.save(Data.EXAM);
        Exam exam2 = service.save(Data.EXAM);

        // *** Then -> luego verificamos el resultado
        assertNotNull(exam, "El examen no debe ser nulo");
        assertNotNull(exam2, "El examen no debe ser nulo");

        assertEquals(Data.EXAM.getId(), exam.getId(), "El id del examen debe ser 16");
        assertEquals(Data.EXAM.getId(), exam2.getId(), "El id del examen debe ser 17");

        assertEquals(Data.EXAM.getName(), exam.getName(), "El nombre del examen debe ser Electrónica");

        verify(repository, times(2)).save(any(Exam.class));
        verify(questionRepository, times(2)).saveQuestions(anyList());
    }
        

    @Test
    void testExceptionHandler(){
        when(repository.findAll()).thenReturn(Data.DATA);
        
        // thenThrow() es un método de Mockito que permite simular una excepción
        // cuando se llama a un método de un mock.
        when(questionRepository.findQuestionsByExamId(anyLong()))
            .thenThrow(new IllegalArgumentException("Error al obtener las preguntas"));
        
        // Aquí usamos assertThrows() para verificar que se lanza una excepción
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamByNameWithQuestions("Matematicas");
        }, "Error al obtener las preguntas");

        assertEquals(IllegalArgumentException.class, exception.getClass(), "La excepción debe ser IllegalArgumentException");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.DATA);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamByNameWithQuestions("Matematicas");

        // argThat() es un método de Mockito que permite verificar que se ha llamado a un
        // método con un argumento que cumple una determinada condición.
        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(id -> id > 0 && id < 1000));
        verify(questionRepository).findQuestionsByExamId(argThat(id -> id != null && id.equals(1L)));
    }
}
