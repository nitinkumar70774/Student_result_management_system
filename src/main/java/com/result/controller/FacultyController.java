package com.result.controller;

import com.result.model.*;
import com.result.repository.*;
import com.result.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SemesterResultRepository resultRepository;
    
    @Autowired
    private SmsService smsService;
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        model.addAttribute("faculty", faculty);
        
        // YEH GALAT THA - Ye har subject ko ek result count karta hai
        // long facultyResultCount = resultRepository.countByFacultyId(faculty.getId());
        
        // YEH SAHI HAI - Ye ek student ke ek semester ko 1 result count karega
        long facultyResultCount = resultRepository.countDistinctStudentsByFacultyId(faculty.getId());
        model.addAttribute("resultCount", facultyResultCount);
        
        // Get total students
        long totalStudents = studentRepository.count();
        model.addAttribute("totalStudents", totalStudents);
        
        model.addAttribute("courses", List.of("B.Tech", "M.Tech", "BCA", "MCA", "B.Sc"));
        model.addAttribute("branches", List.of("Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil"));
        model.addAttribute("years", List.of("1st Year", "2nd Year", "3rd Year", "4th Year"));
        model.addAttribute("semesters", List.of("Semester 1", "Semester 2", "Semester 3", "Semester 4", 
                                                "Semester 5", "Semester 6", "Semester 7", "Semester 8"));
        
        return "faculty/dashboard";
    }
    @PostMapping("/filter-students")
    public String filterStudents(@RequestParam String course,
                                 @RequestParam String branch,
                                 @RequestParam String year,
                                 @RequestParam String semester,
                                 HttpSession session,
                                 Model model) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        List<Student> students = studentRepository.findByCourseAndBranchAndYearAndSemester(course, branch, year, semester);
        
        // For each student, check if results already uploaded for this semester
        for (Student student : students) {
            List<SemesterResult> existingResults = resultRepository.findByStudentAndSemester(student, semester);
            student.setAttribute("hasResults", !existingResults.isEmpty());
        }
        
        model.addAttribute("faculty", faculty);
        model.addAttribute("students", students);
        model.addAttribute("selectedCourse", course);
        model.addAttribute("selectedBranch", branch);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("courses", List.of("B.Tech", "M.Tech", "BCA", "MCA", "B.Sc"));
        model.addAttribute("branches", List.of("Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil"));
        model.addAttribute("years", List.of("1st Year", "2nd Year", "3rd Year", "4th Year"));
        model.addAttribute("semesters", List.of("Semester 1", "Semester 2", "Semester 3", "Semester 4", 
                                                "Semester 5", "Semester 6", "Semester 7", "Semester 8"));
        
        return "faculty/dashboard";
    }
    
    @GetMapping("/upload-marks/{studentId}")
    public String showUploadMarksForm(@PathVariable Long studentId,
                                      @RequestParam String course,
                                      @RequestParam String branch,
                                      @RequestParam String year,
                                      @RequestParam String semester,
                                      HttpSession session,
                                      Model model) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        Student student = studentRepository.findById(studentId).orElse(null);
        
        // Check if results already exist for this student and semester
        List<SemesterResult> existingResults = resultRepository.findByStudentAndSemester(student, semester);
        if (!existingResults.isEmpty()) {
            model.addAttribute("error", "Results already uploaded for this student in " + semester);
            return "redirect:/faculty/dashboard?error=alreadyUploaded";
        }
        
        List<Subject> subjects = subjectRepository.findByCourseAndBranchAndSemester(course, branch, semester);
        
        model.addAttribute("student", student);
        model.addAttribute("subjects", subjects);
        model.addAttribute("course", course);
        model.addAttribute("branch", branch);
        model.addAttribute("year", year);
        model.addAttribute("semester", semester);
        
        return "faculty/upload-marks";
    }
    
    @PostMapping("/upload-marks")
    public String uploadMarks(@RequestParam Long studentId,
                              @RequestParam String course,
                              @RequestParam String branch,
                              @RequestParam String year,
                              @RequestParam String semester,
                              @RequestParam("subjectIds") Long[] subjectIds,
                              @RequestParam("marksObtained") Integer[] marksObtained,
                              @RequestParam("totalMarks") Integer[] totalMarks,
                              HttpSession session,
                              HttpServletRequest request) {
        
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        Student student = studentRepository.findById(studentId).orElse(null);
        
        if (student != null && subjectIds != null) {
            String baseUrl = request.getScheme() + "://" + request.getServerName() + 
                            ":" + request.getServerPort();
            
            boolean smsSent = false;
            
            for (int i = 0; i < subjectIds.length; i++) {
                Subject subject = subjectRepository.findById(subjectIds[i]).orElse(null);
                
                if (subject != null) {
                    // Check if result already exists for this student, subject, and semester
                    boolean exists = resultRepository.existsByStudentAndSubjectAndSemester(studentId, subjectIds[i], semester);
                    
                    if (!exists) {
                        SemesterResult result = new SemesterResult();
                        result.setStudent(student);
                        result.setSubject(subject);
                        result.setFaculty(faculty);  // Set faculty
                        result.setSemester(semester);
                        result.setMarksObtained(marksObtained[i]);
                        result.setTotalMarks(totalMarks[i]);
                        result.setUploadedAt(LocalDateTime.now());
                        
                        resultRepository.save(result);
                        
                        // Send SMS only once per student
                        if (!smsSent) {
                            String resultLink = baseUrl + "/student/semester/" + semester + "?roll=" + student.getRollNumber();
                            smsService.sendResultNotification(
                                student.getPhoneNumber(),
                                student.getName(),
                                semester,
                                resultLink
                            );
                            smsSent = true;
                        }
                    }
                }
            }
        }
        
        return "redirect:/faculty/dashboard?success=uploaded";
    }
    
    @GetMapping("/results")
    public String viewResults(HttpSession session, Model model) {
        Faculty faculty = (Faculty) session.getAttribute("faculty");
        if (faculty == null) {
            return "redirect:/faculty/login";
        }
        
        model.addAttribute("results", resultRepository.findAll());
        return "faculty/results";
    }
}