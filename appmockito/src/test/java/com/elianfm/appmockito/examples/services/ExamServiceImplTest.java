package com.elianfm.appmockito.examples.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.elianfm.appmockito.ejemplos.models.Exam;
import com.elianfm.appmockito.ejemplos.repositories.ExamRepository;
import com.elianfm.appmockito.ejemplos.repositories.ExamRepositoryImpl;
import com.elianfm.appmockito.ejemplos.repositories.QuestionRepository;
import com.elianfm.appmockito.ejemplos.repositories.QuestionRepositoryImpl;
import com.elianfm.appmockito.ejemplos.services.ExamServiceImpl;

// Esta anotación permite usar Mockito sin necesidad de inicializar los mocks manualmente
@ExtendWith(MockitoExtension.class) 
public class ExamServiceImplTest {
    // Para evitar el uso del @BeforeEach setup(), podemos usar inyeccion
    // de dependencias con @Mock y @InjectMocks

    @Mock
    ExamRepositoryImpl repository;

    @Mock
    QuestionRepositoryImpl questionRepository;

    @InjectMocks // inyecta los dos mocks en el servicio (tiene que ser 
    // una implementación y no una interfaz)
    ExamServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor; // Capturador de argumentos, se usa para capturar
    // los argumentos que se pasan a un método de un mock

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

    // En lugar de usar un argumento que cumple una determinada condición, podemos
    // usar un ArgumentMatcher personalizado. 
    @Test
    void testArgumentMatchersCustom() {
        when(repository.findAll()).thenReturn(Data.DATA);
        when(questionRepository.findQuestionsByExamId(argThat(new MyArgsMatchers()))).thenReturn(Data.QUESTIONS);

        service.findExamByNameWithQuestions("Matematicas");

        verify(repository).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(new MyArgsMatchers()));
    }


    // ArgumentMatchers personalizados, se pueden usar para verificar que se ha llamado a un
    // método con un argumento que cumple una determinada condición. En este caso, estamos
    // verificando que el id del examen es mayor que 0 y menor que 1000.
    public static class MyArgsMatchers implements ArgumentMatcher<Long> {
        // matches() es un método de la interfaz ArgumentMatcher que permite verificar que
        // se ha llamado a un método con un argumento que cumple una determinada condición.
        @Override
        public boolean matches(Long argument) {
            return argument != null && argument > 0 && argument < 1000;
        }

        // Este método se usa para mostrar un mensaje de error cuando el argumento no
        // cumple la condición. 
        @Override
        public String toString() {
            return "El id del examen debe ser mayor que 0 y menor que 1000";
        }
    }


    @Test
    void testArgumenCapture(){
        when(repository.findAll()).thenReturn(Data.DATA);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        service.findExamByNameWithQuestions("Matematicas");

        // ArgumentCaptor es una clase de Mockito que permite capturar los argumentos
        // que se pasan a un método de un mock. En este caso, estamos capturando el id
        // del examen que se pasa al método findQuestionsByExamId().
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        // lo comentamos porque usamos la anotación @Captor en la clase de prueba
        // y no es necesario inicializarlo manualmente.

        verify(questionRepository).findQuestionsByExamId(captor.capture());

        Long id = captor.getValue();
        assertNotNull(id, "El id no debe ser nulo");
        assertEquals(1L, id, "El id del examen debe ser 1");
    }

    @Test
    void testDoThrow(){
        // Acá no podemos usar el método thenThrow() porque el método save() de la clase
        // ExamenRepository no lanza una excepción, sino que 
        // void saveQuestions(List<String> question); 
        // devuelve void.
        // Por lo tanto, tenemos que usar el método doThrow() que permite simular una
        // excepción cuando se llama a un método de un mock.
        /*********
        when(questionRepository.saveQuestions(anyList()))
            .thenThrow(new IllegalArgumentException("Error al guardar las preguntas"));
        *********/
        Exam exam = Data.DATA.get(0);
        exam.setQuestions(Data.QUESTIONS);
        
        doThrow(new IllegalArgumentException("Error al guardar las preguntas"))
            .when(questionRepository).saveQuestions(anyList());

        // Aquí usamos assertThrows() para verificar que se lanza una excepción
        assertThrows(IllegalArgumentException.class, () -> {
            service.save(exam);
        }, "Error al guardar las preguntas");
    }

    @Test
    void testDoAnswer(){
        when(repository.findAll()).thenReturn(Data.DATA);
        
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);


        // doAnswer() es un método de Mockito que permite simular el comportamiento
        // de un método cuando se llama a un método de un mock. En este caso, estamos
        // simulando el comportamiento del método findQuestionsByExamId() de la clase
        // QuestionRepository.
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0); // Obtenemos el id del examen que se pasa como argumento

            // Si el id es 1, devolvemos las preguntas, si no, devolvemos una lista vacía
            return id == 1L ? Data.QUESTIONS : Collections.emptyList();

        }).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam examen = service.findExamByNameWithQuestions("Matematicas");
        assertTrue(!examen.getQuestions().isEmpty(), "El examen debe tener preguntas");
        assertEquals(1L, examen.getId(), "El id del examen debe ser 1");
        assertEquals("Matematicas", examen.getName(), "El nombre del examen debe ser Matematicas");

    }

    @Test
    void testDoCallRealMethod(){
        when(repository.findAll()).thenReturn(Data.DATA);
        //when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        // doCallRealMethod() es un método de Mockito que permite llamar al método real
        // de un mock. En este caso, estamos llamando al método real findQuestionsByExamId()
        // de la clase QuestionRepository.
        doCallRealMethod().when(questionRepository).findQuestionsByExamId(anyLong());

        Exam examen = service.findExamByNameWithQuestions("Matematicas");
        assertEquals(1L, examen.getId(), "El id del examen debe ser 1");
        assertEquals("Matematicas", examen.getName(), "El nombre del examen debe ser Matematicas");
    }

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
        ExamRepositoryImpl examRepository = spy(ExamRepositoryImpl.class);
        QuestionRepositoryImpl questionRepository = spy(QuestionRepositoryImpl.class);

        ExamServiceImpl examService = new ExamServiceImpl(examRepository, questionRepository);

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

    @Test
    void testInvocationOrder(){
        when(repository.findAll()).thenReturn(Data.DATA);

        service.findExamByNameWithQuestions("Matematicas");
        service.findExamByNameWithQuestions("Historia");

        // InOrder es una clase de Mockito que permite verificar el orden 
        // en el que se han llamado a los métodos de un mock.
        InOrder inOrder = inOrder(questionRepository);
        inOrder.verify(questionRepository).findQuestionsByExamId(1L);
        inOrder.verify(questionRepository).findQuestionsByExamId(2L);
    }

    @Test 
    void testInvocationOrder2(){
        when(repository.findAll()).thenReturn(Data.DATA);

        service.findExamByNameWithQuestions("Matematicas");
        service.findExamByNameWithQuestions("Historia");

        // Podemos verificar el orden de los métodos de varios mocks
        InOrder inOrder = inOrder(repository, questionRepository);

        inOrder.verify(repository).findAll();
        // inOrder.verify(repository).findAll(); // No funcionaría porque
        // el método findAll() no se llama dos veces seguidas, solo una vez.
        inOrder.verify(questionRepository).findQuestionsByExamId(1L);

        inOrder.verify(repository).findAll();
        inOrder.verify(questionRepository).findQuestionsByExamId(2L);
    }

    @Test
    void testInvocationNumber(){
        when(repository.findAll()).thenReturn(Data.DATA);

        service.findExamByNameWithQuestions("Matematicas");
        service.findExamByNameWithQuestions("Historia");

        // Verificamos que el método findAll() se ha llamado dos veces
        verify(repository, times(2)).findAll();

        // Verificamos que el método findQuestionsByExamId() se ha llamado dos veces
        verify(questionRepository, times(2)).findQuestionsByExamId(anyLong());

        // atLeast para verificar que se ha llamado al menos x veces
        verify(repository, atLeast(1)).findAll();

        // o podríamos usar atLeatstOnce() que es lo mismo
        verify(repository, atLeastOnce()).findAll();

        // o un máximo
        verify(repository, atMost(2)).findAll();

        // o máximo 1 vez
        //verify(repository, atMostOnce()).findAll();

        // o nunca
        //verify(repository, never()).findAll();

        // o sin interacción con el mock
        //verifyNoInteractions(repository);

        // o que no se interactue después de esta línea
        // No da error porque no se ha llamado a ningún método después de esta línea
        verifyNoMoreInteractions(repository); 


    }
}
