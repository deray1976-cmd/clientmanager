package com.example.clientmanager;

import com.example.clientmanager.dto.ClientDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Utilitza application-test.properties
@Transactional
class ClientControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    
    @Test
    void testCreateAndGetClient() throws Exception {
        /// Prepara un ClientDto JSON (records no tenen entitats dins)
        ClientDto client = new ClientDto(null, "Joan", "joan@test.com", null);

        // POST: crea el client
        String response = mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(client))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.client.name").value("Joan"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Converteix el JSON de resposta a ClientDto
        ClientDto created = objectMapper.readValue(response, ClientDto.class);

        // GET: obté el client creat per ID
        mockMvc.perform(get("/clients/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.client.email").value("joan@test.com"));
    }


    @Test
        void testCreateClient_InvalidData_400() throws Exception {
            // ClientDto amb nom i email invàlids
            ClientDto invalid = new ClientDto(null, "", "email-no-valid", null);

            mockMvc.perform(post("/clients")
                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                   .content(Objects.requireNonNull(objectMapper.writeValueAsString(invalid))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath( "$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists());
}

    // -------------------------
    // TEST CLIENT NO TROBAT (GET)
    // -------------------------
    @Test
    void testGetClientNotFound_404() throws Exception {
        mockMvc.perform(get("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client amb id 99 no trobat"))
                .andExpect(jsonPath("$.path").value("/clients/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // -------------------------
    // TEST CLIENT NO TROBAT (DELETE)
    // -------------------------
    @Test
    void testDeleteClientNotFound_404() throws Exception {
        mockMvc.perform(delete("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client amb id 99 no trobat"))
                .andExpect(jsonPath("$.path").value("/clients/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}