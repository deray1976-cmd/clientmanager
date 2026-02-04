package com.example.clientmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.exception.ClientNotFoundException;
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
	
	@InjectMocks
    private ClientService clientService;

	@Test
	void testFindAll() {
        Client c1 = new Client("Joan", "joan@example.com");
        Client c2 = new Client("Anna", "anna@example.com");
        Mockito.when(clientRepository.findAll()).thenReturn(List.of(c1, c2));

        List<ClientDto> clients = clientService.getAllClients();

        assertEquals(2, clients.size());
        assertEquals("Joan", clients.get(0).name());
        assertEquals("Anna", clients.get(1).name());
    }

     @Test
    void testFindByIdExists() {
        Client c = new Client("Joan", "joan@example.com");
        Mockito.when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(c));

        ClientDto result = clientService.findById(1L);

        assertEquals("Joan", result.name());
        assertEquals("joan@example.com", result.email());
    }

     @Test
    void testFindByIdNotExists() {
        Mockito.when(clientRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.findById(99L));
    }

    @Test
    void testCreateClient() {
        Client saved = new Client("Joan", "joan@example.com"); 
        saved.setId(1L); 
        Mockito.when(clientRepository.save(Mockito.any(Client.class))).thenReturn(saved);

         // Crear DTO a partir de dades (sense entitat dins)
        ClientDto dto = new ClientDto(null, "Joan", "joan@example.com", null);
        ClientDto result = clientService.createClient(dto);

        assertEquals(1L, result.id());
        assertEquals("Joan", result.name());
    }

    @Test
    void testUpdateClientExists() {
        Client existing = new Client("Joan", "joan@example.com");
        Client updated = new Client("Joan Updated", "joan.new@example.com");
        Mockito.when(clientRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        Mockito.when(clientRepository.save(existing)).thenReturn(updated);

        ClientDto updateDto = new ClientDto(null, "Joan Updated", "joan.new@example.com", null);
        ClientDto result = clientService.update(1L, updateDto); 

        assertEquals("Joan Updated", result.name());
        assertEquals("joan.new@example.com", result.email());
    }

    @Test
    void testUpdateClientNotExists() {
        Mockito.when(clientRepository.findById(99L)).thenReturn(java.util.Optional.empty());


        ClientDto updateDto = new ClientDto(null, "Updated", "updated@test.com", null);
        assertThrows(ClientNotFoundException.class,
                () -> clientService.update(99L, updateDto));
    }

    @Test
    void testDeleteClientExists() {
        Client c = new Client("Joan", "joan@example.com");
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
