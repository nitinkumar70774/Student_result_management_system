package com.result.repository;


import com.result.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumberAndPassword(String rollNumber, String password);
    Optional<Student> findByRollNumber(String rollNumber);
    
    // This method requires 'semester' field in Student entity
    List<Student> findByCourseAndBranchAndYearAndSemester(String course, String branch, String year, String semester);
}