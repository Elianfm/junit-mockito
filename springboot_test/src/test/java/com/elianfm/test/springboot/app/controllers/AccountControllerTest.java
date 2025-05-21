package com.elianfm.test.springboot.app.controllers;

import static com.elianfm.test.springboot.app.Data.createAccount001;
import static com.elianfm.test.springboot.app.Data.createAccount002;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.elianfm.test.springboot.app.models.Account;
import com.elianfm.test.springboot.app.models.TransactionDTO;
import com.elianfm.test.springboot.app.services.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * WebMvcTest es una anotación de Spring que se utiliza para probar controladores 
 * de Spring MVC. Esta anotación configura un contexto de aplicación que incluye solo
 * los componentes necesarios para probar un controlador específico, como los controladores,
 * los convertidores de mensajes y los filtros. No carga toda la aplicación, lo que
 * hace que las pruebas sean más rápidas y ligeras.
 */
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    /*
     * MockMvc es una clase de Spring que se utiliza para probar controladores de
     * Spring MVC. Proporciona una forma de simular solicitudes HTTP y verificar
     * respuestas sin necesidad de iniciar un servidor web. Esto permite realizar
     * pruebas unitarias y de integración de los controladores de forma aislada.
     * Es como un mvc pero falso que simula el comportamiento de un controlador.
     */
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AccountService accountService;

    /*
     * ObjectMapper es una clase de la biblioteca Jackson que se utiliza para
     * convertir objetos Java a JSON y viceversa.
     */
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testDetail() throws Exception {
        // Given
        when(accountService.findById(1L)).thenReturn(createAccount001().get());
        when(accountService.findById(2L)).thenReturn(createAccount002().get());

        // When

        /*
         * El método perform() se utiliza para simular una solicitud HTTP a la
         * aplicación. En este caso, se está simulando una solicitud GET a la
         * ruta "/api/v1/accounts/1".
         * 
         * .andExpect() se utiliza para verificar la respuesta de la solicitud. En este
         * caso, se están verificando los valores de los campos "id", "person" y
         * "balance" en la respuesta JSON.
         */
        mvc.perform(get("/api/v1/accounts/1").contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.person").value("Elian"))
                .andExpect(jsonPath("$.balance").value(1000.00));

        verify(accountService).findById(1L);
    }

    @Test
    void testTransfer() throws Exception {

        // Given
        TransactionDTO dto = new TransactionDTO();
        dto.setOriginId(1L);
        dto.setDestinationId(2L);
        dto.setAmount(new BigDecimal(100));
        dto.setBankId(1L);

        Map<String, Object> response = Map.of(
                "date", LocalDate.now(),
                "status", "ok",
                "message", "Transaction completed successfully",
                "transaction", dto);


        // When
        mvc.perform(post("/api/v1/accounts/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.message").value("Transaction completed successfully"))
                .andExpect(jsonPath("$.transaction.originId").value(1L))
                .andExpect(jsonPath("$.transaction.destinationId").value(2L))
                .andExpect(jsonPath("$.transaction.amount").value(100.00))
                .andExpect(jsonPath("$.transaction.bankId").value(1L))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testList() throws Exception {
        
        // Given
        List<Account> accounts = List.of(
                createAccount001().orElseThrow(),
                createAccount002().orElseThrow()
        );
        when(accountService.findAll()).thenReturn(accounts);

        // When
        mvc.perform(get("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].person").value("Elian"))
                .andExpect(jsonPath("$[0].balance").value(1000.00))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].person").value("Julián"))
                .andExpect(jsonPath("$[1].balance").value(2000.00))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));

        verify(accountService).findAll();
    }

    @Test
    void testCreate() throws Exception {
        // Given
        Account account = new Account(null, "Picasso", new BigDecimal(1111.00));
        when(accountService.save(any())).then( invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(3L);
            return acc;
        });

        // When
        mvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(3))) // Alternativa a .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.person", Matchers.is("Picasso"))) 
                .andExpect(jsonPath("$.balance").value(account.getBalance()));  

        verify(accountService).save(any());
    }

}
