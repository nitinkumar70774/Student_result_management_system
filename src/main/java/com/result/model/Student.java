package com.result.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String rollNumber;
    
    private String name;
    private String fatherName;
    private String motherName;
    private String course;
    private String branch;
    private String year;
    private String semester;
    private String currentSemester;  // ADD THIS FIELD
    private String phoneNumber;
    private String email;
    private String password;
    private String dateOfBirth;
    private String address;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<SemesterResult> semesterResults;
    
    public Student() {}
    
    // Getters
    public Long getId() { return id; }
    public String getRollNumber() { return rollNumber; }
    public String getName() { return name; }
    public String getFatherName() { return fatherName; }
    public String getMotherName() { return motherName; }
    public String getCourse() { return course; }
    public String getBranch() { return branch; }
    public String getYear() { return year; }
    public String getSemester() { return semester; }
    public String getCurrentSemester() { return currentSemester; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getAddress() { return address; }
    public List<SemesterResult> getSemesterResults() { return semesterResults; }
    
 // Add a transient field for UI
    @Transient
    private boolean hasResults;

    public boolean isHasResults() { return hasResults; }
    public void setHasResults(boolean hasResults) { this.hasResults = hasResults; }

    public void setAttribute(String key, boolean value) {
        if ("hasResults".equals(key)) {
            this.hasResults = value;
        }
    }
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public void setName(String name) { this.name = name; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }
    public void setCourse(String course) { this.course = course; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setYear(String year) { this.year = year; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setAddress(String address) { this.address = address; }
    public void setSemesterResults(List<SemesterResult> semesterResults) { this.semesterResults = semesterResults; }
}