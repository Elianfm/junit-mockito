package com.elianfm.test.springboot.app.controllers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

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
@Tag("integration_wc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Permite ordenar los tests por anotaciones @Order
@SpringBootTest( // A diferencia de @WebMvcTest, esta anotación carga toda la aplicación
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT // Usa un puerto aleatorio para evitar conflictos
)
public class AccountControllerWebTestClientTest {

    // Para pruebas de integración con WebTestClient, es necesario que
    // la aplicación esté levantada para poder realizar las peticiones HTTP.

    // Este tipo de pruebas levanta su propia instancia del servidor
    // y permite realizar peticiones HTTP a los endpoints definidos en la
    // aplicación.

    // Es importante que los datos no hayan sido modificados por otros
    // procesos o pruebas, ya que esto podría afectar los resultados
    // de las pruebas. Por lo tanto, es recomendable partir de cero y
    // reiniciar el servidor antes de cada prueba para asegurar un estado limpio.

    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Normamente solo hace falta el new ObjectMapper(), pero
        // para trabajar con fechas de tipo LocalDate, es necesario
        // registrar el módulo JavaTimeModule y desactivar la serialización
        // de fechas como timestamps para que se serialicen como cadenas.
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @Order(1) // Ordena este test para que se ejecute primero
    void testTranfer() throws JsonProcessingException {

        // Given
        TransactionDTO transaction = new TransactionDTO();
        transaction.setOriginId(1L);
        transaction.setDestinationId(2L);
        transaction.setBankId(1L);
        transaction.setAmount(new BigDecimal(100));

        Map<String, Object> response = Map.of(
                "date", LocalDate.now(),
                "status", "ok",
                "message", "Transaction completed successfully",
                "transaction", transaction);

        // When
        client.post()
                // .uri("http://localhost:8080/api/v1/accounts/transaction")
                .uri("/api/v1/accounts/transaction") // De esta forma, en lugar de levantar dos instances del servidor,
                                                     // se usa la instancia que ya está levantada por Spring Boot,
                                                     // siempre y cuando el test este dentro del mismo contexto de la
                                                     // aplicación.
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transaction) // No es necesario usar .bodyValue(transaction.toString())
                .exchange() // Realiza la petición y espera la respuesta
                // Then
                .expectStatus().isOk() // Verifica que el estado de la respuesta sea 200 OK
                .expectBody() // Verifica el cuerpo de la respuesta
                .consumeWith(responseSpec -> {
                    // En lugar de usar .jsonPath o similar, se puede usar
                    // consumeWith para realizar aserciones más complejas
                    try {
                        JsonNode json = objectMapper.readTree(responseSpec.getResponseBody());
                        assertEquals("Transaction completed successfully", json.path("message").asText());
                        assertEquals(1L, json.path("transaction").path("originId").asLong());
                        assertEquals(2L, json.path("transaction").path("destinationId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                // Distintas formas de verificar el cuerpo de la respuesta con jsonPath
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(is("Transaction completed successfully"))
                .jsonPath("$.message").value(value -> {
                    assertEquals("Transaction completed successfully", value);
                })
                .jsonPath("$.message").isEqualTo("Transaction completed successfully")
                .jsonPath("$.transaction.originId").isEqualTo(transaction.getOriginId())
                .jsonPath("$.transaction.destinationId").isEqualTo(2L)
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response)); // Comparamos directamente con el objeto esperado

    }

    @Test
    @Order(2)
    void testDetail() throws JsonProcessingException {
        // Given
        Long accountId = 1L;
        Account account = new Account(1L, "John Doe", new BigDecimal("900.00"));

        // When
        client.get()
                .uri("/api/v1/accounts/{id}", accountId)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(accountId)
                .jsonPath("$.person").isEqualTo("John Doe")
                .jsonPath("$.balance").isEqualTo(900.00)
                .json(objectMapper.writeValueAsString(account));
    }

    @Test
    @Order(3)
    void testDetail2() {
        // Given
        Long accountId = 2L;

        // When
        client.get()
                .uri("/api/v1/accounts/{id}", accountId)
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class) // Aquí se espera que el cuerpo de la respuesta sea del tipo Account
                .consumeWith(responseSpec -> {
                    Account account = responseSpec.getResponseBody();
                    assertEquals(accountId, account.getId());
                    assertEquals("Jane Smith", account.getPerson());
                    assertEquals(new BigDecimal("2100.00"), account.getBalance());
                });
    }

    @Test
    @Order(4)
    void testList() {
        // When
        client.get()
                .uri("/api/v1/accounts")
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1L)
                .jsonPath("$[0].person").isEqualTo("John Doe")
                .jsonPath("$[0].balance").isEqualTo(900.00)
                .jsonPath("$[1].id").isEqualTo(2L)
                .jsonPath("$[1].person").isEqualTo("Jane Smith")
                .jsonPath("$[1].balance").isEqualTo(2100.00)
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(4);
    }

    @Test
    @Order(5)
    void testList2() {
        // When
        client.get()
                .uri("/api/v1/accounts")
                .exchange()
                // Then
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class) // Usamos expectBodyList para esperar una lista de objetos
                .consumeWith(response -> {
                    List<Account> accounts = response.getResponseBody();
                    assertNotNull(accounts);
                    assertEquals(4, accounts.size());
                    assertEquals(1L, accounts.get(0).getId());
                    assertEquals("John Doe", accounts.get(0).getPerson());
                    assertEquals(new BigDecimal("900.00"), accounts.get(0).getBalance());
                    assertEquals(2L, accounts.get(1).getId());
                    assertEquals("Jane Smith", accounts.get(1).getPerson());
                    assertEquals(new BigDecimal("2100.00"), accounts.get(1).getBalance());
                })
                .hasSize(4);
    }

    @Test
    @Order(6)
    void testSave() {
        // Given
        Account account = new Account(null, "New Account", new BigDecimal("500.00"));

        client.post()
                .uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account) // Enviamos el objeto Account como cuerpo de la petición)
                .exchange()
                // Then
                .expectStatus().isCreated() // Verifica que el estado de la respuesta sea 201 Created
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody() // Espera que el cuerpo de la respuesta sea del tipo Account
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.id").isEqualTo(5) // Asumiendo que el ID se asigna automáticamente y es 5
                .jsonPath("$.person").isEqualTo("New Account")
                .jsonPath("$.balance").isEqualTo(500.00);
    }

    @Test
    @Order(7)
    void testSave2() {
        // Given
        Account account = new Account(null, "Another Account", new BigDecimal("300.00"));

        client.post()
                .uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account savedAccount = response.getResponseBody();
                    assertNotNull(savedAccount);
                    assertEquals("Another Account", savedAccount.getPerson());
                    assertEquals(new BigDecimal("300.00"), savedAccount.getBalance());
                    assertEquals(6L, savedAccount.getId()); // Asumiendo que el ID se asigna automáticamente y es 6
                });
    }

    @Test
    @Order(8)
    void testDelete() {
        // Deberíamos tener 6 cuentas antes de eliminar una, por lo que
        // al eliminar una, deberíamos quedarnos con 5 cuentas.
        client.get()
                .uri("/api/v1/accounts")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(6);

        Long accountId = 1L;

        // Eliminamos la cuenta y verificamos que se elimina correctamente
        client.delete()
                .uri("/api/v1/accounts/{id}", accountId)
                .exchange()
                .expectStatus().isNoContent() // Verifica que el estado de la respuesta sea 204 No Content
                .expectBody().isEmpty(); // Verifica que el cuerpo de la respuesta esté vacío

        // Verificamos que la cuenta ha sido eliminada
        client.get()
                .uri("/api/v1/accounts")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(5);

        // Verificamos que la cuenta eliminada ya no existe
        client.get()
                .uri("/api/v1/accounts/{id}", accountId)
                .exchange()
                //.expectStatus().is5xxServerError() // Por defecto
                .expectStatus().isNotFound(); // Esperamos un 404 porque cambiamos el comportamiento del controlador
    }

}
