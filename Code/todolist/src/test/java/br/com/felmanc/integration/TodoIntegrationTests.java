package br.com.felmanc.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoIntegrationTests {

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void getAllTodos_ShouldReturnListOfTodos() {
        given()
            .when()
            .get("/todos")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", equalTo(10))
            .body("[0].nome", equalTo("Todo 607"));
    }

    @Test
    public void getTodoById_ShouldReturnTodo() {
        given()
            .when()
            .get("/todos/1")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("nome", equalTo("Todo 1"))
            .body("realizado", equalTo(false));
    }

    @Test
    public void getAllDone_ShouldReturnListOfDoneTodos() {
        given()
            .when()
            .get("/todos/done")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", equalTo(1))
            .body("[0].realizado", equalTo(true));
    }
}


