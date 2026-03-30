package com.example.clientmanager.advisors;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.model.ClientModel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AdviseController - Component amb les regles de negoci per a validacions.
 * Centralitza totes les regles de validació de negoci i guarda el darrer missatge d'error.
 */
@Component
public class AdviseController {

    private static final Logger log = LoggerFactory.getLogger(AdviseController.class);

    // ----------------------------
    // CONSTANTS - REGLAS DE NEGOCI
    // ----------------------------
    private static final int MIN_CLIENT_AGE_TO_DELETE = 18;
    private static final int MIN_CLIENT_AGE = 0;
    private static final int MAX_CLIENT_AGE = 120;
    private static final int MIN_ADDRESSES = 1;
    private static final int MIN_ADDRESS_LENGTH = 3;
    private static final int MAX_ADDRESS_LENGTH = 100;

    // Cache del últim error
    private ThreadLocal<String> lastErrorMessage = new ThreadLocal<>();

    // ----------------------------
    // GETTER PER AL ÚLTIM ERROR
    // ----------------------------
    /**
     * Obté el darrer missatge d'error i el neteja.
     */
    public String getLastErrorMessage() {
        String message = lastErrorMessage.get();
        lastErrorMessage.remove();
        return message != null ? message : "Error desconegut";
    }

    /**
     * Estableix un missatge d'error.
     */
    private void setErrorMessage(String message) {
        this.lastErrorMessage.set(message);
    }

    // ----------------------------
    // CREAR CLIENT
    // ----------------------------
    /**
     * Valida si es pot crear un client.
     */
    public boolean adviseCreateClient(ClientModel client) {
        if (client == null) {
            setErrorMessage("Intent de crear client nul");
            log.warn("Intent de crear client nul");
            return false;
        }

        if (client.getName() == null || client.getName().trim().isEmpty()) {
            setErrorMessage("Nom del client obligatori");
            log.warn("Nom del client obligatori");
            return false;
        }

        if (client.getSurname() == null || client.getSurname().trim().isEmpty()) {
            setErrorMessage("Cognom del client obligatori");
            log.warn("Cognom del client obligatori");
            return false;
        }

        if (client.getEdat() == null) {
            setErrorMessage("Edat del client obligatòria");
            log.warn("Edat del client obligatòria");
            return false;
        }

        if (client.getEdat() < MIN_CLIENT_AGE || client.getEdat() > MAX_CLIENT_AGE) {
            String msg = String.format("Edat del client fora de rang [%d-%d]: %d", MIN_CLIENT_AGE, MAX_CLIENT_AGE, client.getEdat());
            setErrorMessage(msg);
            log.warn(msg);
            return false;
        }

        if (client.getDni() == null || client.getDni().trim().isEmpty()) {
            setErrorMessage("DNI del client obligatori");
            log.warn("DNI del client obligatori");
            return false;
        }

        if (!isValidEmail(client.getEmail())) {
            setErrorMessage("Email del client invàlid: " + client.getEmail());
            log.warn("Email del client invàlid");
            return false;
        }

        log.info("Client validat correctament: {}", client.getId());
        return true;
    }

    // ----------------------------
    // ACTUALITZAR CLIENT
    // ----------------------------
    /**
     * Valida si es pot actualitzar un client.
     */
    public boolean adviseUpdateClient(ClientModel client) {
        if (client == null) {
            setErrorMessage("Intent d'actualitzar client nul");
            log.warn("Intent d'actualitzar client nul");
            return false;
        }

        if (!isValidEmail(client.getEmail())) {
            setErrorMessage(String.format("Email invàlid: %s", client.getEmail()));
            log.warn("Email no vàlid: {}", client.getEmail());
            return false;
        }

        if (client.getEdat() != null && (client.getEdat() < MIN_CLIENT_AGE || client.getEdat() > MAX_CLIENT_AGE)) {
            String msg = String.format("Edat fora de rang [%d-%d]: %d", MIN_CLIENT_AGE, MAX_CLIENT_AGE, client.getEdat());
            setErrorMessage(msg);
            log.warn(msg);
            return false;
        }

        log.info("Client actualitzat correctament: {}", client.getId());
        return true;
    }

    // ----------------------------
    // ELIMINAR CLIENT
    // ----------------------------
    /**
     * Valida si es pot eliminar un client segons condicions de negoci.
     */
    public boolean adviseDeleteClient(ClientModel client) {
        if (client == null) {
            setErrorMessage("Intent d'eliminar client nul");
            log.warn("Attempt to delete null client");
            return false;
        }

        if (client.getId() == null) {
            setErrorMessage("Client sense id");
            log.warn("Client sense id");
            return false;
        }

        if (client.getEdat() == null) {
            setErrorMessage("Edat del client no disponible");
            log.warn("Edat del client no disponible");
            return false;
        }

        if (client.getEdat() < MIN_CLIENT_AGE_TO_DELETE) {
            String msg = String.format("No es pot eliminar un client menor de %d anys. Edat actual: %d", MIN_CLIENT_AGE_TO_DELETE, client.getEdat());
            setErrorMessage(msg);
            log.warn(msg);
            return false;
        }

        log.info("Client {} autoritzat per a eliminació", client.getId());
        return true;
    }

    // ----------------------------
    // CREAR ADREÇA
    // ----------------------------
    /**
     * Valida si es pot crear una adreça.
     */
    public boolean adviseCreateAddress(ClientModel client, AddressDto address) {
        if (client == null) {
            setErrorMessage("Intent de crear adreça per a client nul");
            log.warn("Attempt to create address for null client");
            return false;
        }

        if (address == null) {
            setErrorMessage("Intent de crear adreça nula");
            log.warn("Attempt to create null address");
            return false;
        }

        if (!isValidStreet(address.street())) {
            String msg = String.format("Carrer invàlid: '%s' (ha de tenir entre %d i %d caràcters)", 
                                      address.street(), MIN_ADDRESS_LENGTH, MAX_ADDRESS_LENGTH);
            setErrorMessage(msg);
            log.warn("Carrer invàlid: {}", address.street());
            return false;
        }

        if (!isValidCity(address.city())) {
            String msg = String.format("Ciutat invàlida: '%s' (ha de tenir entre 2 i 50 caràcters)", address.city());
            setErrorMessage(msg);
            log.warn("Ciutat invàlida: {}", address.city());
            return false;
        }

        log.info("Adreça validada per al client: {}", client.getId());
        return true;
    }

    // ----------------------------
    // ACTUALITZAR ADRECES
    // ----------------------------
    /**
     * Valida que es pugui actualitzar les adreces d'un client.
     */
    public boolean adviseUpdateAddresses(ClientModel client, List<AddressDto> addresses) {
        if (client == null) {
            setErrorMessage("Intent d'actualitzar adreces per a client nul");
            log.warn("Attempt to update addresses for null client");
            return false;
        }

        if (addresses == null || addresses.isEmpty()) {
            setErrorMessage("Llista d'adreces buida o nula");
            log.warn("Llista d'adreces buida o nula per al client: {}", client.getId());
            return false;
        }

        // Validar cada adreça
        for (int i = 0; i < addresses.size(); i++) {
            AddressDto address = addresses.get(i);
            if (address == null) {
                setErrorMessage(String.format("Adreça %d de la llista és nula", i + 1));
                log.warn("Adreça nula en la llista");
                return false;
            }

            if (!isValidStreet(address.street())) {
                String msg = String.format("Adreça %d: Carrer invàlid: '%s' (ha de tenir entre %d i %d caràcters)", 
                                          i + 1, address.street(), MIN_ADDRESS_LENGTH, MAX_ADDRESS_LENGTH);
                setErrorMessage(msg);
                log.warn("Carrer invàlid: {}", address.street());
                return false;
            }

            if (!isValidCity(address.city())) {
                String msg = String.format("Adreça %d: Ciutat invàlida: '%s' (ha de tenir entre 2 i 50 caràcters)", 
                                          i + 1, address.city());
                setErrorMessage(msg);
                log.warn("Ciutat invàlida: {}", address.city());
                return false;
            }
        }

        // Validar que hi hagi almenys una adreça
        if (addresses.size() < MIN_ADDRESSES) {
            setErrorMessage("Ha de tenir almenys una adreça");
            log.warn("Ha de tenir almenys una adreça el client: {}", client.getId());
            return false;
        }

        log.info("Adreces actualitzades correctament per al client: {}", client.getId());
        return true;
    }

    // ----------------------------
    // ELIMINAR ADREÇA
    // ----------------------------
    /**
     * Valida que es pugui eliminar una adreça concreta.
     */
    public boolean adviseDeleteAddress(ClientModel client, Long addressId) {
        if (client == null) {
            setErrorMessage("Intent d'eliminar adreça per a client nul");
            log.warn("Client nul");
            return false;
        }

        if (client.getAddresses() == null) {
            setErrorMessage("Client sense adreces");
            log.warn("Client sense adreces");
            return false;
        }

        if (addressId == null) {
            setErrorMessage("Id de l'adreça no especificat");
            log.warn("AddressId nul");
            return false;
        }

        boolean exists = client.getAddresses().stream()
                .anyMatch(a -> a.getId() != null && a.getId().equals(addressId));

        if (!exists) {
            setErrorMessage(String.format("L'adreça amb id=%d no pertany al client", addressId));
            log.warn("L'adreça {} no pertany al client {}", addressId, client.getId());
            return false;
        }

        // No permet eliminar l'última adreça
        if (client.getAddresses().size() <= MIN_ADDRESSES) {
            setErrorMessage("No es pot eliminar l'única adreça del client. Ha de tenir almenys una.");
            log.warn("No es pot eliminar l'última adreça del client: {}", client.getId());
            return false;
        }

        log.info("Adreça {} eliminada correctament del client {}", addressId, client.getId());
        return true;
    }

    // ----------------------------
    // MÉTODES AUXILIARS DE VALIDACIÓ
    // ----------------------------

    /**
     * Valida format d'email.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Valida carrer.
     */
    private boolean isValidStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            return false;
        }
        String trimmed = street.trim();
        return trimmed.length() >= MIN_ADDRESS_LENGTH && trimmed.length() <= MAX_ADDRESS_LENGTH;
    }

    /**
     * Valida ciutat.
     */
    private boolean isValidCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return false;
        }
        String trimmed = city.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 50;
    }
}