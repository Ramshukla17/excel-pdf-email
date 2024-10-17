package com.ashok.file.generator.binding;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
@Data
public class SearchCriteria {

	private String planNames;
	private String planStatus;
	private String gender;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate planStartDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate planEndDate;
	
}
