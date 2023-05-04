package com.example.sweaterboot.services;

import com.example.sweaterboot.models.Message;
import com.example.sweaterboot.models.Person;
import com.example.sweaterboot.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageService {
    private final MessageRepo messageRepo;

    @Autowired
    public MessageService(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public List<Message> findAllMessages() {
        return messageRepo.findAll();
    }

    @Transactional
    public void saveMessage(Message message) {
        messageRepo.save(message);
    }

    public List<Message> findByTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return findAllMessages();
        }

        return messageRepo.findAllByTagStartingWith(tag);
    }

    public List<Message> findByTag(String tag, int personId) {
        if (tag == null || tag.isEmpty()) {
            return findAllMessages()
                    .stream()
                    .filter(m -> m.getAuthor().getId() == personId)
                    .toList();
        }

        return messageRepo.findAllByTagStartingWith(tag)
                .stream()
                .filter(m -> m.getAuthor().getId() == personId)
                .toList();
    }

    @Transactional
    public void updateMessage(Message message, Person person) {
        List<Message> messages = person.getMessages();

        Message msg = messages.stream().filter(m -> m.getId() == message.getId()).toList().get(0);

        msg.setText(message.getText());
        msg.setTag(message.getTag());
        if (message.getFilename() != null)
            msg.setFilename(message.getFilename());

        messageRepo.save(msg);
    }
}
