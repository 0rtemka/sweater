package com.example.sweaterboot.repos;

import com.example.sweaterboot.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {
    List<Message> findAllByTagStartingWith(String tag);
}
