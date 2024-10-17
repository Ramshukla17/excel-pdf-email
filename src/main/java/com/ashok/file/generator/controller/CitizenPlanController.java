package com.ashok.file.generator.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ashok.file.generator.binding.SearchCriteria;
import com.ashok.file.generator.entity.CitizenPlan;
import com.ashok.file.generator.service.CitizenPlanService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CitizenPlanController {

	@Autowired
	private CitizenPlanService planService;

	@GetMapping("/")
	public String indexPage(Model model) {
		formInit(model);
		model.addAttribute("searchCriteria", new SearchCriteria());
		return "index";
	}

	private void formInit(Model model) {
		model.addAttribute("planNames", planService.getPlanNames());
		model.addAttribute("planStatus", planService.getPlanStatus());
	}

	@PostMapping("/filter-data")
	public String handleSearchBtn(@ModelAttribute("searchCriteria") SearchCriteria criteria, Model model) {

		List<CitizenPlan> citizenInfo = planService.searchCitizens(criteria);
		model.addAttribute("citizenInfo", citizenInfo);

		formInit(model);
		return "index";
	}

	@GetMapping("/excel")
	public void generateExcellFile(HttpServletResponse response) throws IOException {

		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=data.xls";
		response.setHeader(headerKey, headerValue);
		planService.generateExcellFile(response);
	}

	@GetMapping("/pdf")
	public void generatePdfFile(HttpServletResponse response) throws IOException {

		response.setContentType("application/pdf");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=data.pdf";
		response.setHeader(headerKey, headerValue);
		planService.generatePdfFile(response);
	}
}
