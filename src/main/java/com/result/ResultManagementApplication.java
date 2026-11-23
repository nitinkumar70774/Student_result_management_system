package com.result;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class ResultManagementApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(ResultManagementApplication.class, args);
//	}
//
//}

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class ResultManagementApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(ResultManagementApplication.class, args);
//        System.out.println("\n=========================================");
//        System.out.println("Student Result Management System");
//        System.out.println("Access: http://localhost:8080");
//        System.out.println("Admin Login: admin / admin123");
//        System.out.println("Students can search by Roll Number");
//        System.out.println("=========================================\n");
//    }
//}


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResultManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ResultManagementApplication.class, args);
        System.out.println("\n=========================================");
        System.out.println("SEMESTER RESULT MANAGEMENT SYSTEM");
        System.out.println("=========================================");
        System.out.println("Access: http://localhost:8081");
        System.out.println("Admin: admin / admin123");
        System.out.println("Faculty: faculty1 / faculty123");
        System.out.println("Student: 2024CS001 / student123");
        System.out.println("=========================================\n");
    }
}