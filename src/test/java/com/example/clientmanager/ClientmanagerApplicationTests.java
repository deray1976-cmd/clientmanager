package com.example.clientmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.dto.ClientMapper;
import com.example.clientmanager.model.Client;
import com.example.clientmanager.repository.ClientRepository;
import com.example.clientmanager.service.ClientService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClientmanagerApplicationTests {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper; // <-- afegit

    @InjectMocks
    private ClientService clientService;

    @Test
    void testFindAll() {
        Client c1 = new Client("Joan", "Pérez", 30, "12345678A", "joan@example.com");
        Client c2 = new Client("Anna", "Garcia", 28, "87654321B", "anna@example.com");

        // Mock del repository
        Mockito.when(clientRepository.findAll()).thenReturn(List.of(c1, c2));

        // Mock del mapper
        Mockito.when(clientMapper.toDto(c1)).thenReturn(
            new ClientDto(c1.getId(), c1.getName(), c1.getSurname(), c1.getEdat(), c1.getDni(), c1.getEmail(), List.of())
        );
        Mockito.when(clientMapper.toDto(c2)).thenReturn(
            new ClientDto(c2.getId(), c2.getName(), c2.getSurname(), c2.getEdat(), c2.getDni(), c2.getEmail(), List.of())
        );

        List<ClientDto> clients = clientService.getAllClients();

        assertEquals(2, clients.size());
        assertEquals("Joan", clients.get(0).name());
        assertEquals("Anna", clients.get(1).name());
        assertEquals("Pérez", clients.get(0).surname());
        assertEquals("Garcia", clients.get(1).surname());
    }

    @Test
    void testFindByIdExists() {
        Client c = new Client("Joan", "Pérez", 30, "12345678A", "joan@example.com");
        Mockito.when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(c));
        Mockito.when(clientMapper.toDto(c)).thenReturn(
            new ClientDto(c.getId(), c.getName(), c.getSurname(), c.getEdat(), c.getDni(), c.getEmail(), List.of())
        );

        ClientDto result = clientService.findById(1L);

        assertEquals("Joan", result.name());
        assertEquals("Pérez", result.surname());
        assertEquals("joan@example.com", result.email());
        assertEquals(30, result.edat());
        assertEquals("12345678A", result.dni());
    }

    @Test
    void testFindByIdNotExists() {
        Mockito.when(clientRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.findById(99L));
    }

@Test
void testCreateClient() {
    // -------------------------------
    // 1️⃣ Client que simula estar guardat a la base de dades
    // -------------------------------
    Client saved = new Client("Joan", "Pérez", 30, "12345678A", "joan@example.com");
    saved.setId(1L);

    // -------------------------------
    // 2️⃣ Mock del repository per retornar el client guardat
    // -------------------------------
    Mockito.when(clientRepository.save(Mockito.any(Client.class))).thenReturn(saved);

    // -------------------------------
    // 3️⃣ Mock del mapper: ClientDto -> Client (toEntity)
    // -------------------------------
    Mockito.when(clientMapper.toEntity(Mockito.any(ClientDto.class))).thenAnswer(invocation -> {
        ClientDto dto = invocation.getArgument(0);
        Client client = new Client(dto.name(), dto.surname(), dto.edat(), dto.dni(), dto.email());
        client.setId(dto.id());
        return client; // mai null!
    });

    // -------------------------------
    // 4️⃣ Mock del mapper: Client -> ClientDto (toDto)
    // -------------------------------
    Mockito.when(clientMapper.toDto(Mockito.any(Client.class))).thenAnswer(invocation -> {
        Client c = invocation.getArgument(0);
        return new ClientDto(
            c.getId(),
            c.getName(),
            c.getSurname(),
            c.getEdat(),
            c.getDni(),
            c.getEmail(),
            // Llista d'adreces dummy
            List.of(new AddressDto(null, "Carrer Fictici", "Barcelona"))
        );
    });

    // -------------------------------
    // 5️⃣ ClientDto d'entrada amb adreça no nul·la
    // -------------------------------
    ClientDto dto = new ClientDto(
        null,
        "Joan",
        "Pérez",
        30,
        "12345678A",
        "joan@example.com",
        List.of(new AddressDto(null, "Carrer Fictici", "Barcelona"))
    );

    // -------------------------------
    // 6️⃣ Crida al servei
    // -------------------------------
    ClientDto result = clientService.createClient(dto);

    // -------------------------------
    // 7️⃣ Comprovacions
    // -------------------------------
    assertEquals(1L, result.id());
    assertEquals("Joan", result.name());
    assertEquals("Pérez", result.surname());
    assertEquals(30, result.edat());
    assertEquals("12345678A", result.dni());
    assertEquals("joan@example.com", result.email());
    assertEquals(1, result.addresses().size());
    assertEquals("Carrer Fictici", result.addresses().get(0).street());
    assertEquals("Barcelona", result.addresses().get(0).city());
}


    @Test
    void testDeleteClientExists() {
        Client c = new Client("Joan", "Pérez", 30, "12345678A", "joan@example.com");
        c.setId(1L);
        Mockito.when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(c));
        clientService.delete(1L);
        Mockito.verify(clientRepository).delete(c);
    }

    @Test
    void testDeleteClientNotExists() {
        Mockito.when(clientRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.delete(99L));
    }

}
