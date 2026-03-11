package com.example.clientmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.clientmanager.entity.ClientEntity;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.entity.ClientEntityModelMapper;
import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.repository.ClientRepository;
import com.example.clientmanager.service.ClientService;

@ExtendWith(MockitoExtension.class)
class ApplicationManagerApplicationTests {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientEntityModelMapper clientEntityMapper;

    @InjectMocks
    private ClientService clientService;

    // -------------------------
    // FIND ALL
    // -------------------------
    @Test
    void testFindAll() {

        ClientEntity c1 = new ClientEntity("Joan", "Pérez", 30, "12345678A", "joan@example.com");
        ClientEntity c2 = new ClientEntity("Anna", "Garcia", 28, "87654321B", "anna@example.com");

        Mockito.when(clientRepository.findAll()).thenReturn(List.of(c1, c2));

        Mockito.when(clientEntityMapper.toModel(c1))
                .thenReturn(new ClientModel(1L, "Joan", "Pérez", 30, "12345678A", "joan@example.com", List.of()));

        Mockito.when(clientEntityMapper.toModel(c2))
                .thenReturn(new ClientModel(2L, "Anna", "Garcia", 28, "87654321B", "anna@example.com", List.of()));

        List<ClientModel> clients = clientService.getAllClients();

        assertEquals(2, clients.size());
        assertEquals("Joan", clients.get(0).getName());
        assertEquals("Anna", clients.get(1).getName());
    }

    // -------------------------
    // FIND BY ID EXISTS
    // -------------------------
    @Test
    void testFindByIdExists() {

        ClientEntity c = new ClientEntity("Joan", "Pérez", 30, "12345678A", "joan@example.com");

        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(c));

        Mockito.when(clientEntityMapper.toModel(c))
                .thenReturn(new ClientModel(1L, "Joan", "Pérez", 30, "12345678A", "joan@example.com", List.of()));

        ClientModel result = clientService.findById(1L);

        assertEquals("Joan", result.getName());
        assertEquals("Pérez", result.getSurname());
        assertEquals("joan@example.com", result.getEmail());
        assertEquals(30, result.getEdat());
        assertEquals("12345678A", result.getDni());
    }

    // -------------------------
    // FIND BY ID NOT EXISTS
    // -------------------------
    @Test
    void testFindByIdNotExists() {

        Mockito.when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class,
                () -> clientService.findById(99L));
    }

    // -------------------------
    // CREATE CLIENT
    // -------------------------
    @Test
    void testCreateClient() {

        ClientModel model = new ClientModel(
                null,
                "Joan",
                "Pérez",
                30,
                "12345678A",
                "joan@example.com",
                List.of()
        );

        ClientEntity entityToSave = new ClientEntity(
                "Joan", "Pérez", 30, "12345678A", "joan@example.com"
        );

        ClientEntity savedEntity = new ClientEntity(
                "Joan", "Pérez", 30, "12345678A", "joan@example.com"
        );
        savedEntity.setId(1L);

        Mockito.when(clientEntityMapper.toEntity(model))
                .thenReturn(entityToSave);

        Mockito.when(clientRepository.save(entityToSave))
                .thenReturn(savedEntity);

        Mockito.when(clientEntityMapper.toModel(savedEntity))
                .thenReturn(new ClientModel(
                        1L,
                        "Joan",
                        "Pérez",
                        30,
                        "12345678A",
                        "joan@example.com",
                        List.of()
                ));

        ClientModel result = clientService.createClient(model);

        assertEquals(1L, result.getId());
        assertEquals("Joan", result.getName());
    }

    // -------------------------
    // DELETE EXISTS
    // -------------------------
    @Test
    void testDeleteClientExists() {

        ClientEntity c = new ClientEntity("Joan", "Pérez", 30, "12345678A", "joan@example.com");
        c.setId(1L);

        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(c));

        clientService.delete(1L);

        Mockito.verify(clientRepository).delete(c);
    }

    // -------------------------
    // DELETE NOT EXISTS
    // -------------------------
    @Test
    void testDeleteClientNotExists() {

        Mockito.when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class,
                () -> clientService.delete(99L));
    }
}
