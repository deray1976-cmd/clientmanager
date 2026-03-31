# ClientManager - Instruccions de Codi

Patrons establerts per a mantenir consistència, qualitat i escalabilitat del projecte ClientManager.

---

## 🏗️ Arquitectura: Entity → Model → DTO

**Responsabilitat de cada capa:**
- **Entity**: JPA, persistència, relacions amb BD
- **Model**: Lògica de negoci, validacions
- **DTO**: Transferència REST, només camps necessaris

```
Request → DTO → Model → Entity → BD
Response ← DTO ← Model ← Entity ← BD
```

---

## ✅ Validació Centralitzada (AdviseController)

### Principis
1. **Una sola fonte de veritat**: Les regles de negoci viuen a `AdviseController`
2. **ThreadLocal per errors**: Captura missatges detallats sense excepcions
3. **Separació clar**: Validació != Lògica de negoci

### Patró estàndard

```java
// 1. AdviseController: define les regles
@Component
public class AdviseController {
    private static final ThreadLocal<String> lastErrorMessage = new ThreadLocal<>();
    
    public boolean adviseUpdateClient(ClientModel client) {
        if (client.getEdat() < 0 || client.getEdat() > 120) {
            setErrorMessage("Edat fora de rang [0-120]: " + client.getEdat());
            return false;
        }
        return true;
    }
    
    public String getLastErrorMessage() { return lastErrorMessage.get(); }
    private void setErrorMessage(String msg) { lastErrorMessage.set(msg); }
}

// 2. Controller: usa AdviseController
if (!adviseController.adviseUpdateClient(client)) {
    return ResponseEntity.badRequest().body(
        ErrorResponse.badRequest("No es pot actualitzar el client", 
                                 adviseController.getLastErrorMessage()));
}

// 3. Service: valida model i persiseix
@Transactional
public ClientModel update(Long id, ClientModel clientModel) {
    validateClientModel(clientModel); // Última validació
    // ... persisència
}
```

### Constants de validació
```java
// Edats
public static final int MIN_AGE = 0;
public static final int MAX_AGE = 120;

// Adreces
public static final int MIN_ADDRESSES = 1;
public static final int STREET_MIN_LENGTH = 3;
public static final int STREET_MAX_LENGTH = 100;
public static final int CITY_MIN_LENGTH = 2;
public static final int CITY_MAX_LENGTH = 50;
```

---

## 📦 DTOs: Factory Methods + Compact Constructors

### Estructura estàndard

```java
public record ClientDto(
    Long id,
    String name,
    String surname,
    Integer edat,
    String dni,
    String email,
    List<AddressDto> addresses
) {
    // Factory methods per a casos comuns
    public static ClientDto newClient(String name, String surname, String email) {
        return new ClientDto(null, name.trim(), surname.trim(), null, "", email.toLowerCase(), null);
    }
    
    public static ClientDto existing(ClientModel model) {
        return new ClientDto(
            model.getId(),
            model.getName(),
            model.getSurname(),
            model.getEdat(),
            model.getDni().toUpperCase(),
            model.getEmail().toLowerCase(),
            model.getAddresses() != null ? model.getAddresses().stream()
                .map(AddressDto::fromModel)
                .collect(Collectors.toList()) : null
        );
    }
    
    // Compact constructor: normalització
    public ClientDto {
        if (name != null) name = name.trim();
        if (surname != null) surname = surname.trim();
        if (dni != null) dni = dni.toUpperCase();
        if (email != null) email = email.toLowerCase();
    }
}
```

### Validacions en DTOs
```java
public record ClientDto(
    @NotBlank(message = "Nom és obligatori") String name,
    @NotBlank(message = "Email és obligatori") @Email String email,
    @Min(value = 0) @Max(value = 120) Integer edat,
    @Size(min = 1, message = "Almenys una adreça requerida") List<AddressDto> addresses
) { ... }
```

---

## 🔀 Mapatge: EntityMapper + DtoMapper

### Responsabilitats
- **EntityMapper** (Entity ↔ Model): Conversió de persistència
- **DtoMapper** (DTO ↔ Model): Conversió REST

```java
// EntityMapper
@Mapper(componentModel = "spring")
public interface ClientEntityModelMapper {
    
    ClientModel toModel(ClientEntity entity);
    
    ClientEntity toEntity(ClientModel model);
    
    @Mapping(target = "addresses", ignore = true)
    void updateEntityFromModel(ClientModel model, @MappingTarget ClientEntity entity);
}

// DtoMapper
@Mapper(componentModel = "spring", uses = AddressDtoMapper.class)
public interface ClientDtoMapper {
    
    ClientModel toModel(ClientDto dto);
    
    ClientDto toDto(ClientModel model);
}
```

---

## 📩 ErrorResponse: Estructura estàndard

```java
public record ErrorResponse(
    String message,      // Què va malament (cat/fra/eng)
    String reason,       // Per què va malament (detallar causa)
    int status,         // HTTP status
    LocalDateTime timestamp
) {
    public static ErrorResponse badRequest(String message, String reason) {
        return new ErrorResponse(message, reason, 400, LocalDateTime.now());
    }
    
    public static ErrorResponse notFound(String message) {
        return new ErrorResponse(message, "Recurs no trobat", 404, LocalDateTime.now());
    }
}
```

---

## 🌐 Separació de Responsabilitats: Client vs Adreces

### Regla de or
**Els endpoints de CLIENT només toquen els 5 camps bàsics:**
- `name`, `surname`, `edat`, `dni`, `email`

**Les ADRECES es manejen en endpoints separats:**
- `POST /clients/{id}/addresses` - crear adreça
- `PUT /clients/{id}/addresses` - actualitzar totes les adreces
- `DELETE /clients/{id}/addresses/{addressId}` - eliminar una

### Implementació
```java
// ClientService.update() - SOLO els 5 camps
@Transactional
public ClientModel update(Long id, ClientModel clientModel) {
    ClientEntity existing = clientRepository.findByIdWithAddresses(id)
        .orElseThrow(() -> new ClientNotFoundException(id));
    
    // Actualitza SOLO dades, les adreces es mantenen intactes
    existing.setName(clientModel.getName());
    existing.setSurname(clientModel.getSurname());
    existing.setEdat(clientModel.getEdat());
    existing.setDni(clientModel.getDni());
    existing.setEmail(clientModel.getEmail());
    // NO touch addresses!
    
    return entityMapper.toModel(repository.save(existing));
}

// AddressService.updateAddresses() - Maneja la col·lecció
@Transactional
public void updateAddresses(Long clientId, List<AddressModel> newAddresses) {
    // Lògica completa de sincronització
}
```

---

## 🗣️ Idiomatització: Catalan per a Missatges

### Convencions
- **Mensatges de negoci**: Sempre en **català**
- **Logs tècnics**: Anglès (stacktraces, classes, etc.)
- **Comments**: Català si és conceptual, anglès si és tecnicitat

```java
// ✅ BON
if (client.getEdat() > 120) {
    log.warn("Edat inválida per clientId={}", id);
    setErrorMessage("Edat fora de rang [0-120]: " + age);
}

// ❌ MALAMENT
if (client.getEdat() > 120) {
    setErrorMessage("Age out of range [0-120]: " + age);
}
```

### Recursos de traducció
Mantén les propietats a `src/main/resources/`:
- `messages.properties` (català per defecte)
- `messages_en.properties`
- `messages_fr.properties`
- `messages_ca.properties`

---

## 🛠️ Convencions de Codi

### Nomenclatura
| Element | Pattern | Exemple |
|---------|---------|---------|
| Classes | PascalCase | `ClientService`, `ClientDto`, `ErrorResponse` |
| Methods | camelCase | `getLastErrorMessage()`, `adviseUpdateClient()` |
| Constants | UPPER_SNAKE_CASE | `MIN_AGE`, `MAX_STREET_LENGTH` |
| Fields (private) | camelCase + `private` | `private ClientRepository repository` |
| ThreadLocal | descriptive + ThreadLocal suffix | `lastErrorMessage`, `transactionContext` |

### Annotations
```java
// DTOs
@NotBlank, @Email, @Min, @Max, @Size

// Services  
@Transactional (read-only si és SELECT)
@Autowired (constructor injection always)

// Controllers
@RestController, @PostMapping, @PutMapping, @DeleteMapping
@Valid, @PathVariable, @RequestBody
```

### Logs
```java
// INFO: operacions exitoses
log.info("Client {} actualitzat correctament", id);

// WARN: validacions que fallen
log.warn("Client no compleix les regles de negoci: {}", errorMsg);

// ERROR: excepcions tècniques
log.error("Database error creating client", e);

// DEBUG: traces detallats (development only)
log.debug("Validating client with edat={}", client.getEdat());
```

---

## 🔗 Relacions JPA: Cascade i Orphan Removal

```java
@Entity
public class ClientEntity {
    @OneToMany(
        mappedBy = "client",
        cascade = CascadeType.ALL,      // Propagar saves/deletes
        orphanRemoval = true             // Eliminar adreces orfanes
    )
    private List<AddressEntity> addresses;
}
```

**Quan usar `findByIdWithAddresses()`:**
```java
// ✅ Usar quan toques relacions
ClientEntity existing = clientRepository.findByIdWithAddresses(id);

// ❌ NO usar si només touches camps simples
ClientEntity existing = clientRepository.findById(id); // Suficient
```

---

## ✨ Checklist per a PR/Commits

- [ ] Validacions a AdviseController (si és de negoci)
- [ ] Missatges d'error en català detallats
- [ ] DTOs amb factory methods i compact constructors
- [ ] MapperMappers actualitzats
- [ ] ResponseEntity<?> retorna DTO o ErrorResponse
- [ ] Adreces NO incloses en ClientDto.update()
- [ ] Service.update() usa `findByIdWithAddresses()` si toca relacions
- [ ] Tests unitaris per a validators
- [ ] Logs a nivell apropiat (INFO/WARN/ERROR)
- [ ] Documentació d'API REST si afecta endpoints

---

## 📚 Enllaços Interns
- **Handlers**: [AdviseController.java](src/main/java/com/example/clientmanager/advisors/AdviseController.java)
- **Services**: [ClientService.java](src/main/java/com/example/clientmanager/service/ClientService.java)
- **Mappers**: [ClientEntityModelMapper](src/main/java/com/example/clientmanager/dto/ClientEntityModelMapper.java)
- **Tests**: [ApplicationControllerIntegrationTest.java](src/test/java)

---

**Última actualització:** 30 de Març de 2026  
**Responsable:** ClientManager Team  
**Branca:** Advise_Controller
