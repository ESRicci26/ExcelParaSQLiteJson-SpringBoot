# **Documentação do Projeto - ExcelParaSQLiteJson**

## **Resumo**
O projeto é uma aplicação Spring Boot desenvolvida para processar arquivos Excel, salvar os dados em um banco de dados SQLite e exportar as informações para um arquivo JSON.
O Botão Escolher arquivo permite que o usuário selecione um arquivo Excel com infinitas colunas e linhas.
Ao clicar no botão Carregar a função leituraArquivoExcel lê o conteúdo do arquivo Excel e exibe na Table do HTML todas as linhas contidas no EXCEL e usa como cabeçalho todas as
colunas que estiver na linha 01 desse arquivo que é gravada com o mesmo nome das colunas na tabela Contratados do Data Base.
Observação: Não deixar linhas em branco a partir da linha 02.
A interface web foi construída usando Thymeleaf e Bootstrap.

---

## **Estrutura do Projeto**

### **1. Pacotes**
- `com.javaricci.ExcelParaSQLiteJson`
  - Contém a classe principal `ExcelParaSQLiteJsonSpringBootApplication`.
  
- `com.javaricci.ExcelParaSQLiteJson.Controller`
  - Gerencia as requisições HTTP e o fluxo de informações entre o frontend e o backend.
  - Classe principal: `ExcelJsonController`.

- `com.javaricci.ExcelParaSQLiteJson.Service`
  - Contém a lógica de negócio para processar arquivos Excel, criar tabelas no banco de dados, salvar dados e exportar para JSON.
  - Classe principal: `ExcelJsonService`.

- `com.javaricci.ExcelParaSQLiteJson.Config`
  - Configura o suporte ao SQLite no Hibernate.
  - Classe principal: `SQLiteDialect`.

---

## **Dependências**
- **Spring Boot**:
  - Web, Data JPA.
- **SQLite**:
  - Banco de dados utilizado.
- **Apache POI**:
  - Processamento de arquivos Excel.
- **Bootstrap 5**:
  - Estilização do frontend.
- **Thymeleaf**:
  - Template engine para renderização de páginas HTML.

---

## **Principais Funcionalidades**

### **1. Upload de Arquivo Excel**
- **Rota**: `/upload`
- **Método HTTP**: `POST`
- **Descrição**:
  - Permite o envio de arquivos Excel.
  - Processa o conteúdo e exibe os dados na interface web.
- **Comportamento**:
  - Verifica se o arquivo está vazio.
  - Lê o conteúdo usando o Apache POI.
  - Exibe as colunas e os dados extraídos.

---

### **2. Criação da Tabela no Banco de Dados**
- **Rota**: `/create-database`
- **Método HTTP**: `POST`
- **Descrição**:
  - Cria a tabela `Contratados` no banco SQLite com base nas colunas extraídas do arquivo Excel.
- **Detalhes**:
  - O nome das colunas é sanitizado para evitar caracteres inválidos.
  - A tabela é recriada sempre que este endpoint é chamado.

---

### **3. Salvar Dados no Banco de Dados**
- **Rota**: `/save-to-database`
- **Método HTTP**: `POST`
- **Descrição**:
  - Insere os dados extraídos do arquivo Excel na tabela `Contratados`.
- **Comportamento**:
  - Prepara a consulta SQL para inserção.
  - Realiza a inserção em batch para eficiência.

---

### **4. Exportação para JSON**
- **Rota**: `/export-json`
- **Método HTTP**: `GET`
- **Descrição**:
  - Exporta os dados da tabela `Contratados` para um arquivo JSON.
- **Comportamento**:
  - Cria um JSON formatado com os dados do banco.
  - Permite o download direto pelo navegador.

---

## **Frontend**

### **Página Principal**
- **URL**: `http://localhost:8080/`
- **Recursos**:
  - Formulário para upload de arquivos Excel.
  - Tabela dinâmica para exibir os dados extraídos.
  - Botões para:
    1. Criar Banco de Dados.
    2. Criar Tabela.
    3. Salvar Dados.
    4. Exportar JSON.

- **Scripts JavaScript**:
  - Funções para comunicação com os endpoints da aplicação via `fetch`.
  - Permite criar banco, salvar dados e exportar JSON diretamente da interface.

---

## **Configurações**
### **Arquivo `application.properties`**
```properties
spring.application.name=ExcelParaSQLiteJson-SpringBoot
spring.datasource.url=jdbc:sqlite:JavaExcel.DB
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=com.javaricci.ExcelParaSQLiteJson.Config.SQLiteDialect
spring.jpa.hibernate.ddl-auto=none
```

---

## **Como Executar**

1. Certifique-se de ter o Java 17 e o Maven instalados.
2. Clone o repositório ou baixe o projeto.
3. Compile e inicie o servidor:
   ```bash
   mvn spring-boot:run
   ```
4. Acesse a aplicação no navegador: `http://localhost:8080/`.

---

## **Requisitos**
- **Java**: Versão 17 ou superior.
- **SQLite**: Incluído no projeto como dependência do Hibernate.
- **Apache POI**: Para manipulação de arquivos Excel.

---
