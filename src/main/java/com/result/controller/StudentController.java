package com.result.controller;


import com.result.model.*;
import com.result.repository.*;
import com.result.service.ResultCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ResultCalculationService calculationService;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            return "redirect:/student/login";
        }
        
        // Get all available semesters with results
        List<String> availableSemesters = calculationService.getAvailableSemesters(student);
        Map<String, List<SemesterResult>> allResults = calculationService.getAllSemesterResults(student);
        
        // Calculate SGPA for each semester
        Map<String, Double> semesterSGPA = new LinkedHashMap<>();
        for (String semester : availableSemesters) {
            List<SemesterResult> results = allResults.get(semester);
            if (results != null && !results.isEmpty()) {
                double sgpa = calculationService.calculateSGPA(results);
                semesterSGPA.put(semester, sgpa);
            } else {
                semesterSGPA.put(semester, 0.0);
            }
        }
        
        // FIXED: Calculate CGPA properly
        double cgpa = calculationService.calculateCGPA(student);
        
        // Debug output
        System.out.println("=== STUDENT DASHBOARD DEBUG ===");
        System.out.println("Student: " + student.getName());
        System.out.println("Available Semesters: " + availableSemesters);
        System.out.println("Calculated CGPA: " + cgpa);
        
        model.addAttribute("student", student);
        model.addAttribute("availableSemesters", availableSemesters);
        model.addAttribute("semesterSGPA", semesterSGPA);
        model.addAttribute("cgpa", cgpa);
        model.addAttribute("allResults", allResults);
        
        return "student/dashboard";
    }
    
    @GetMapping("/semester/{semester}")
    public String viewSemesterResult(@PathVariable String semester, 
                                     HttpSession session, 
                                     Model model) {
        Student student = (Student) session.getAttribute("student");
        if (student == null) {
            return "redirect:/student/login";
        }
        
        System.out.println("=== VIEWING SEMESTER RESULT ===");
        System.out.println("Student ID: " + student.getId());
        System.out.println("Semester parameter: '" + semester + "'");  // Debug line
        
        // URL decode if needed
        if (semester != null) {
            semester = semester.trim();
        }
        
        Map<String, Object> semesterSummary = calculationService.getSemesterSummary(student, semester);
        List<SemesterResult> results = (List<SemesterResult>) semesterSummary.get("results");
        
        System.out.println("Results found: " + (results != null ? results.size() : 0));
        
        // Rest of the code...
    
        if (results != null) {
            for (SemesterResult r : results) {
                System.out.println("  - " + r.getSubject().getSubjectName() + ": " + r.getMarksObtained());
            }
        }
        
        // Calculate total marks
        int totalObtained = 0;
        int totalMax = 0;
        if (results != null && !results.isEmpty()) {
            totalObtained = results.stream().mapToInt(SemesterResult::getMarksObtained).sum();
            totalMax = results.stream().mapToInt(SemesterResult::getTotalMarks).sum();
        }
        double percentage = totalMax > 0 ? (totalObtained * 100.0) / totalMax : 0;
        
        double cgpa = calculationService.calculateCGPA(student);
        double sgpa = (double) semesterSummary.get("sgpa");
        
        // Remark based on SGPA
        String remark;
        if (sgpa >= 9.0) remark = "🏆 Outstanding! Excellent Performance!";
        else if (sgpa >= 8.0) remark = "🥇 Very Good! Keep it up!";
        else if (sgpa >= 7.0) remark = "🥈 Good Performance! Can do better!";
        else if (sgpa >= 6.0) remark = "🥉 Satisfactory! Need improvement!";
        else if (sgpa >= 5.0) remark = "⚠️ Average! Work harder!";
        else if (sgpa >= 4.0) remark = "⚠️ Pass! Need significant improvement!";
        else remark = "❌ Fail! Please work hard for next time!";
        
        model.addAttribute("student", student);
        model.addAttribute("semester", semester);
        model.addAttribute("results", results != null ? results : new ArrayList<>());
        model.addAttribute("sgpa", sgpa);
        model.addAttribute("cgpa", cgpa);
        model.addAttribute("totalObtained", totalObtained);
        model.addAttribute("totalMax", totalMax);
        model.addAttribute("percentage", Math.round(percentage * 100.0) / 100.0);
        model.addAttribute("remark", remark);
        model.addAttribute("earnedCredits", semesterSummary.get("earnedCredits"));
        model.addAttribute("totalCredits", semesterSummary.get("totalCredits"));
        
        return "student/semester-result";
    }
}