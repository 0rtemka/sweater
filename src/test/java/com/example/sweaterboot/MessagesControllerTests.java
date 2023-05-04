package com.example.sweaterboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.FileInputStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("admin")
@TestPropertySource("/application-test.properties")
@Sql(value = {"/user-list-before.sql", "/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/messages-list-after.sql", "/user-list-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MessagesControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Test
    void mainPage() throws Exception {
        this.mockMvc.perform(get("/auth/hello"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/header/nav/div/div/div").string("admin"));
    }

    @Test
    void messagesCount() throws Exception {
        this.mockMvc.perform(get("/messages"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='messages-list']/div").nodeCount(3));
    }

    @Test
    void filterMessages() throws Exception {
        this.mockMvc.perform(post("/messages/filter").param("tag", "1").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='messages-list']/div").nodeCount(2))
                .andExpect(xpath("//div[@id='messages-list']/div[@data-id='1']").exists())
                .andExpect(xpath("//div[@id='messages-list']/div[@data-id='2']").exists());
    }

    @Test
    void addMessage() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/messages")
                .file("file", "123".getBytes())
                .param("text", "fileMessage")
                .param("tag", "#file")
                .with(csrf());

        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/messages"));

        this.mockMvc.perform(get("/messages"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='messages-list']/div").nodeCount(4))
                .andExpect(xpath("//div[@id='messages-list']/div[@data-id='10']").exists());
    }
}