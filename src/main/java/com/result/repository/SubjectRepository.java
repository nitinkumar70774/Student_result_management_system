package com.result.repository;

import com.result.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findBySemester(String semester);
    List<Subject> findByCourseAndBranchAndSemester(String course, String branch, String semester);
}