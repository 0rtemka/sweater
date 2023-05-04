package com.example.sweaterboot.controllers;

import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.models.Role;
import com.example.sweaterboot.security.PersonDetails;
import com.example.sweaterboot.services.MailSender;
import com.example.sweaterboot.services.PersonService;
import com.example.sweaterboot.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {
    private final PersonService personService;
    private final PersonValidator personValidator;
    private final MailSender mailSender;

    @Autowired
    public UserController(PersonService personService, PersonValidator personValidator, MailSender mailSender) {
        this.personService = personService;
        this.personValidator = personValidator;
        this.mailSender = mailSender;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getUsersListPage(Model model) {
        model.addAttribute("users", personService.findAll());
        return "users/userList";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editUserPage(@PathVariable("id") int id,
                               Model model) {

        Optional<Person> person = personService.findById(id);

        if (person.isPresent()) {
            model.addAttribute("user", person.get());
            model.addAttribute("roles", Role.values());
        }

        return "users/editUser";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editUser(@ModelAttribute("user") @Valid Person person,
                           @RequestParam Map<String, String> form,
                           BindingResult bindingResult) {

        personValidator.validate(person, bindingResult);

        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        person.getRoles().clear();

        for (String k : form.keySet()) {
            if (roles.contains(k)) {
                person.getRoles().add(Role.valueOf(k));
            }
        }

        if (bindingResult.hasErrors()) {
            return "users/editUser";
        }

        personService.update(person);
        return "redirect:/users";
    }

    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal PersonDetails personDetails, Model model) {
        Person person = personService.findByUsername(personDetails.getUsername()).get();
        model.addAttribute("person", person);
        return "users/profile";
    }

    @PostMapping("/profile")
    public String updateUser(@ModelAttribute("person") @Valid Person person,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "users/profile";
        }

        personService.updateFromProfile(person);

        return "redirect:/auth/hello";
    }
}
