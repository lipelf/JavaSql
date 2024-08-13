create database pokemon;
use pokemon;


CREATE TABLE tb_pokemon (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pokemon VARCHAR(50),
    tipo VARCHAR(50)
);


INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('picachu', 'eletrico');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('miraidon', 'eletrico');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('charmander', 'Fogo');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('fuecoco', 'Fogo');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('miraidon', 'elétrico');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('pidgeotto', 'voador');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('butterfree', 'voador');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('butterfree', 'voador');
INSERT INTO tb_pokemon (pokemon, tipo) VALUES ('fuecoco', 'fogo');


CREATE TABLE tb_pokemon_eletrico (
    id INT PRIMARY KEY,
    pokemon VARCHAR(50),
    tipo VARCHAR(50)
);

CREATE TABLE tb_pokemon_fogo (
    id INT PRIMARY KEY,
    pokemon VARCHAR(50),
    tipo VARCHAR(50)
);

CREATE TABLE tb_pokemon_voador (
    id INT PRIMARY KEY,
    pokemon VARCHAR(50),
    tipo VARCHAR(50)
);

CREATE TABLE tb_pokemon_totalizador (
    tipo VARCHAR(50) PRIMARY KEY,
    quantidade INT
);

CREATE TABLE tb_pokemon_deletados (
    id INT PRIMARY KEY,
    pokemon VARCHAR(50),
    tipo VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tb_pokemon_deletados (
    id INT PRIMARY KEY,
    pokemon VARCHAR(50),
    tipo VARCHAR(50)
);


INSERT INTO tb_pokemon_deletados (id, pokemon, tipo)
SELECT id, pokemon, tipo
FROM (
    SELECT id, pokemon, tipo, 
           ROW_NUMBER() OVER (PARTITION BY pokemon, tipo ORDER BY id) AS row_num
    FROM tb_pokemon
) sub
WHERE sub.row_num > 1;


DELETE FROM tb_pokemon
WHERE id IN (
    SELECT id
    FROM (
        SELECT id, ROW_NUMBER() OVER (PARTITION BY pokemon, tipo ORDER BY id) AS row_num
        FROM tb_pokemon
    ) sub
    WHERE sub.row_num > 1
);


SELECT 'tb_pokemon' AS tabela, tipo, COUNT(*) AS quantidade 
FROM tb_pokemon 
GROUP BY tipo
UNION
SELECT 'tb_pokemon_eletrico' AS tabela, tipo, COUNT(*) AS quantidade 
FROM tb_pokemon_eletrico 
GROUP BY tipo
UNION
SELECT 'tb_pokemon_fogo' AS tabela, tipo, COUNT(*) AS quantidade 
FROM tb_pokemon_fogo 
GROUP BY tipo
UNION
SELECT 'tb_pokemon_voador' AS tabela, tipo, COUNT(*) AS quantidade 
FROM tb_pokemon_voador 
GROUP BY tipo;

SELECT 'tb_pokemon' AS tabela, id, pokemon, tipo 
FROM tb_pokemon
UNION
SELECT 'tb_pokemon_eletrico' AS tabela, id, pokemon, tipo 
FROM tb_pokemon_eletrico
UNION
SELECT 'tb_pokemon_fogo' AS tabela, id, pokemon, tipo 
FROM tb_pokemon_fogo
UNION
SELECT 'tb_pokemon_voador' AS tabela, id, pokemon, tipo 
FROM tb_pokemon_voador;
