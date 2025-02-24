package br.com.felmanc.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TodoIntegrationTests {
	private final Logger logger = LoggerFactory.getLogger(TodoIntegrationTests.class);

	@org.springframework.boot.test.web.server.LocalServerPort
	private int port;

	@BeforeEach
	public void setUp() {
		RestAssured.port = port;
	}

	private void logInicio(String str) {
		logger.info(">>> Iniciando teste: " + str);
	}

	private void logFim(String str) {
		logger.info("<<< Finalizado teste: " + str);
	}

	@Test
	@Order(1)
    public void getAllTasks_ShouldReturnListOfTasks() {
        logInicio("getAllTasks_ShouldReturnListOfTasks");

        // Enviar a requisição e obter a resposta JSON
        String response = given().filter(new RequestLoggingFilter()) // Logar a requisição
                .filter(new ResponseLoggingFilter()) // Logar a resposta
                .when()
                .get("/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .asString();

        // Usar JsonPath para manipular o JSON
        JsonPath jsonPath = new JsonPath(response);

        // Verificar o tamanho da lista
        int size = jsonPath.getList("$").size();
        assertThat(size, equalTo(11));

        // Regex para verificar o formato de data "yyyy-MM-dd"
        String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // Padrão para ano-mês-dia

        // Verificar os detalhes da primeira tarefa
        assertThat(jsonPath.getString("[0].nome"), equalTo("Manutenção Carro"));
        assertThat(jsonPath.getString("[0].descricao"), equalTo("Realizar a manutenção preventiva do carro"));
        assertThat(jsonPath.getBoolean("[0].realizado"), equalTo(false));
        assertThat(jsonPath.getInt("[0].prioridade"), equalTo(11));
        assertThat(jsonPath.getString("[0].data_criacao"), matchesPattern(datePattern));
        assertThat(jsonPath.getString("[0].data_vencimento"), equalTo("2025-01-30"));

        // Verificar os detalhes de uma tarefa específica (exemplo da tarefa com id 8)
        // Usar uma expressão simples para filtrar a tarefa com id 8
        String task8Nome = jsonPath.getString("find { it.id == 8 }.nome");
        if (task8Nome != null) {
            assertThat(task8Nome, equalTo("Webinar TI"));
            assertThat(jsonPath.getString("find { it.id == 8 }.descricao"),
                    equalTo("Assistir ao webinar sobre as novas tendências em tecnologia da informação"));
            assertThat(jsonPath.getBoolean("find { it.id == 8 }.realizado"), equalTo(true));
            assertThat(jsonPath.getInt("find { it.id == 8 }.prioridade"), equalTo(8));
            assertThat(jsonPath.getString("find { it.id == 8 }.data_criacao"), matchesPattern(datePattern));
            assertThat(jsonPath.getString("find { it.id == 8 }.data_vencimento"), equalTo("2025-01-25"));
        }

        logFim("getAllTasks_ShouldReturnListOfTasks");
    }

	@Test
	@Order(2)
	public void getTasksById_ShouldReturnTask() {
	    logInicio("getTasksById_ShouldReturnTask");

	    // Enviar a requisição e obter a resposta JSON
	    String response = given().filter(new RequestLoggingFilter()) // Logar a requisição
	            .filter(new ResponseLoggingFilter()) // Logar a resposta
	            .when()
	            .get("/tasks/1")
	            .then()
	            .statusCode(200)
	            .contentType(ContentType.JSON)
	            .extract()
	            .asString();

	    // Usar JsonPath para manipular o JSON
	    JsonPath jsonPath = new JsonPath(response);

	    // Regex para verificar o formato de data "yyyy-MM-dd"
	    String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // Padrão para ano-mês-dia

	    // Validar os detalhes da tarefa retornada
	    assertThat(jsonPath.getInt("id"), equalTo(1));
	    assertThat(jsonPath.getString("nome"), equalTo("Reunião Marketing"));
	    assertThat(jsonPath.getString("descricao"), equalTo("Reunião com o time de marketing para discutir estratégias para a próxima campanha"));
	    assertThat(jsonPath.getBoolean("realizado"), equalTo(false));
	    assertThat(jsonPath.getInt("prioridade"), equalTo(1));
	    assertThat(jsonPath.getString("data_criacao"), matchesPattern(datePattern));
	    assertThat(jsonPath.getString("data_vencimento"), equalTo("2025-02-20"));

	    logFim("getTasksById_ShouldReturnTask");
	}

	@Test
	@Order(3)
	public void getTasksById_ErrNotFound() {
	    logInicio("getTasksById_ErrNotFound");

	    // Enviando a requisição e capturando a resposta
	    given().filter(new RequestLoggingFilter()) // Logar a requisição
	            .filter(new ResponseLoggingFilter()) // Logar a resposta
	            .when().
	            get("/tasks/12").
	            then()
	            .statusCode(404);

	    logFim("getTasksById_ErrNotFound");
	}

	@Test
	@Order(4)
	public void getTasksById_ErrBadRequest() {
	    logInicio("getTasksById_ErrBadRequest");

	    // Enviando a requisição e capturando a resposta
	    given().filter(new RequestLoggingFilter()) // Logar a requisição
	            .filter(new ResponseLoggingFilter()) // Logar a resposta
	            .when()
	            .get("/tasks/7-00")
	            .then()
	            .statusCode(400);

	    logFim("getTasksById_ErrBadRequest");
	}
	
	@Test
	@Order(5)
	public void getAllTasks_ShouldReturnListOfTodos() {
		logInicio("getAllTasks_ShouldReturnListOfTodos");

	    // Enviar a requisição e obter a resposta JSON
	    String response = given()
	            .filter(new RequestLoggingFilter()) // Logar a requisição
	            .filter(new ResponseLoggingFilter()) // Logar a resposta
	            .when()
	            .get("/tasks/todo")
	            .then()
	            .statusCode(200)
	            .contentType(ContentType.JSON)
	            .extract()
	            .asString();

	    // Usar JsonPath para manipular o JSON
	    JsonPath jsonPath = new JsonPath(response);

	    // Regex para verificar o formato de data "yyyy-MM-dd"
	    String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // Padrão para ano-mês-dia

	    // Validar o primeiro registro com ID 11
	    assertThat(jsonPath.getInt("[0].id"), equalTo(11));
	    assertThat(jsonPath.getString("[0].nome"), equalTo("Manutenção Carro"));
	    assertThat(jsonPath.getString("[0].descricao"), equalTo("Realizar a manutenção preventiva do carro"));
	    assertThat(jsonPath.getBoolean("[0].realizado"), equalTo(false));
	    assertThat(jsonPath.getInt("[0].prioridade"), equalTo(11));
	    assertThat(jsonPath.getString("[0].data_criacao"), equalTo("2025-01-05"));
	    assertThat(jsonPath.getString("[0].data_vencimento"), equalTo("2025-01-30"));
	    
	    // Validar um outro registro específico (id 4)
	    String task4Nome = jsonPath.getString("find { it.id == 4 }.nome");
	    if (task4Nome != null) {
	        assertThat(task4Nome, equalTo("Consulta Médica"));
	        assertThat(jsonPath.getString("find { it.id == 4 }.descricao"), equalTo("Agendar uma consulta médica anual com o clínico geral"));
	        assertThat(jsonPath.getBoolean("find { it.id == 4 }.realizado"), equalTo(false));
	        assertThat(jsonPath.getInt("find { it.id == 4 }.prioridade"), equalTo(4));
	        assertThat(jsonPath.getString("find { it.id == 4 }.data_criacao"), matchesPattern(datePattern));
	        assertThat(jsonPath.getString("find { it.id == 4 }.data_vencimento"), equalTo("2025-03-01"));
	    }

	    // Validar que não há nenhum registro com "realizado" diferente de false
	    List<Boolean> realizados = jsonPath.getList("realizado", Boolean.class);
	    assertThat(realizados, everyItem(equalTo(false)));

		logFim("getAllTasks_ShouldReturnListOfTodos");
	}

	@Test
	@Order(6)
	public void getAllDone_ShouldReturnListOfDone() {
	    logInicio("getAllDone_ShouldReturnListOfDone");

	    // Enviar a requisição e obter a resposta JSON
	    String response = given()
	            .filter(new RequestLoggingFilter()) // Logar a requisição
	            .filter(new ResponseLoggingFilter()) // Logar a resposta
	            .when()
	            .get("/tasks/done")
	            .then()
	            .statusCode(200)
	            .contentType(ContentType.JSON)
	            .extract()
	            .asString();

	    // Usar JsonPath para manipular o JSON
	    JsonPath jsonPath = new JsonPath(response);

	    // Regex para verificar o formato de data "yyyy-MM-dd"
	    String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // Padrão para ano-mês-dia

	    // Validar o primeiro registro
	    assertThat(jsonPath.getInt("[0].id"), equalTo(10));
	    assertThat(jsonPath.getString("[0].nome"), equalTo("Reunião Conselho"));
	    assertThat(jsonPath.getString("[0].descricao"), equalTo("Participar da reunião do conselho de administração da empresa"));
	    assertThat(jsonPath.getBoolean("[0].realizado"), equalTo(true));
	    assertThat(jsonPath.getInt("[0].prioridade"), equalTo(10));
	    assertThat(jsonPath.getString("[0].data_criacao"), matchesPattern(datePattern));
	    assertThat(jsonPath.getString("[0].data_vencimento"), equalTo("2025-02-13"));

	    // Validar um outro registro específico (id 6)
	    String task6Nome = jsonPath.getString("find { it.id == 6 }.nome");
	    if (task6Nome != null) {
	        assertThat(task6Nome, equalTo("Comprar Presentes"));
	        assertThat(jsonPath.getString("find { it.id == 6 }.descricao"), equalTo("Comprar presentes para o aniversário de casamento"));
	        assertThat(jsonPath.getBoolean("find { it.id == 6 }.realizado"), equalTo(true));
	        assertThat(jsonPath.getInt("find { it.id == 6 }.prioridade"), equalTo(6));
	        assertThat(jsonPath.getString("find { it.id == 6 }.data_criacao"), matchesPattern(datePattern));
	        assertThat(jsonPath.getString("find { it.id == 6 }.data_vencimento"), equalTo("2025-02-14"));
	    }

	    // Validar que não há nenhum registro com "realizado" diferente de true
	    List<Boolean> realizados = jsonPath.getList("realizado", Boolean.class);
	    assertThat(realizados, everyItem(equalTo(true)));	    
	    
	    logFim("getAllDone_ShouldReturnListOfDone");
	}

	@Test
	@Order(7)
	@DirtiesContext
	public void createTodo_ShouldReturnListOfTodos() {
		logInicio("createTodo_ShouldReturnListOfTodos");

		Map<String, Object> newTodo = new HashMap<>();
		newTodo.put("descricao", "Estudar para a prova de certificação de Java");
		newTodo.put("nome", "Estudar Java");
		newTodo.put("realizado", false);
		newTodo.put("prioridade", 2);
		newTodo.put("data_criacao", "2025-02-18");
		newTodo.put("data_vencimento", "2025-02-25");

		logger.info("Iniciando o teste de criação de Todo");
		logger.debug("Dados do novo Todo: {}", newTodo);

		// Create the new Todo
		given().filter(new RequestLoggingFilter()) // Logar a requisição
				.filter(new ResponseLoggingFilter()) // Logar a resposta
				.contentType(ContentType.JSON).body(newTodo).when().post("/tasks").then().statusCode(200);

		logger.info("Teste de criação de Todo executado por completo");

		// Validate the new Todo was inserted
		given()
		    .filter(new RequestLoggingFilter()) // Logar a requisição
		    .filter(new ResponseLoggingFilter()) // Logar a resposta
		    .contentType(ContentType.JSON)
		    .when()
		    .get("/tasks/12")
		    .then()
		    .statusCode(200)
		    .contentType(ContentType.JSON)
		    .body("nome", equalTo("Estudar Java"))
		    .body("descricao", equalTo("Estudar para a prova de certificação de Java"))
		    .body("prioridade", equalTo(2))
		    .body("realizado", equalTo(false))
		    .body("data_criacao", equalTo("2025-02-18"))
		    .body("data_vencimento", equalTo("2025-02-25"));

		logFim("createTodo_ShouldReturnListOfTodos");
	}

	@Test
	@Order(8)
	@DirtiesContext
	public void createTodo_ErrBadRequest() {
		logInicio("createTodo_ErrBadRequest");

		Map<String, Object> newTodo = new HashMap<>();
		newTodo.put("descricao", "Estudar para a prova de certificação de Java");
		// newTodo.put("nome", "Estudar Java");
		newTodo.put("realizado", false);
		newTodo.put("prioridade", 2);
		newTodo.put("data_criacao", "2025-02-18");
		newTodo.put("data_vencimento", "2025-02-25");

		logger.debug("Dados do novo Todo: {}", newTodo);

		// Create the new Todo
		given().filter(new RequestLoggingFilter()) // Logar a requisição
				.filter(new ResponseLoggingFilter()) // Logar a resposta
				.contentType(ContentType.JSON).body(newTodo)
				.when()
				.post("/tasks")
				.then()
				.statusCode(400);

		logFim("createTodo_ErrBadRequest");
	}

	@Test
	@Order(9)
	@DirtiesContext
	public void updateTask_ShouldReturnUpdatedTodo() {
	    logInicio("updateTask_ShouldReturnUpdatedTodo");

	    // Valida os dados antigos do ID 6
	    given().filter(new RequestLoggingFilter())
	            .filter(new ResponseLoggingFilter())
	            .when().get("/tasks/6")
	            .then().statusCode(200)
	            .contentType(ContentType.JSON)
	            .body("id", equalTo(6))
	            .body("nome", equalTo("Comprar Presentes"))
	            .body("descricao", equalTo("Comprar presentes para o aniversário de casamento"))
	            .body("realizado", equalTo(true))
	            .body("prioridade", equalTo(6))
	            .body("data_criacao", equalTo("2025-02-05"))
	            .body("data_vencimento", equalTo("2025-02-14"));

	    // Dados da tarefa atualizada
	    String updatedTaskJson = "{\n"
	            + "        \"id\": 6,\n"
	            + "        \"nome\": \"Reunião Estratégica\",\n"
	            + "        \"descricao\": \"Reunião com o time de vendas para discutir as estratégias para o próximo trimestre\",\n"
	            + "        \"realizado\": true,\n"
	            + "        \"prioridade\": 2,\n"
	            + "        \"data_criacao\": \"2025-02-18\",\n"
	            + "        \"data_vencimento\": \"2025-03-01\"\n"
	            + "    }";

	    // Realiza o PUT para atualizar a tarefa
	    given().filter(new RequestLoggingFilter())
	            .filter(new ResponseLoggingFilter())
	            .contentType(ContentType.JSON).body(updatedTaskJson)
	            .when().put("/tasks")
	            .then().statusCode(204);

	    // Valida com um GET os dados após o update
	    given().filter(new RequestLoggingFilter())
	            .filter(new ResponseLoggingFilter())
	            .when().get("/tasks/6")
	            .then().statusCode(200)
	            .contentType(ContentType.JSON)
	            .body("id", equalTo(6))
	            .body("nome", equalTo("Reunião Estratégica"))
	            .body("descricao", equalTo("Reunião com o time de vendas para discutir as estratégias para o próximo trimestre"))
	            .body("realizado", equalTo(true))
	            .body("prioridade", equalTo(2))
	            .body("data_vencimento", equalTo("2025-03-01"));

	    logFim("updateTask_ShouldReturnUpdatedTodo");
	}

	@Test
	@Order(10)
	public void updateTask_ErrNotFound() {
		logInicio("updateTask_ErrNotFound");

		// Dados da tarefa atualizada, incluindo o ID para uma tarefa inexistente
	    String updatedTaskJson = "{\n"
	            + "        \"id\": 22,\n"
	            + "        \"nome\": \"Reunião Estratégica\",\n"
	            + "        \"descricao\": \"Reunião com o time de vendas para discutir as estratégias para o próximo trimestre\",\n"
	            + "        \"realizado\": true,\n"
	            + "        \"prioridade\": 2,\n"
	            + "        \"data_criacao\": \"2025-02-18\",\n"
	            + "        \"data_vencimento\": \"2025-03-01\"\n"
	            + "    }";

		given().filter(new RequestLoggingFilter()) // Logar a requisição
				.filter(new ResponseLoggingFilter()) // Logar a resposta
				.contentType(ContentType.JSON)
				.body(updatedTaskJson)
				.when()
				.put("/tasks")
				.then()
				.statusCode(404);

		logFim("updateTask_ErrNotFound");
	}

	@Test
	@Order(11)
	@DirtiesContext
	public void deleteTaskById_ShouldRemoveTask() {
	    logInicio("deleteTaskById_ShouldRemoveTask");

	    // Enviar a requisição e obter a resposta JSON
	    given()
	        .filter(new RequestLoggingFilter()) // Logar a requisição
	        .filter(new ResponseLoggingFilter()) // Logar a resposta
	        .when()
	        .get("/tasks/9")
	        .then()
	        .statusCode(200)
	        .contentType(ContentType.JSON);

	    // Deletar a tarefa com ID 9
	    given()
	        .filter(new RequestLoggingFilter()) // Logar a requisição
	        .filter(new ResponseLoggingFilter()) // Logar a resposta
	        .when()
	        .delete("/tasks/9")
	        .then()
	        .statusCode(204); // Espera-se um código 204 (No Content) para deleção bem-sucedida

	    // Tentar buscar a tarefa deletada e validar que não existe mais
	    given()
	        .filter(new RequestLoggingFilter())
	        .filter(new ResponseLoggingFilter())
	        .when()
	        .get("/tasks/9")
	        .then()
	        .statusCode(404); // Espera-se um código 404 (Not Found)

	    logFim("deleteTaskById_ShouldRemoveTask");
	}
	
	@Test
	@Order(12)	
	public void deleteTaskById_ErrNotFound() {
	    logInicio("deleteTaskById_ErrNotFound");

	    // Deletar a tarefa com ID 28
	    given()
	        .filter(new RequestLoggingFilter())
	        .filter(new ResponseLoggingFilter())
	        .when()
	        .delete("/tasks/28")
	        .then()
	        .statusCode(404); // Esperado para deleção bem-sucedida

	    logFim("deleteTaskById_ErrNotFound");
	}
}
