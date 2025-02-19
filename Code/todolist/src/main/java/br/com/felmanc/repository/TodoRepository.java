package br.com.felmanc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.felmanc.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
	@Query("SELECT t FROM Todo t ORDER BY t.prioridade DESC, t.nome ASC")
	List<Todo> findAllSorted();
}
