package com.result.repository;


import com.result.model.SemesterResult;
import com.result.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SemesterResultRepository extends JpaRepository<SemesterResult, Long> {
    
    List<SemesterResult> findByStudentOrderBySemesterAsc(Student student);
    
    List<SemesterResult> findByStudentAndSemester(Student student, String semester);
    
    List<SemesterResult> findBySubjectId(Long subjectId);
    
    // Count total results (individual subject entries)
    long count();
    
    // Count DISTINCT students who have results for a semester
    @Query("SELECT COUNT(DISTINCT s.student.id) FROM SemesterResult s")
    long findDistinctStudentCount();
    
    // Count distinct student-semester combinations (ONE result per student per semester)
    @Query("SELECT COUNT(DISTINCT CONCAT(s.student.id, '-', s.semester)) FROM SemesterResult s")
    long countDistinctStudentSemester();
    
    // Count results by faculty - DISTINCT students
    @Query("SELECT COUNT(DISTINCT s.student.id) FROM SemesterResult s WHERE s.faculty.id = ?1")
    long countDistinctStudentsByFacultyId(Long facultyId);
    
    // Check if result already exists for a student, subject, and semester
    @Query("SELECT COUNT(s) > 0 FROM SemesterResult s WHERE s.student.id = ?1 AND s.subject.id = ?2 AND s.semester = ?3")
    boolean existsByStudentAndSubjectAndSemester(Long studentId, Long subjectId, String semester);
    
    // Get all results for a student
    List<SemesterResult> findByStudent(Student student);
}