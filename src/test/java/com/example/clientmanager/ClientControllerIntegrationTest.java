package com.example.clientmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.model.Client;
import com.example.clientmanager.repository.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; 
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClientControllerIntegrationTest {
 @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        clientRepository.deleteAll();
    }

    @Test
    void testCreateAndGetClient() throws Exception {
        String json = """
            {"name":"Joan","email":"joan@example.com"}
        """;
mockMvc.perform(post("/clients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
    .andDo(print())   // 👈 AFEGEIX AIXÒ
    .andExpect(status().isOk());

        // POST
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joan"));

        // GET
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("joan@example.com"));
    }

    @Test
void testCreateAndGetClient2() throws Exception {
    // CREATE
    ClientDto client = new ClientDto(new Client("Joan","joan@test.com"));
    
    String response = mockMvc.perform(post("/clients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(client)))
        .andDo(print())                     // MOSTRA el JSON a consola
        .andExpect(status().isCreated())
        //.andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Joan")).andReturn()
        .getResponse()
        .getContentAsString();

    ClientDto created = objectMapper.readValue(response, ClientDto.class);

    // GET BY ID
    mockMvc.perform(get("/clients/" + created.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Joan"));
}

    @Test
    void testUpdateClient() throws Exception {
        Client c = clientRepository.save(new Client("Joan", "joan@example.com"));

        String updateJson = """
            {"name":"Joan Updated","email":"joan.new@example.com"}
        """;

        mockMvc.perform(put("/clients/" + c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joan Updated"));
    }

    @Test
    void testDeleteClient() throws Exception {
        Client c = clientRepository.save(new Client("Joan", "joan@example.com"));

        mockMvc.perform(delete("/clients/" + c.getId()))
                .andExpect(status().isNoContent());
    }
}
