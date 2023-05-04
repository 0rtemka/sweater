package com.example.sweaterboot.util;

import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Component
public class PersonValidator implements Validator {
    private final PersonRepo personRepo;

    @Autowired
    public PersonValidator(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        Optional<Person> optionalPerson1 = personRepo.findByUsername(person.getUsername());
        Optional<Person> optionalPerson2 = personRepo.findByEmail(person.getEmail());

        if (optionalPerson1.isPresent() && optionalPerson1.get().getId() != person.getId()) {
            errors.rejectValue("username", "", "Пользователь с таким именем уже существует");
        }

        if (optionalPerson2.isPresent() && optionalPerson2.get().getEmail().equals(person.getEmail())) {
            errors.rejectValue("email", "", "Пользователь с таким email уже существует");
        }

    }
}
