package br.com.felmanc.unit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.felmanc.controller.TodoController;
import br.com.felmanc.entity.Todo;
import br.com.felmanc.service.TodoService;

@WebMvcTest(TodoController.class)
public class TodoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Test
    public void getAllTodos_ShouldReturnListOfTodos() throws Exception {
        List<Todo> todos = Arrays.asList(new Todo(1L, "Teste", "Descrição", false, 1));

        when(todoService.getAllTodos()).thenReturn(todos);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Teste")))
        		.andExpect(jsonPath("$[0].descricao", is("Descrição")));        
    }

    @Test
    public void getTodoById_ShouldReturnTodo() throws Exception {
        Todo todo = new Todo(1L, "Teste", "Descrição", false, 1);

        when(todoService.getTodoById(1L)).thenReturn(todo);

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Teste")));
    }

    
    @Test
    public void getAllDone_ShouldReturnListOfDoneTodos() throws Exception {
        List<Todo> todos = Arrays.asList(new Todo(1L, "Teste", "Descrição", true, 1));

        when(todoService.getAllDone()).thenReturn(todos);

        mockMvc.perform(get("/todos/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].realizado", is(true)));
    }
}
