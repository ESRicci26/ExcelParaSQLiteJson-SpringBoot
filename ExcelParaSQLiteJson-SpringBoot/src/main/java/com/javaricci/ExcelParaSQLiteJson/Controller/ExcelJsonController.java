package com.javaricci.ExcelParaSQLiteJson.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.javaricci.ExcelParaSQLiteJson.Service.ExcelJsonService;

import java.util.*;

@Controller
@RequestMapping("/")
public class ExcelJsonController {

	@Autowired
	private ExcelJsonService service;

	@GetMapping
	public String index() {
		return "index";
	}


	@PostMapping("/upload")
	public String uploadExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
	    List<Map<String, Object>> data = new ArrayList<>();
	    try {
	        if (file.isEmpty()) {
	            redirectAttributes.addFlashAttribute("error", "Nenhum arquivo foi enviado.");
	            return "redirect:/";
	        }
	        data = service.leituraArquivoExcel(file);
	        redirectAttributes.addFlashAttribute("data", data);
	        redirectAttributes.addFlashAttribute("columns", data.isEmpty() ? Collections.emptyList() : data.get(0).keySet());
	        redirectAttributes.addFlashAttribute("message", "Arquivo carregado com sucesso.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Erro ao processar o arquivo: " + e.getMessage());
	    }
	    return "redirect:/";
	}


	//Cria a Tabela Contratados
	@PostMapping("/create-database")
	public ResponseEntity<String> createDatabase(@RequestParam("columns") List<String> columns) {
		service.criarTabelaDataBase(columns);
		return ResponseEntity.ok("Tabela criada com sucesso.");
	}


	//Salva os dados da planilha Excel na tabela Contratados
	@PostMapping("/save-to-database")
	public ResponseEntity<String> saveToDatabase(@RequestBody List<Map<String, Object>> data) {
	    service.inserirDadosTabela(data);
	    return ResponseEntity.ok("Dados salvos no banco de dados.");
	}


	@GetMapping("/export-json")
	public ResponseEntity<String> exportToJson() {
		String json = service.exportarParaJson();
		return ResponseEntity.ok(json);
	}
	
}
