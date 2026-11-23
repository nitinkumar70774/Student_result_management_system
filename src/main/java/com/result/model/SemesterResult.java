package com.result.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "semester_results")
public class SemesterResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
   @ManyToOne
   @JoinColumn(name = "faculty_id", referencedColumnName = "id")
   private Faculty faculty;
    
    private String semester;
    private Integer marksObtained;
    private Integer totalMarks;
    private String grade;
    private String gradePoint;
    private String resultStatus;
    private LocalDateTime uploadedAt;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public Integer getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Integer marksObtained) { this.marksObtained = marksObtained; }
    
    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }
    
    public String getGrade() {
        if (marksObtained != null && totalMarks != null) {
            double percentage = (marksObtained * 100.0) / totalMarks;
            if (percentage >= 90) return "O";
            if (percentage >= 80) return "A+";
            if (percentage >= 70) return "A";
            if (percentage >= 60) return "B+";
            if (percentage >= 50) return "B";
            if (percentage >= 40) return "C";
            return "F";
        }
        return grade;
    }
    public void setGrade(String grade) { this.grade = grade; }
    
    public String getGradePoint() {
        if (marksObtained != null && totalMarks != null) {
            double percentage = (marksObtained * 100.0) / totalMarks;
            if (percentage >= 90) return "10";
            if (percentage >= 80) return "9";
            if (percentage >= 70) return "8";
            if (percentage >= 60) return "7";
            if (percentage >= 50) return "6";
            if (percentage >= 40) return "5";
            return "0";
        }
        return gradePoint;
    }
    public void setGradePoint(String gradePoint) { this.gradePoint = gradePoint; }
    
    public String getResultStatus() {
        if (marksObtained != null && totalMarks != null) {
            double percentage = (marksObtained * 100.0) / totalMarks;
            return percentage >= 40 ? "PASS" : "FAIL";
        }
        return resultStatus;
    }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public double getPercentage() {
        if (marksObtained != null && totalMarks != null) {
            return (marksObtained * 100.0) / totalMarks;
        }
        return 0;
    }
}