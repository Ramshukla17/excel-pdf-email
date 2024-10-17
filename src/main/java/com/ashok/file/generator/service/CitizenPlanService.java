package com.ashok.file.generator.service;

import java.io.IOException;
import java.util.List;

import com.ashok.file.generator.binding.SearchCriteria;
import com.ashok.file.generator.entity.CitizenPlan;

import jakarta.servlet.http.HttpServletResponse;

public interface CitizenPlanService {

	List<String> getPlanNames();
	List<String> getPlanStatus();
	List<CitizenPlan> searchCitizens(SearchCriteria searchCriteria);
	void generateExcellFile(HttpServletResponse response) throws IOException ;
	void generatePdfFile(HttpServletResponse response) throws IOException;
}
