package com.sato.linenorderapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sato.linenorderapp.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);
}
