package com.univus.project.repository;

import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByEmailAndPwd(String email, String pwd);

    // 활성 상태의 유저만 조회
    Optional<User> findByEmailAndActiveTrue(String email);

}
