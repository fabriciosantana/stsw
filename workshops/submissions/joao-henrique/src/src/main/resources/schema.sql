-- Cria a tabela em cada banco temporário preparado pela fixture dos testes.
CREATE TABLE usuarios (
    -- O PostgreSQL gera o ID. PRIMARY KEY impede IDs repetidos ou nulos.
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    -- TEXT guarda o e-mail; NOT NULL exige um valor; UNIQUE rejeita duplicados.
    email TEXT NOT NULL UNIQUE
);
