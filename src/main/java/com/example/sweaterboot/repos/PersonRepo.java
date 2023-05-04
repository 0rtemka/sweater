package com.example.sweaterboot.repos;

import com.example.sweaterboot.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepo extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);

    Optional<Person> findByEmail(String email);

    Optional<Person> findByActivationCode(String code);
}
