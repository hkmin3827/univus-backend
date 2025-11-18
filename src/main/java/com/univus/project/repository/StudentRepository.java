package com.univus.project.repository;

import com.univus.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // SELECT * from Student WHERE email = '';
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);

    // SELECT * from Student WHERE email = '' and pwd = '';
    Optional<Student> findByEmailAndPwd(String email, String pwd);

}
