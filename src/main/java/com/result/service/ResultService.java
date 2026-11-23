package com.result.service;


import com.result.model.SemesterResult;
import com.result.model.Student;
import com.result.model.Subject;
import com.result.repository.SemesterResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ResultService {
    
    @Autowired
    private SemesterResultRepository resultRepository;
    
    @Autowired
    private SmsService smsService;
    
    public SemesterResult saveResult(SemesterResult result, Student student, Subject subject, String baseUrl) {
        result.setStudent(student);
        result.setSubject(subject);
        result.setUploadedAt(LocalDateTime.now());
        
        SemesterResult savedResult = resultRepository.save(result);
        
        // Send SMS notification
        String resultLink = baseUrl + "/student/semester/" + result.getSemester() + "?roll=" + student.getRollNumber();
        smsService.sendResultNotification(
            student.getPhoneNumber(),
            student.getName(),
            result.getSemester(),
            resultLink
        );
        
        return savedResult;
    }
    
    public Map<String, List<SemesterResult>> getSemesterWiseResults(Student student) {
        List<SemesterResult> allResults = resultRepository.findByStudentOrderBySemesterAsc(student);
        Map<String, List<SemesterResult>> semesterMap = new LinkedHashMap<>();
        
        for (SemesterResult result : allResults) {
            semesterMap.computeIfAbsent(result.getSemester(), k -> new ArrayList<>()).add(result);
        }
        
        return semesterMap;
    }
    
    public List<SemesterResult> getResultsBySemester(Student student, String semester) {
        return resultRepository.findByStudentAndSemester(student, semester);
    }
    
    public Map<String, Object> calculateSemesterSummary(List<SemesterResult> results) {
        Map<String, Object> summary = new HashMap<>();
        
        int totalCredits = 0;
        int earnedCredits = 0;
        double totalGradePoints = 0;
        
        for (SemesterResult result : results) {
            int credits = result.getSubject().getCredits();
            double gradePoint = Double.parseDouble(result.getGradePoint());
            
            totalCredits += credits;
            totalGradePoints += gradePoint * credits;
            
            if ("PASS".equals(result.getResultStatus())) {
                earnedCredits += credits;
            }
        }
        
        double sgpa = totalCredits > 0 ? totalGradePoints / totalCredits : 0;
        
        summary.put("totalCredits", totalCredits);
        summary.put("earnedCredits", earnedCredits);
        summary.put("sgpa", Math.round(sgpa * 100.0) / 100.0);
        
        return summary;
    }
    
    public double calculateCGPA(Map<String, List<SemesterResult>> semesterResults) {
        double totalGradePoints = 0;
        int totalCredits = 0;
        
        for (Map.Entry<String, List<SemesterResult>> entry : semesterResults.entrySet()) {
            Map<String, Object> summary = calculateSemesterSummary(entry.getValue());
            double sgpa = (double) summary.get("sgpa");
            int credits = (int) summary.get("totalCredits");
            
            totalGradePoints += sgpa * credits;
            totalCredits += credits;
        }
        
        return totalCredits > 0 ? Math.round((totalGradePoints / totalCredits) * 100.0) / 100.0 : 0;
    }
    
    public SemesterResult getResultById(Long id) {
        return resultRepository.findById(id).orElse(null);
    }
    
    public List<SemesterResult> getAllResults() {
        return resultRepository.findAll();
    }
}