package com.example.sweaterboot.controllers;

import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.security.PersonDetails;
import com.example.sweaterboot.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SubscriptionsController {
    private final PersonService personService;
    @Autowired
    public SubscriptionsController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/{type}/{person}/list")
    public String subscribersPage(@PathVariable String type, @PathVariable Person person, Model model) {
        if ("subscribers".equals(type))
            model.addAttribute("subscriptions", person.getSubscribers());
        else
            model.addAttribute("subscriptions", person.getSubscriptions());

        model.addAttribute("type", type);
        model.addAttribute("person", person);
        return "subscriptions/subscriptionsPage";
    }

    @GetMapping("/subscriptions/subscribe/{channel}")
    public String subscribeChannel(@AuthenticationPrincipal PersonDetails currentUser,
                                   @PathVariable Person channel)
    {
        personService.subscribe(channel, currentUser.person());
        return "redirect:/messages/" + channel.getId();
    }
}