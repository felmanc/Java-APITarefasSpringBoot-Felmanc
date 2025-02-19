package br.com.felmanc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.felmanc.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
