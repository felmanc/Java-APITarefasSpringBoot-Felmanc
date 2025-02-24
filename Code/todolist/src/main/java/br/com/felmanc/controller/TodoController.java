package br.com.felmanc.controller;

import java.time.LocalDate;
import java.util.List;

import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.felmanc.entity.Todo;
import br.com.felmanc.service.TodoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @GetMapping
    public List<Todo> getAllTasks() {
        return todoService.getAllTasks();
    }

    @GetMapping("/todo")
    public List<Todo> getAllTodos() {
        return todoService.getAllTodos();
    }
    
    @GetMapping("/done")
    public List<Todo> getAllDone() {
        return todoService.getAllDone();
    }
   
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        try {
            Todo todo = todoService.getTodoById(id);
            return ResponseEntity.ok(todo);
        } catch (OpenApiResourceNotFoundException ex) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public void createTodo(@RequestBody @Valid Todo todo) {
        if (todo.getDataCriacao() == null) {
            todo.setDataCriacao(LocalDate.now());
        }    	
        todoService.createTodo(todo);
    }

    @PutMapping
    public ResponseEntity<Void> updateTodo(@RequestBody Todo updatedTodo) {
        try {
            todoService.updateTodo(updatedTodo);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content para sucesso
        } catch (OpenApiResourceNotFoundException ex) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 Not Found sem body
        }        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        try {
            todoService.deleteTodo(id);
            return ResponseEntity.noContent().build(); // Retorna 204 sem corpo
        } catch (OpenApiResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 sem corpo
        }
    }
}
