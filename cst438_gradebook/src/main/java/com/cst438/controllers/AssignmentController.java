package com.cst438.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentListDTO.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.GradebookDTO;
import com.cst438.services.RegistrationService;

@RestController
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })

public class AssignmentController {

	@Autowired
	AssignmentRepository assignmentRepository;

	@Autowired
	AssignmentGradeRepository assignmentGradeRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	RegistrationService registrationService;

	@PutMapping("/course/{id}")
	@Transactional
	public AssignmentListDTO updateAssignment(@RequestBody AssignmentListDTO assignment,
			@PathVariable("id") Integer assignmentId) {

		String email = "dwisneski@csumb.edu"; // user name (should be instructor's email)
		checkAssignment(assignmentId, email); // check that user name matches instructor email of the course.
		AssignmentListDTO aldto = new AssignmentListDTO();

		List<Assignment> assignments = assignmentRepository.findNeedGradingByEmail(email);

		/*
		 * for (Assignment a : assignments) { aldto.assignments.add(new
		 * AssignmentListDTO.AssignmentDTO(a.getId(), a.getCourse().getCourse_id(),
		 * a.getName(), a.getDueDate().toString(), a.getCourse().getTitle())); }
		 */

		for (AssignmentListDTO.AssignmentDTO adto : assignment.assignments) {
			System.out.printf("%s\n", adto.toString());

			Assignment am = assignmentRepository.findById(adto.assignmentId).orElse(null);
			am.setName(adto.assignmentName);
			assignmentRepository.save(am);
		}

		return aldto;

	}

	@DeleteMapping("/course/{id}")
	public void deleteAssignment(@PathVariable("id") int id) {
		Assignment a = assignmentRepository.findById(id).orElse(null);

		if (a == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID not found. ");
		}
		System.out.print(id);
		AssignmentGrade ag = assignmentGradeRepository.findById(id).orElse(null);
		if (ag != null) {
			throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Student Has Grades. Do NOT Delete. ");
		} else {
			assignmentRepository.delete(a);

		}
		// assignmentGradeRepository.delete(ag);

	}

	@PostMapping("/course/{courseId}")
	@Transactional
	public void addAssignment(@RequestBody AssignmentListDTO.AssignmentDTO assignment,
			@PathVariable("courseId") Integer course_id) {

		// check that this request is from the course instructor
		String email = "dwisneski@csumb.edu"; // user name (should be instructor's email)

		Course c = courseRepository.findById(course_id).orElse(null);

		if (!c.getInstructor().equals(email)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not Authorized. ");
		}

		System.out.printf("%d %s\n", c.getCourse_id(), c.getTitle());

		Assignment e = new Assignment();

		System.out.printf("Course_ID: " + c.getAssignments() + "\n");

		e.setCourse(c);
		e.setNeedsGrading(1);
		e.setDueDate(assignment.dueDate);
		e.setName(assignment.assignmentName);
		System.out.printf("%s\n", e.toString());

		assignmentRepository.save(e);

		System.out.printf("%s\n", e.getName());

	}

	private Assignment checkAssignment(int assignmentId, String email) {
		// get assignment
		Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
		if (assignment == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found. " + assignmentId);
		}
		// check that user is the course instructor
		if (!assignment.getCourse().getInstructor().equals(email)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not Authorized. ");
		}

		return assignment;
	}

}
