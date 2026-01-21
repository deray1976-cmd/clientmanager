package com.example.clientmanager;

import com.example.clientmanager.controller.ClientController;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.exception.GlobalExceptionHandler;
import com.example.clientmanager.model.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Utilitza application-test.properties
@Transactional
class ClientControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientController clientController;

    @Autowired
    private ObjectMapper objectMapper;

    
    @Test
    void testCreateAndGetClient() throws Exception {
        // Prepara un client JSON
        ClientDto client = new ClientDto(new Client("Joan", "joan@test.com"));

        // POST: crea el client
        String response = mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.client.name").value("Joan"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Converteix el JSON de resposta a ClientDto
        ClientDto created = objectMapper.readValue(response, ClientDto.class);

        // GET: obté el client creat per ID
        mockMvc.perform(get("/clients/" + created.getClient().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client.email").value("joan@test.com"));
    }

    @Test
    void testGetClientNotFound_404() throws Exception {
        // GET amb ID no existent ha de retornar 404
        /*mockMvc.perform(get("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Client amb id 99 no trobat"));
        */
        mockMvc.perform(get("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client amb id 99 no trobat"))
                .andExpect(jsonPath("$.path").value("/clients/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteClientNotFound_404() throws Exception {
        // DELETE amb ID no existent ha de retornar 404
        mockMvc.perform(delete("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client amb id 99 no trobat"))
                .andExpect(jsonPath("$.path").value("/clients/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}