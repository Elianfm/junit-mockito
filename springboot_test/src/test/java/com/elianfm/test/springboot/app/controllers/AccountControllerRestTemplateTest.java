package com.elianfm.test.springboot.app.controllers;

import static org.junit.jupiter.api.Assertions.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.elianfm.test.springboot.app.models.Account;
import com.elianfm.test.springboot.app.models.TransactionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


// Generalmente solo tenemos un test de integración, pero en casos especiales
// podemos tener varios, como aquí que usamos TestRestTemplate y WebTestClient
// En estos casos podemos usar @Tag("integration_rt") y @Tag("integration_wc")
// para diferenciarlos, de esta forma podemos excluir aquellos tests que no queremos e
// ejecutar en un momento dado sin que afecte al resto de pruebas. 
// El comando en la terminal sería:
// ./mvnw test -Dgroups="integration_wc" para ejecutar solo los tests WebTestClient
// ./mvnw test -Dgroups="!integration_wc" para ejecutar todos los tests excepto los WebTestClient
@Tag("integration_rt")
// Similar a WebTestClient, pero usando RestTemplate
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerRestTemplateTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    // Si queremos usar un puerto específico, podemos obtenerlo de la anotación
    // LocalServerPort
    // @LocalServerPort
    // private int port;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @Order(1)
    void testTransfer() throws JsonProcessingException {
        TransactionDTO transaction = new TransactionDTO();
        transaction.setOriginId(1L);
        transaction.setDestinationId(2L);
        transaction.setBankId(1L);
        transaction.setAmount(new BigDecimal(100));

        ResponseEntity<String> response = client
                .postForEntity("/api/v1/accounts/transaction", transaction, String.class);
        // .postForEntity(createURI("/api/v1/accounts/transaction"), transaction,
        // String.class);
        String json = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transaction completed successfully"));
        assertTrue(json.contains("date"));
        assertTrue(json.contains("status"));
        assertTrue(json.contains("transaction"));

        // También podemos usar jsonNode para validar el JSON, nos da más flexibilidad
        JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("Transaction completed successfully", jsonNode.path("message").asText());
        assertEquals("ok", jsonNode.path("status").asText());
        assertEquals(transaction.getOriginId(), jsonNode.path("transaction").path("originId").asLong());
        assertEquals(transaction.getDestinationId(), jsonNode.path("transaction").path("destinationId").asLong());
        assertEquals(transaction.getBankId(), jsonNode.path("transaction").path("bankId").asLong());
        assertEquals(transaction.getAmount().toString(), jsonNode.path("transaction").path("amount").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());

        Map<String, Object> response2 = Map.of(
                "date", LocalDate.now(),
                "status", "ok",
                "message", "Transaction completed successfully",
                "transaction", transaction);

        assertEquals(objectMapper.writeValueAsString(response2), json);
    }

    @Test
    @Order(2)
    void testDetail() {
        ResponseEntity<Account> response = client
                .getForEntity("/api/v1/accounts/1", Account.class);

        Account account = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(1L, account.getId());
        assertEquals("John Doe", account.getPerson());
        assertEquals(new BigDecimal("900.00"), account.getBalance());
        assertEquals(new Account(1L, "John Doe", new BigDecimal("900.00")), account);

    }

    @Test
    @Order(3)
    void testList() throws JsonProcessingException {
        ResponseEntity<Account[]> response = client
                .getForEntity("/api/v1/accounts", Account[].class);

        List<Account> accounts = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(accounts);
        assertEquals(4, accounts.size());

        assertEquals(1L, accounts.get(0).getId());
        assertEquals("John Doe", accounts.get(0).getPerson());
        assertEquals(new BigDecimal("900.00"), accounts.get(0).getBalance());

        assertEquals(2L, accounts.get(1).getId());
        assertEquals("Jane Smith", accounts.get(1).getPerson());
        assertEquals(new BigDecimal("2100.00"), accounts.get(1).getBalance());

        // En lugar de usar List<Account>, podemos usar JsonNode para validar el JSON
        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(accounts));
        assertEquals(4, jsonNode.size());

        assertEquals(1L, jsonNode.get(0).path("id").asLong());
        assertEquals("John Doe", jsonNode.get(0).path("person").asText());
        assertEquals("900.0", jsonNode.get(0).path("balance").asText());

        assertEquals(2L, jsonNode.get(1).path("id").asLong());
        assertEquals("Jane Smith", jsonNode.get(1).path("person").asText());
        assertEquals("2100.0", jsonNode.get(1).path("balance").asText());

    }

    @Test
    @Order(4)
    void testCreate() {
        Account newAccount = new Account();
        newAccount.setPerson("Alice Johnson");
        newAccount.setBalance(new BigDecimal("1500.00"));

        ResponseEntity<Account> response = client
                .postForEntity("/api/v1/accounts", newAccount, Account.class);

        Account createdAccount = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(createdAccount);
        assertEquals(5L, createdAccount.getId());
        assertEquals("Alice Johnson", createdAccount.getPerson());
        assertEquals(new BigDecimal("1500.00"), createdAccount.getBalance());

    }

    @Test
    @Order(5)
    void testDelete() {

        // Primero vemos que existen 5 cuentas
        ResponseEntity<Account[]> response = client
                .getForEntity("/api/v1/accounts", Account[].class);
        assertEquals(5, Arrays.asList(response.getBody()).size());

        // Luego eliminamos la cuenta con id 5
        //client.delete("/api/v1/accounts/5"); // No devuelve nada

        // Si usamos exchange, podemos especificar el tipo de respuesta
        // y obtener un ResponseEntity<Void> para verificar el estado de la respuesta
        // ResponseEntity<Void> deleteResponse = client
        //        .exchange("/api/v1/accounts/{id}", HttpMethod.DELETE, null, Void.class, 5L);

        // Podemos también usar un Map para pasar parámetros si son muchos
        Map<String, Long> params = Map.of("id", 5L);
        ResponseEntity<Void> deleteResponse = client
                .exchange("/api/v1/accounts/{id}", HttpMethod.DELETE, null, Void.class, params);


        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertFalse(deleteResponse.hasBody());

        // Verificamos que el número de cuentas ha disminuido a 4
        response = client
                .getForEntity("/api/v1/accounts", Account[].class);
        assertEquals(4, Arrays.asList(response.getBody()).size());

        // Finalmente, intentamos obtener la cuenta eliminada y esperamos un 404 Not Found
        ResponseEntity<Account> responseDetail = client
                .getForEntity("/api/v1/accounts/5", Account.class);

        assertEquals(HttpStatus.NOT_FOUND, responseDetail.getStatusCode());
        assertFalse(responseDetail.hasBody());

    }



    /*
     * Método auxiliar para crear la URI completa (si usamos un puerto específico)
     * private String createURI(String uri) {
     * return "http://localhost:" + port + uri;
     * }
     */
}
