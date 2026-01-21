package com.example.clientmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.ClientDto;
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
        assertEquals("Joan", clients.get(0).getClient().getName());
    }

    @Test
    void testFindByIdExists() {
        Client c = new Client("Joan", "joan@example.com");
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(c));

        Optional<ClientDto> result = clientService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Joan", result.get().getClient().getName());
    }

    @Test
    void testFindByIdNotExists() {
        Mockito.when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ClientDto> result = clientService.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateClient() {
        Client c = new Client( "Joan", "joan@example.com");
        Client saved = new Client("Joan", "joan@example.com"); saved.setId(1L); 
        Mockito.when(clientRepository.save(Mockito.any(Client.class))).thenReturn(saved);

        ClientDto result = clientService.createClient(new ClientDto(c));

        assertEquals(1L, result.getClient().getId());
        assertEquals("Joan", result.getClient().getName());
    }

    @Test
    void testUpdateClient() {
        Client existing = new Client("Joan", "joan@example.com");
        Client updated = new Client("Joan Updated", "joan.new@example.com");
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(clientRepository.save(existing)).thenReturn(updated);

        Optional<ClientDto> result = clientService.update(1L,new ClientDto(updated));

        assertTrue(result.isPresent());
        assertEquals("Joan Updated", result.get().getClient().getName());
    }

    @Test
    void testDeleteClient() {
        Client c = new Client("Joan", "joan@example.com");
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(c));

        boolean deleted = clientService.delete(1L);

        assertTrue(deleted);
        Mockito.verify(clientRepository).delete(c);
    }

}
