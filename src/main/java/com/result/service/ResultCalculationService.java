package com.result.service;

import com.result.model.SemesterResult;
import com.result.model.Student;
import com.result.repository.SemesterResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ResultCalculationService {
    
    @Autowired
    private SemesterResultRepository resultRepository;
    
    public List<String> getAvailableSemesters(Student student) {
        List<SemesterResult> allResults = resultRepository.findByStudentOrderBySemesterAsc(student);
        Set<String> semesters = new LinkedHashSet<>();
        
        System.out.println("=== getAvailableSemesters DEBUG ===");
        System.out.println("Student ID: " + student.getId());
        System.out.println("Total results found: " + allResults.size());
        
        for (SemesterResult result : allResults) {
            System.out.println("Result - Semester: " + result.getSemester() + 
                               ", Subject: " + (result.getSubject() != null ? result.getSubject().getSubjectName() : "NULL") +
                               ", Marks: " + result.getMarksObtained());
            if (result.getSemester() != null) {
                semesters.add(result.getSemester());
            }
        }
        
        System.out.println("Unique semesters: " + semesters);
        return new ArrayList<>(semesters);
    }
    
    public List<SemesterResult> getResultsBySemester(Student student, String semester) {
        List<SemesterResult> results = resultRepository.findByStudentAndSemester(student, semester);
        
        System.out.println("=== getResultsBySemester DEBUG ===");
        System.out.println("Student: " + student.getName());
        System.out.println("Semester: " + semester);
        System.out.println("Results found: " + results.size());
        
        for (SemesterResult result : results) {
            System.out.println("  - Subject: " + (result.getSubject() != null ? result.getSubject().getSubjectName() : "NULL") +
                               ", Marks: " + result.getMarksObtained() + "/" + result.getTotalMarks() +
                               ", Grade: " + result.getGrade());
        }
        
        return results;
    }
    
    public Map<String, List<SemesterResult>> getAllSemesterResults(Student student) {
        List<SemesterResult> allResults = resultRepository.findByStudentOrderBySemesterAsc(student);
        Map<String, List<SemesterResult>> semesterMap = new LinkedHashMap<>();
        
        System.out.println("=== getAllSemesterResults DEBUG ===");
        System.out.println("Total results: " + allResults.size());
        
        for (SemesterResult result : allResults) {
            if (result.getSemester() != null && result.getSubject() != null) {
                semesterMap.computeIfAbsent(result.getSemester(), k -> new ArrayList<>()).add(result);
                System.out.println("Added: " + result.getSemester() + " - " + result.getSubject().getSubjectName());
            } else {
                System.out.println("WARNING: Result with null semester or subject! ID: " + result.getId());
            }
        }
        
        return semesterMap;
    }
    
    public double calculateSGPA(List<SemesterResult> results) {
        int totalCredits = 0;
        double totalGradePoints = 0;
        
        System.out.println("=== calculateSGPA DEBUG ===");
        
        for (SemesterResult result : results) {
            if (result.getSubject() == null) {
                System.out.println("WARNING: Subject is null for result ID: " + result.getId());
                continue;
            }
            int credits = result.getSubject().getCredits();
            double gradePoint = Double.parseDouble(result.getGradePoint());
            
            totalCredits += credits;
            totalGradePoints += gradePoint * credits;
            
            System.out.println("Subject: " + result.getSubject().getSubjectName() + 
                               ", Credits: " + credits + 
                               ", Grade Point: " + gradePoint);
        }
        
        double sgpa = totalCredits > 0 ? (totalGradePoints / totalCredits) : 0;
        sgpa = Math.round(sgpa * 100.0) / 100.0;
        
        System.out.println("Total Credits: " + totalCredits);
        System.out.println("Total Grade Points: " + totalGradePoints);
        System.out.println("SGPA: " + sgpa);
        
        return sgpa;
    }
    
    public double calculateCGPA(Student student) {
        List<SemesterResult> allResults = resultRepository.findByStudent(student);
        
        if (allResults.isEmpty()) {
            return 0.0;
        }
        
        int totalCredits = 0;
        double totalGradePoints = 0;
        
        for (SemesterResult result : allResults) {
            if (result.getSubject() == null) continue;
            int credits = result.getSubject().getCredits();
            double gradePoint = Double.parseDouble(result.getGradePoint());
            
            totalCredits += credits;
            totalGradePoints += gradePoint * credits;
        }
        
        double cgpa = totalCredits > 0 ? (totalGradePoints / totalCredits) : 0;
        return Math.round(cgpa * 100.0) / 100.0;
    }
    
    public Map<String, Object> getSemesterSummary(Student student, String semester) {
        Map<String, Object> summary = new HashMap<>();
        List<SemesterResult> results = getResultsBySemester(student, semester);
        
        int totalCredits = 0;
        int earnedCredits = 0;
        
        for (SemesterResult result : results) {
            if (result.getSubject() != null) {
                totalCredits += result.getSubject().getCredits();
                if ("PASS".equals(result.getResultStatus())) {
                    earnedCredits += result.getSubject().getCredits();
                }
            }
        }
        
        double sgpa = calculateSGPA(results);
        
        summary.put("semester", semester);
        summary.put("totalCredits", totalCredits);
        summary.put("earnedCredits", earnedCredits);
        summary.put("sgpa", sgpa);
        summary.put("results", results);
        
        return summary;
    }
}