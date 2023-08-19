package net.remgant.restdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.extern.slf4j.Slf4j;
import net.remgant.restdemo.dao.PersonRepository;
import net.remgant.restdemo.models.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class Controller {

    final private PersonRepository repository;

    public Controller(PersonRepository repository) {
        this.repository = repository;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    static public class ResourceNotFoundException extends RuntimeException {

    }
    @RequestMapping(value="/person/{personId}", method = RequestMethod.GET)
    @ResponseBody
    public Person person(@PathVariable long personId) {
        log.info("Get request for person {}", personId);
        return repository.findById(personId).orElseThrow(ResourceNotFoundException::new);
    }

    @RequestMapping(value = "/person", method = RequestMethod.GET)
    public Collection<Person> person() {
        log.info("Get request for all persons");
        return repository.findAll();
    }

    @RequestMapping(value="/person", method = RequestMethod.POST)
    public Person person(@RequestBody Person person) {
        log.info("request to create person {}", person);
        return repository.save(person);
    }

    @RequestMapping(value="/person/{personId}", method = RequestMethod.PUT)
    public Person person(@RequestBody Person person, @PathVariable long personId) {
        log.info("request to upsert {}: {}", personId, person);
        return repository.findById(personId)
                .map(p -> {
                    p.setFirstName(person.getFirstName());
                    p.setLastName(person.getLastName());
                    p.setAddress(person.getAddress());
                    p.setCity(person.getCity());
                    p.setState(person.getState());
                    p.setZipCode(person.getZipCode());
                    return repository.save(p);
                })
                .orElseGet(() -> repository.save(person));
    }

    @RequestMapping(value="/person/{personId}", method = RequestMethod.DELETE)
    public ResponseEntity<Person> deletePerson(@PathVariable long personId) {
        log.info("request to delete person {}", personId);
        if (!repository.existsById(personId))
            throw new ResourceNotFoundException();
        repository.deleteById(personId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value="/person/{personId}", method = RequestMethod.PATCH, consumes = "application/json-patch+json")
    public ResponseEntity<Person> person(@PathVariable long personId, @RequestBody JsonPatch jsonPatch) {
        log.info("Request to update fields for person for {}: {}", personId, jsonPatch);
        Person person = repository.findById(personId).orElseThrow(ResourceNotFoundException::new);
        try {
            Person personPatched = applyPatchToPerson(jsonPatch, person);
            repository.saveAndFlush(personPatched);
            return ResponseEntity.ok(personPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Person applyPatchToPerson(JsonPatch jsonPatch, Person targetPerson) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(targetPerson, JsonNode.class));
        return objectMapper.treeToValue(patched, Person.class);
    }
}
