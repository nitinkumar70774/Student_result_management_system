package com.result.controller;

import com.result.model.*;
import com.result.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    private final FacultyRepository facultyRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SemesterResultRepository resultRepository;

    AdminController(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("studentCount", studentRepository.count());
        model.addAttribute("facultyCount", facultyRepository.count());
        model.addAttribute("subjectCount", subjectRepository.count());
        model.addAttribute("resultCount", resultRepository.countDistinctStudentSemester());
        
        return "admin/dashboard";
    }
    
    // ==================== Student Management ====================
    
    @GetMapping("/students")
    public String listStudents(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("students", studentRepository.findAll());
        return "admin/students";
    }
    
    // Search students with filters
    @PostMapping("/students/search")
    public String searchStudents(@RequestParam(required = false) String searchKeyword,
                                 @RequestParam(required = false) String course,
                                 @RequestParam(required = false) String branch,
                                 @RequestParam(required = false) String year,
                                 @RequestParam(required = false) String semester,
                                 HttpSession session,
                                 Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        List<Student> allStudents = studentRepository.findAll();
        
        // Apply filters
        List<Student> filteredStudents = allStudents.stream()
                .filter(s -> {
                    // Filter by search keyword (name or roll number)
                    if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                        String keyword = searchKeyword.toLowerCase().trim();
                        boolean nameMatch = s.getName() != null && s.getName().toLowerCase().contains(keyword);
                        boolean rollMatch = s.getRollNumber() != null && s.getRollNumber().toLowerCase().contains(keyword);
                        return nameMatch || rollMatch;
                    }
                    return true;
                })
                .filter(s -> {
                    // Filter by course
                    if (course != null && !course.isEmpty()) {
                        return course.equals(s.getCourse());
                    }
                    return true;
                })
                .filter(s -> {
                    // Filter by branch
                    if (branch != null && !branch.isEmpty()) {
                        return branch.equals(s.getBranch());
                    }
                    return true;
                })
                .filter(s -> {
                    // Filter by year
                    if (year != null && !year.isEmpty()) {
                        return year.equals(s.getYear());
                    }
                    return true;
                })
                .filter(s -> {
                    // Filter by semester
                    if (semester != null && !semester.isEmpty()) {
                        return semester.equals(s.getSemester());
                    }
                    return true;
                })
                .collect(Collectors.toList());
        
        model.addAttribute("students", filteredStudents);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("course", course);
        model.addAttribute("branch", branch);
        model.addAttribute("year", year);
        model.addAttribute("semester", semester);
        
        return "admin/students";
    }
    
    @GetMapping("/students/add")
    public String showAddStudentForm(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("student", new Student());
        model.addAttribute("courses", List.of("B.Tech", "M.Tech", "BCA", "MCA", "B.Sc", "BBA", "MBA"));
        model.addAttribute("branches", List.of("Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Electrical"));
        model.addAttribute("years", List.of("1st Year", "2nd Year", "3rd Year", "4th Year"));
        model.addAttribute("semesters", List.of("Semester 1", "Semester 2", "Semester 3", "Semester 4", 
                                                "Semester 5", "Semester 6", "Semester 7", "Semester 8"));
        return "admin/add-student";
    }
    
    @PostMapping("/students/add")
    public String addStudent(@ModelAttribute Student student, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        student.setPassword("student123");
        studentRepository.save(student);
        return "redirect:/admin/students?success=added";
    }
    
    @GetMapping("/students/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return "redirect:/admin/students?error=notfound";
        }
        
        model.addAttribute("student", student);
        model.addAttribute("courses", List.of("B.Tech", "M.Tech", "BCA", "MCA", "B.Sc", "BBA", "MBA"));
        model.addAttribute("branches", List.of("Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Electrical"));
        model.addAttribute("years", List.of("1st Year", "2nd Year", "3rd Year", "4th Year"));
        model.addAttribute("semesters", List.of("Semester 1", "Semester 2", "Semester 3", "Semester 4", 
                                                "Semester 5", "Semester 6", "Semester 7", "Semester 8"));
        return "admin/edit-student";
    }
    
    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute Student student, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        Student existingStudent = studentRepository.findById(student.getId()).orElse(null);
        if (existingStudent != null) {
            student.setPassword(existingStudent.getPassword());
        }
        studentRepository.save(student);
        return "redirect:/admin/students?success=updated";
    }
    
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        studentRepository.deleteById(id);
        return "redirect:/admin/students?success=deleted";
    }
    
    // ==================== Faculty Management ====================
    
    @GetMapping("/faculty")
    public String listFaculty(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("faculty", facultyRepository.findAll());
        return "admin/faculty";
    }
    
    @GetMapping("/faculty/add")
    public String showAddFacultyForm(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("faculty", new Faculty());
        model.addAttribute("departments", List.of("Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Electrical"));
        return "admin/add-faculty";
    }
    
    @PostMapping("/faculty/add")
    public String addFaculty(@ModelAttribute Faculty faculty, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        faculty.setPassword("faculty123");
        facultyRepository.save(faculty);
        return "redirect:/admin/faculty?success=added";
    }
    
    @GetMapping("/faculty/delete/{id}")
    public String deleteFaculty(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        facultyRepository.deleteById(id);
        return "redirect:/admin/faculty?success=deleted";
    }
    
    // ==================== Subject Management ====================
    
    @GetMapping("/subjects")
    public String listSubjects(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("subjects", subjectRepository.findAll());
        return "admin/subjects";
    }
    
    @GetMapping("/subjects/add")
    public String showAddSubjectForm(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("subject", new Subject());
        model.addAttribute("courses", List.of("B.Tech", "M.Tech", "BCA", "MCA", "B.Sc"));
        model.addAttribute("branches", List.of("Computer Science", "Information Technology", "Electronics"));
        model.addAttribute("semesters", List.of("Semester 1", "Semester 2", "Semester 3", "Semester 4", 
                                                "Semester 5", "Semester 6", "Semester 7", "Semester 8"));
        return "admin/add-subject";
    }
    
    @PostMapping("/subjects/add")
    public String addSubject(@ModelAttribute Subject subject, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        subjectRepository.save(subject);
        return "redirect:/admin/subjects?success=added";
    }
    
    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }
        
        try {
            List<SemesterResult> results = resultRepository.findBySubjectId(id);
            if (!results.isEmpty()) {
                return "redirect:/admin/subjects?error=hasResults";
            }
            subjectRepository.deleteById(id);
            return "redirect:/admin/subjects?success=deleted";
        } catch (Exception e) {
            return "redirect:/admin/subjects?error=cannotDelete";
        }
    }
}