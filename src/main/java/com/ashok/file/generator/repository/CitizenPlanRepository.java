package com.ashok.file.generator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ashok.file.generator.entity.CitizenPlan;

public interface CitizenPlanRepository extends JpaRepository<CitizenPlan, Integer> {

	
	@Query("select distinct (planNames) from CitizenPlan")
	List<String> getPlanNames();
	
	@Query("select distinct (planStatus) from CitizenPlan")
	List<String> getPlanStatus();
}
