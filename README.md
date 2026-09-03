# 🚗 EcoDrive — Sistema de Gestão Operacional de Frotas

Aplicação desktop desenvolvida em Java (Swing) e banco de dados relacional MySQL para controle operacional, monitoramento e gestão logística de frotas veiculares comerciais.

---

## 📌 Contexto e Problema de Negócio

No cenário de gestão logística e frotas comerciais, pequenas e médias empresas frequentemente enfrentam sérios problemas pela falta de controle centralizado sobre seus veículos. A ausência de um registro organizado de quilometragem, ano e identificação dos automóveis resulta em:
- Manutenções preventivas atrasadas;
- Depreciação acelerada dos ativos;
- Aumento imprevisto de custos operacionais.

O **EcoDrive** foi desenvolvido para solucionar essa dor: uma aplicação desktop ágil, intuitiva e segura que centraliza a frota sem a complexidade desnecessária de grandes ERPs, permitindo cadastrar, consultar, atualizar e remover veículos com persistência de dados em tempo real.

---

## 🏗️ Arquitetura e Engenharia do Software

O sistema adota uma arquitetura integrada, dividida em três pilares fundamentais:

- **Interface Gráfica (Camada Visual):** Construída com **Java Swing** (`JFrame`). Utiliza gerenciadores de layout (`BorderLayout` e `GridLayout`) combinando campos de entrada (`JTextField`) para captura de dados (Marca, Modelo, Placa, Ano e KM), tabela dinâmica com sincronização síncrona (`JTable` e `DefaultTableModel`) e escuta de eventos de linha (`ListSelectionListener`).
- **Camada de Conexão (Driver JDBC):** Comunicação estabelecida via protocolo TCP/IP (porta padrão 3306) utilizando o driver `mysql-connector-j` através da API nativa `java.sql` (`Connection`, `Statement`, `ResultSet`).
- **Camada de Persistência (Banco de Dados):** Banco de dados relacional `ecodrive_db` com a tabela `veiculos`, estruturada com chave primária autoincremental e constraint de unicidade (`UNIQUE`) para a placa do automóvel.

---

## 🔄 Fluxo de Operações (CRUD)

| Operação | Componente / Ação | Comando SQL / Regra |
| :--- | :--- | :--- |
| **Create (Salvar)** | Botão Salvar (novo registro) | Captura dados da tela, converte inteiros com `Integer.parseInt()` e executa `INSERT INTO veiculos (...)`. |
| **Read (Listar)** | Disparo de `atualizarTabela()` | Executa `SELECT * FROM veiculos` via `ResultSet`, percorrendo os registros e populando o grid visual. |
| **Update (Atualizar)** | Seleção na tabela + Botão Atualizar | Captura o ID do veículo selecionado, preenche os inputs e dispara `UPDATE veiculos SET ... WHERE id = X`. |
| **Delete (Excluir)** | Seleção na tabela + Botão Excluir | Remove permanentemente o registro do banco através de `DELETE FROM veiculos WHERE id = X`. |

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java (JDK 17+)
- **Interface Gráfica:** Java Swing
- **Banco de Dados:** MySQL
- **Conector de Banco:** MySQL Connector/J (`mysql-connector-j-9.7.0.jar`)
- **Versionamento:** Git & GitHub

---

## 🗄️ Estrutura do Banco de Dados

O script SQL para criação da base e tabela está disponível no repositório no arquivo [script.sql](./script.sql):

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

---

## 🚀 Passo a Passo — Como Rodar a Aplicação

### 1. Configurar o Banco de Dados no MySQL Workbench
1. Abra o **MySQL Workbench** e conecte-se à sua instância local.
2. Abra uma nova aba de consulta SQL, cole os comandos de criação do banco acima (ou abra o arquivo `script.sql`) e clique no ícone do Raio (**Execute**).
3. Verifique se as tabelas foram criadas com sucesso no Schema `ecodrive_db`.

### 2. Configurar a Senha no Código Java
1. Abra o projeto no **VS Code** (ou em sua IDE de preferência).
2. Abra o arquivo `EcoDriveApp.java`.
3. Verifique as credenciais de conexão nas variáveis de classe e ajuste a senha conforme a sua instalação local:
   - `private String url = "jdbc:mysql://localhost:3306/ecodrive_db";`
   - `private String usuario = "root";`
   - `private String senha = "root";`
4. Salve as alterações no arquivo (`Ctrl + S`).

### 3. Executar pelo Terminal / VS Code
1. Certifique-se de que o arquivo do conector `mysql-connector-j-9.7.0.jar` e o arquivo `EcoDriveApp.java` estejam no mesmo diretório.
2. Abra o terminal integrado do VS Code (`Terminal -> New Terminal`).
3. Execute o comando correspondente ao seu sistema operacional:

- **No Windows:**
  java -cp "mysql-connector-j-9.7.0.jar;." EcoDriveApp.java

- **No Linux / macOS:**
  java -cp "mysql-connector-j-9.7.0.jar:." EcoDriveApp.java

A janela gráfica do **EcoDrive** será inicializada pronta para uso.

---

## 📄 Material de Apresentação
Os slides completos com o estudo de caso e proposta do projeto estão disponíveis no arquivo [EcoDrive Apresentacao.pdf](./EcoDrive%20Apresentacao.pdf).
