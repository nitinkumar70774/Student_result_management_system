package com.result.repository;


import com.result.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyIdAndPassword(String facultyId, String password);
    Optional<Faculty> findByFacultyId(String facultyId);
}