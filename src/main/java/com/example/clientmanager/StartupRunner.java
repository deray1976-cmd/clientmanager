package com.example.clientmanager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.clientmanager.service.ClientService;
@Component
public class StartupRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);
    private final ClientService service;

    public StartupRunner(ClientService service) {
        this.service = service;
    }
    
@Override
public void run(String... args) {
    System.out.println("Aplicació arrencada");
    log.info("Aplicacio arrancada !!!");

    // Creem un ClientDto amb nom i email (ID pot ser null, adreces null)
    //ClientDto clientDto = new ClientDto(null, "Anna", "anna@email.com", null);

 
    // Guardar client
    //    ClientDto savedClient = service.createClient(clientDto);

        // Imprimir nom després de guardar
    //    System.out.println("Client guardat: " + savedClient.name());
   }
}
