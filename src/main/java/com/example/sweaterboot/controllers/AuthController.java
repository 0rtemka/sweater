package com.example.sweaterboot.controllers;

import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.security.PersonDetails;
import com.example.sweaterboot.services.MailSender;
import com.example.sweaterboot.services.PersonService;
import com.example.sweaterboot.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final PersonValidator personValidator;
    private final PersonService personService;
    private final MailSender mailSender;

    @Autowired
    public AuthController(PersonValidator personValidator, PersonService personService, MailSender mailSender) {
        this.personValidator = personValidator;
        this.personService = personService;
        this.mailSender = mailSender;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        PersonDetails details = (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("username", details.getUsername());
        return "auth/hello";
    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("person") Person person) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerPerson(@ModelAttribute("person") @Valid Person person,
                                 BindingResult bindingResult) {

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        personService.save(person);

        personService.sendActivationCode(person);

        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/auth/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable("code")String code, Model model) {
        boolean isActivated = personService.activatePerson(code);

        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "Активация прошла успешно");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Код активации недействителен");
        }

        return "auth/login";
    }
}
