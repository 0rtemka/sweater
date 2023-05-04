package com.example.sweaterboot.services;

import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.repos.PersonRepo;
import com.example.sweaterboot.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepo personRepo;

    @Autowired
    public PersonDetailsService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepo.findByUsername(username);

        if (person.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь с таким именем не найден");
        }

        return new PersonDetails(person.get());
    }
}
