package com.result.controller;


import com.result.model.Faculty;
import com.result.model.Student;
import com.result.repository.FacultyRepository;
import com.result.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private FacultyRepository facultyRepository;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/student/login";
    }
    
    // ==================== Student Auth ====================
    @GetMapping("/student/login")
    public String studentLoginPage() {
        return "student-login";
    }
    
    @PostMapping("/student/login")
    public String studentLogin(@RequestParam String rollNumber, 
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Student student = studentRepository.findByRollNumberAndPassword(rollNumber, password).orElse(null);
        
        if (student != null) {
            session.setAttribute("student", student);
            return "redirect:/student/dashboard";
        }
        
        model.addAttribute("error", "Invalid Roll Number or Password");
        return "student-login";
    }
    
    // ==================== Admin Auth ====================
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin-login";
    }
    
    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String username, 
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("admin", username);
            return "redirect:/admin/dashboard";
        }
        
        model.addAttribute("error", "Invalid credentials");
        return "admin-login";
    }
    
    // ==================== Faculty Auth ====================
    @GetMapping("/faculty/login")
    public String facultyLoginPage() {
        return "faculty-login";
    }
    
    @PostMapping("/faculty/login")
    public String facultyLogin(@RequestParam String facultyId, 
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Faculty faculty = facultyRepository.findByFacultyIdAndPassword(facultyId, password).orElse(null);
        
        if (faculty != null) {
            session.setAttribute("faculty", faculty);
            return "redirect:/faculty/dashboard";
        }
        
        model.addAttribute("error", "Invalid Faculty ID or Password");
        return "faculty-login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/student/login";
    }
}