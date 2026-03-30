package com.example.clientmanager;

import com.example.clientmanager.dto.AddressDto;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetClient() throws Exception {
        ClientDto client = new ClientDto(
                null,
                "Joan",
                "Pérez",
                35,
                "12345678A",
                "joan@test.com",
                List.of(new AddressDto(null, "Carrer Gran", "Barcelona", null))
        );

        String response = mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Joan"))
                .andExpect(jsonPath("$.surname").value("Pérez"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClientDto created = objectMapper.readValue(response, ClientDto.class);

        mockMvc.perform(get("/clients/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joan@test.com"))
                .andExpect(jsonPath("$.edat").value(35))
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.addresses[0].street").value("Carrer Gran"))
                .andExpect(jsonPath("$.addresses[0].city").value("Barcelona"));
    }

    @Test
    void testCreateClient_InvalidData_400() throws Exception {
        ClientDto invalid = new ClientDto(
                null, "", "", null, "", "email-no-valid", List.of()
        );

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.surname").exists())
                .andExpect(jsonPath("$.errors.edat").exists())
                .andExpect(jsonPath("$.errors.dni").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.addresses").exists());
    }

    @Test
    void testGetClientNotFound_404() throws Exception {
        mockMvc.perform(get("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client amb id 99 no trobat"))
                .andExpect(jsonPath("$.path").value("/clients/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testDeleteClientNotFound_404() throws Exception {
        mockMvc.perform(delete("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Client amb id 99 no trobat"))
                .andExpect(jsonPath("$.path").value("/clients/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateClientWithAddresses() throws Exception {
        ClientDto client = new ClientDto(
                null,
                "Anna",
                "Garcia",
                28,
                "87654321B",
                "anna@test.com",
                List.of(
                        new AddressDto(null, "Carrer Major", "Barcelona", null),
                        new AddressDto(null, "Carrer Petita", "Girona", null)
                )
        );

        String response = mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addresses.length()").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClientDto created = objectMapper.readValue(response, ClientDto.class);

        mockMvc.perform(get("/clients/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addresses.length()").value(2))
                .andExpect(jsonPath("$.addresses[0].street").value("Carrer Major"))
                .andExpect(jsonPath("$.addresses[0].city").value("Barcelona"));
    }
}