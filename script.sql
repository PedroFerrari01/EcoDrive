CREATE DATABASE IF NOT EXISTS ecodrive_db;
USE ecodrive_db;

CREATE TABLE IF NOT EXISTS veiculos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    placa VARCHAR(20) NOT NULL UNIQUE,
    ano INT NOT NULL,
    quilometragem INT NOT NULL
);