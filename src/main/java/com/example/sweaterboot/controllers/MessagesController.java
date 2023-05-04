package com.example.sweaterboot.controllers;

import com.example.sweaterboot.models.Message;
import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.security.PersonDetails;
import com.example.sweaterboot.services.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/messages")
public class MessagesController {
    private final MessageService messageService;
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public MessagesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String messagesPage(@ModelAttribute("message")Message message,
                               Model model) {
        model.addAttribute("messages", messageService.findAllMessages());
        return "messages/messagesPage";
    }

    @PostMapping
    public String newMessage(@ModelAttribute("message") @Valid Message message,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal PersonDetails person,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("messages", messageService.findAllMessages());
            return "messages/messagesPage";
        }

        saveFile(message, file);
        message.setAuthor(person.person());
        messageService.saveMessage(message);
        return "redirect:/messages";
    }

    @PostMapping("/filter")
    public String filterMessages(@ModelAttribute("message")Message message,
                                 Model model) {
        model.addAttribute("messages", messageService.findByTag(message.getTag()));
        return "/messages/messagesPage";
    }

    @PostMapping("/{person}/filter")
    public String filterMessages(@PathVariable(name = "person") Person person,
                                 @ModelAttribute("message")Message message,
                                 Model model) {
        model.addAttribute("messages", messageService.findByTag(message.getTag(), person.getId()));
        model.addAttribute("person", person);
        return "/messages/messagePage";
    }

    @GetMapping("/{person}")
    public String messagePage(@PathVariable Person person,
                              @AuthenticationPrincipal PersonDetails personDetails,
                              @RequestParam(name = "message", required = false) Message message,
                              Model model)
    {
        List<Message> messages = person.getMessages();
        model.addAttribute("messages", messages);
        if (message != null) {
            model.addAttribute("message", message);
        } else {
            model.addAttribute("message", messages.stream().findFirst().orElse(null));
        }

        model.addAttribute("person", person);
        model.addAttribute("isSubscriber", person.getSubscribers().contains(personDetails.person()));

        return "/messages/messagePage";
    }

    @PostMapping("/{person}")
    public String editMessage(@PathVariable(name = "person") Person person,
                              @ModelAttribute(name = "message") @Valid Message message,
                              @RequestParam("file") MultipartFile file,
                              BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/messages/messagePage";
        }

        saveFile(message, file);

        messageService.updateMessage(message, person);
        return "redirect:/messages/{person}";
    }

    private void saveFile(Message message, MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File fileDir = new File(uploadPath);

            if (!fileDir.exists())
                fileDir.mkdir();

            String uuid = UUID.randomUUID().toString();
            String resultFileName = uuid + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            message.setFilename(resultFileName);
        }
    }

}
