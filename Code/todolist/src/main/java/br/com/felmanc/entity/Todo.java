package br.com.felmanc.entity;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "todos")
public class Todo {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    @NotNull
	    @NotBlank
	    private String nome;
	    @NotNull
	    @NotBlank
	    private String descricao;
	    private boolean realizado;
	    private Integer prioridade;
	    @Column(name = "data_criacao", nullable = false)
	    @JsonProperty("data_criacao")
	    private LocalDate dataCriacao = LocalDate.now();
	    @Column(name = "data_vencimento", nullable = false)
	    @JsonProperty("data_vencimento")
	    private LocalDate dataVencimento;
	    
	    public Todo() {}

		public Todo(Long id, @NotNull @NotBlank String nome, @NotNull @NotBlank String descricao, boolean realizado,
				Integer prioridade, LocalDate dataCriacao, LocalDate dataVencimento) {
			this.id = id;
			this.nome = nome;
			this.descricao = descricao;
			this.realizado = realizado;
			this.prioridade = prioridade;
			this.dataCriacao = dataCriacao;
			this.dataVencimento = dataVencimento;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getDescricao() {
			return descricao;
		}

		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}

		public boolean isRealizado() {
			return realizado;
		}

		public void setRealizado(boolean realizado) {
			this.realizado = realizado;
		}

		public Integer getPrioridade() {
			return prioridade;
		}

		public void setPrioridade(Integer prioridade) {
			this.prioridade = prioridade;
		}

		public LocalDate getDataCriacao() {
			return dataCriacao;
		}

		public void setDataCriacao(LocalDate dataCriacao) {
			this.dataCriacao = dataCriacao;
		}

		public LocalDate getDataVencimento() {
			return dataVencimento;
		}

		public void setDataVencimento(LocalDate dataVencimento) {
			this.dataVencimento = dataVencimento;
		}
}