package com.ashok.file.generator.runner;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.ashok.file.generator.entity.CitizenPlan;
import com.ashok.file.generator.repository.CitizenPlanRepository;

@Component
public class DataLoader implements ApplicationRunner {

	@Autowired
	private CitizenPlanRepository planRepository;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		planRepository.deleteAll();
		
		CitizenPlan plan = new CitizenPlan("Ashok", "qkH9h@example.com", 123456789L, "Male", "Cash", 
				"Active", LocalDate.now(), LocalDate.now().plusMonths(6));
		
		CitizenPlan plan1 = new CitizenPlan("Sathish", "qkH9h@example.com", 1234567809L, "Male", "Food", 
				"Denied", LocalDate.now(), LocalDate.now().plusMonths(6));
		
		Arrays.asList(plan, plan1).forEach(planRepository::save);
	}

}
