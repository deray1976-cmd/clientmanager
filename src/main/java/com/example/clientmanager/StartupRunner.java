package com.example.clientmanager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.service.ClientService;
@Component
public class StartupRunner implements CommandLineRunner {

    private final ClientService service;

    public StartupRunner(ClientService service) {
        this.service = service;
    }
    
@Override
public void run(String... args) {
    System.out.println("Aplicació arrencada");
    // Creem un ClientDto amb nom i email (ID pot ser null)
    ClientDto clientDto = new ClientDto("Anna","anna@email.com");
 
    service.createClient(clientDto);
    System.out.println("Client guardat: " + clientDto.getClient().getName());
   }
}
