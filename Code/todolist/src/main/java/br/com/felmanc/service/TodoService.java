package br.com.felmanc.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.felmanc.entity.Todo;
import br.com.felmanc.repository.TodoRepository;

@Service
public class TodoService {
    Logger logger = Logger.getLogger(TodoService.class.getName());
	
	@Autowired
    private TodoRepository todoRepository;

    public List<Todo> getAllTodos() {
        return todoRepository.findAllSorted().stream()
                .filter(todo -> !todo.isRealizado())
                .collect(Collectors.toList());
    }
    
    public List<Todo> getAllDone() {
        return todoRepository.findAllSorted().stream()
                .filter(todo -> todo.isRealizado())
                .collect(Collectors.toList());
    }    
    
    public Todo getTodoById(@PathVariable Long id) {
        return todoRepository.findById(id).orElse(null);
    }

    public List<Todo> createTodo(@RequestBody Todo todo) {
        if (!todo.isRealizado()) {
        	todo.setRealizado(false);
        }

        if (todo.getPrioridade() == null || todo.getPrioridade().equals(0)) {
            todo.setPrioridade(1); // valor padrão
        }

        todoRepository.save(todo);
        
        return getAllTodos();
    }
    
    public List<Todo> updateTodo(@RequestBody Todo updatedTodo) {
        Todo todo = todoRepository.findById(updatedTodo.getId()).orElse(null);
        if (todo != null) {
            todo.setNome(updatedTodo.getNome());
            todo.setDescricao(updatedTodo.getDescricao());
            todo.setRealizado(updatedTodo.isRealizado());
            todoRepository.save(todo);
        }
        else {
        	throw new RuntimeException("Entity with id " + updatedTodo.getId() + " not found!");
        }
        	
        return getAllTodos();
    }

    public List<Todo> deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
        
        return getAllTodos();
    }
}
