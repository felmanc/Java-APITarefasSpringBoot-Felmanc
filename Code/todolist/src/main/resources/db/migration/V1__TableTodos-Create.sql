CREATE TABLE todos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    prioridade INT NOT NULL,
    realizado BOOLEAN NOT NULL,
    data_criacao DATE NOT NULL,
    data_vencimento DATE NOT NULL
);
