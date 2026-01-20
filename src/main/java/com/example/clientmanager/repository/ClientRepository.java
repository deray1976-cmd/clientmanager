package com.example.clientmanager.repository;

//import java.util.ArrayList;
//import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

//import com.example.clientmanager.dto.*;
import com.example.clientmanager.model.*;
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    
    /*private final List<ClientDto> clientsDto = new ArrayList<>();

    public ClientRepository() {
        clientsDto.add(new ClientDto(new Client(1L, "Anna", "anna@email.com")));
        clientsDto.add(new ClientDto(new Client(2L, "Marc", "marc@email.com")));
    }

    public List<ClientDto> findAll() {
        return clientsDto;
    }

    public void save(ClientDto clientDto) {
        clientsDto.add(clientDto);
    }*/
}