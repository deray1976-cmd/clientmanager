package com.example.clientmanager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
     service.createClient("Anna");
}
}
