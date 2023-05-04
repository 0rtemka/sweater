package com.example.sweaterboot.services;

import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.models.Role;
import com.example.sweaterboot.repos.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class PersonService {
    private final PersonRepo personRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;

    @Autowired
    public PersonService(PersonRepo personRepo, PasswordEncoder passwordEncoder, MailSender mailSender) {
        this.personRepo = personRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public List<Person> findAll() {
        return personRepo.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepo.findById(id);
    }

    public Optional<Person> findByUsername(String username) {
        return personRepo.findByUsername(username);
    }

    @Transactional
    public void save(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRoles(Set.of(Role.USER));
        person.setActivationCode(UUID.randomUUID().toString());
        personRepo.save(person);
    }

    @Transactional
    public void update(Person person) {
        personRepo.save(person);
    }

    @Transactional
    public void updateFromProfile(Person person) {
        if (!person.getPassword().isEmpty())
            person.setPassword(passwordEncoder.encode(person.getPassword()));
        else {
            person.setPassword(findByUsername(person.getUsername()).get().getPassword());
        }

        Person personFromDb = findByUsername(person.getUsername()).get();

        if (!person.getEmail().equals(personFromDb.getEmail())) {
            person.setActivationCode(UUID.randomUUID().toString());
            sendActivationCode(person);
        }

        personRepo.save(person);
    }

    public boolean activatePerson(String code) {
        Optional<Person> optPerson = personRepo.findByActivationCode(code);

        if (optPerson.isEmpty()) {
            return false;
        }

        Person person = optPerson.get();

        person.setActivationCode(null);
        update(person);

        return true;
    }

    public void sendActivationCode(Person person) {
        if (!StringUtils.isEmpty(person.getEmail())) {
            String message = String.format(
                    "Hello %s! Welcome to Sweater. Visit next link to activate your account http://localhost:8080/auth/activate/%s",
                    person.getUsername(),
                    person.getActivationCode()
            );

            mailSender.send(person.getEmail(), "Activation", message);
        }
    }

    @Transactional
    public void subscribe(Person channel, Person person) {
        if (channel.getSubscribers().contains(person)) {
            channel.getSubscribers().remove(person);
        } else {
            channel.getSubscribers().add(person);
        }
        personRepo.save(channel);
    }
}
