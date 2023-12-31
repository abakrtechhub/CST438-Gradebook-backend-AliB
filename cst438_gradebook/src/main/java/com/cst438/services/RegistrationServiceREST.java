 package com.cst438.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.CourseDTOG;

public class RegistrationServiceREST extends RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
	public void sendFinalGrades(int course_id , CourseDTOG courseDTO) { 
		
		CourseDTOG course = courseDTO;
		course.course_id = course_id;
		restTemplate.put(registration_url+"/course/"+course_id, courseDTO);
		System.out.println(registration_url+"/course/"+course_id);
		System.out.println("Course list: " + courseDTO);
	}
}
