package com.javaricci.ExcelParaSQLiteJson.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.WorkbookFactory;


@Service
public class ExcelJsonService {

	private final String dbUrl = "jdbc:sqlite:JavaExcel.DB";

	public List<Map<String, Object>> leituraArquivoExcel(MultipartFile file) throws IOException {
	    List<Map<String, Object>> data = new ArrayList<>();
	    try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rows = sheet.iterator();
	        List<String> headers = new ArrayList<>();
	        if (rows.hasNext()) {
	            Row headerRow = rows.next();
	            headerRow.forEach(cell -> headers.add(cell.getStringCellValue()));
	        }
	        while (rows.hasNext()) {
	            Row row = rows.next();
	            Map<String, Object> rowData = new LinkedHashMap<>();
	            for (int i = 0; i < headers.size(); i++) {
	                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
	                switch (cell.getCellType()) {
	                    case STRING:
	                        rowData.put(headers.get(i), cell.getStringCellValue());
	                        break;
	                    case NUMERIC:
	                        rowData.put(headers.get(i), cell.getNumericCellValue());
	                        break;
	                    case BOOLEAN:
	                        rowData.put(headers.get(i), cell.getBooleanCellValue());
	                        break;
	                    default:
	                        rowData.put(headers.get(i), "");
	                }
	            }
	            data.add(rowData);
	        }
	    } catch (Exception e) {
	        throw new IOException("Erro ao ler o arquivo Excel: " + e.getMessage(), e);
	    }
	    return data;
	}
	
	public void criarTabelaDataBase(List<String> columns) {
	    try (Connection conn = DriverManager.getConnection(dbUrl); Statement stmt = conn.createStatement()) {
	        // Exclui a tabela existente, se houver
	        stmt.execute("DROP TABLE IF EXISTS Contratados");

	        // Constrói o comando SQL para criar a tabela
	        StringBuilder query = new StringBuilder(
	            "CREATE TABLE Contratados (id INTEGER PRIMARY KEY AUTOINCREMENT");
	        
	        for (String col : columns) {
	            query.append(", ").append(col.replaceAll("[^a-zA-Z0-9_]", "")).append(" TEXT");
	        }
	        query.append(");");
	        
	        stmt.execute(query.toString());
	        System.out.println("Tabela 'Contratados' criada com sucesso. " + query);
	    } catch (SQLException e) {
	        throw new RuntimeException("Erro ao criar o banco de dados.", e);
	    }
	}

	

	public void inserirDadosTabela(List<Map<String, Object>> data) {
	    if (data.isEmpty()) return;

	    try (Connection conn = DriverManager.getConnection(dbUrl)) {
	        conn.setAutoCommit(false);
	        
	        // Construir consulta de inserção
	        StringBuilder query = new StringBuilder("INSERT INTO Contratados (");
	        query.append(String.join(", ", data.get(0).keySet())).append(") VALUES (");
	        query.append(String.join(", ", Collections.nCopies(data.get(0).keySet().size(), "?"))).append(");");

	        System.out.println("Consulta SQL gerada: " + query.toString()); // Log da consulta SQL

	        try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
	            for (Map<String, Object> row : data) {
	                int index = 1;
	                for (String key : row.keySet()) {
	                    pstmt.setObject(index++, row.get(key));
	                }
	                pstmt.addBatch();
	            }
	            pstmt.executeBatch();
	            conn.commit();
	        }
	    } catch (SQLException e) {
	        // Registro da mensagem de erro detalhada
	        System.err.println("SQL Error Code: " + e.getErrorCode());
	        System.err.println("SQL State: " + e.getSQLState());
	        System.err.println("Error Message: " + e.getMessage());
	        throw new RuntimeException("Erro ao salvar no banco de dados.", e);
	    }
	}


	public String exportarParaJson() {
		JSONArray jsonArray = new JSONArray();
		try (Connection conn = DriverManager.getConnection(dbUrl); Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery("SELECT * FROM Contratados");
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					obj.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
				}
				jsonArray.put(obj);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao exportar JSON.", e);
		}
		return jsonArray.toString(4);
	}
}
