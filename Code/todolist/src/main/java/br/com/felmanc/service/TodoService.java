package br.com.felmanc.service;

import java.util.List;
import java.util.logging.Logger;

import org.springdoc.api.OpenApiResourceNotFoundException;
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

    public List<Todo> getAllTasks() {
        return todoRepository.findAllSorted();
    }	

    private List<Todo> getAllByStatus(boolean status) {
    	return todoRepository.findSortedByStatus(status);
    }
    
    public List<Todo> getAllTodos() {
    	return getAllByStatus(false);
    }
    
    public List<Todo> getAllDone() {
    	return getAllByStatus(true);
    }    
    
    public Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Tarefa não encontrada com o id: " + id));
    }

    public List<Todo> createTodo(@RequestBody Todo todo) {
        if (!todo.isRealizado()) {
        	todo.setRealizado(false);
        }

        if (todo.getPrioridade() == null || todo.getPrioridade().equals(0)) {
            todo.setPrioridade(1); // valor padrão
        }

        todoRepository.save(todo);
        
        return getAllTasks();
    }
    
    public List<Todo> updateTodo(@RequestBody Todo updatedTodo) {
    	Todo todo = getTodoById(updatedTodo.getId());

    	if (todo != null) {
            todoRepository.save(updatedTodo);
        }
        	
        return getAllTasks();
    }

    public List<Todo> deleteTodo(@PathVariable Long id) {
    	getTodoById(id);
        
    	todoRepository.deleteById(id);
        
        return getAllTasks();
    }
}
