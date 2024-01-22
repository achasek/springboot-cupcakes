package api.multiverse.swecryptocupcakes.controller;

import api.multiverse.swecryptocupcakes.entity.Cupcake;
import api.multiverse.swecryptocupcakes.payload.CupcakeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
// arrayList is mutable. Can add and remove items. Necessary for post and delete routes
import java.util.ArrayList;
// arrays are fixed in Java (immutable). Can hold primitive datatypes or objects
import java.util.Arrays;
// cannot instantiate List itself
import java.util.List;
import java.util.Objects;

@RestController
// RequestMapping determines the base prefix of any given route of any given request method in this file. So all routes here will be prefixed with "/cupcakes"
@RequestMapping("/cupcakes")
public class CupcakeController {

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${encryption.password}")
    private String encryptionPass;

    @Value("${encryption.salt}")
    private String encryptionSalt;


    // GetMapping is the annotation for all GET routes
    // GetMapping with nothing just means that the endpoint IS the base URL defined above in @RequestMapping
    // @RequestParam(required = false) also specifies that no Param in URL is expected
    @GetMapping
    public ResponseEntity<Cupcake[]> getCupcakes(@RequestParam(required = false) String flavor) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Cupcake[] cupcakes = mapper.readValue(cupcakeResource, Cupcake[].class);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        // we can return only cupcakes of a certain flavor, akin to .filter in javascript, through query params
        if (flavor != null) {
            cupcakes = Arrays.stream(cupcakes).filter(c -> Objects.equals(c.getFlavor(), flavor)).toArray(Cupcake[]::new);
        }

        for (int i = 0; i < cupcakes.length; i++) {
            String decryptedCupcake = encryptor.decrypt(cupcakes[i].getInstructions());
            cupcakes[i].setInstructions(decryptedCupcake);

        }
        return new ResponseEntity<>(cupcakes, HttpStatus.OK);

    }

    // and now with an argument, this endpoint is "/cupcakes/:id"
        @GetMapping("{id}")
    public ResponseEntity<?> getCupcake(@PathVariable Long id) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Cupcake[] cupcake = Arrays.stream(mapper.readValue(cupcakeResource, Cupcake[].class)).filter(c -> Objects.equals(c.getId(), id)).toArray(Cupcake[]::new);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        if (cupcake.length == 0) {
            return new ResponseEntity<>("Cupcake not found", HttpStatus.NOT_FOUND);
        }
        String decryptedCupcake = encryptor.decrypt(cupcake[0].getInstructions());
        cupcake[0].setInstructions(decryptedCupcake);
        return new ResponseEntity<>(cupcake[0], HttpStatus.OK);


    }

    // PostMapping is for all POST requests
    @PostMapping
    public ResponseEntity<Cupcake> createCupcake(@RequestBody CupcakeDTO data) throws IOException {
        // this gets the file, seedData in this case
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        // this serializes the file into JSON data - serializes responses and deserializes requests
        ObjectMapper mapper = new ObjectMapper();
        // configures how to serialized data (is customizable)
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        // variable should be an array of Cupcake entities
        Cupcake[] cupcakes = mapper.readValue(cupcakeResource, Cupcake[].class);
        // encrypts info that needs to be encrypted, like with saltRounds here and user info as to who created it
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        // To instantiate an ArrayList, you must extend the List interface like this. List is the parent of ArrayList
        // You cannot do this for ex. since this is immutable and does not include .add()
        // Cupcake[] cupcakesList = new ArrayList<Cupcake>;
        // Arrays.asList(cupcakes) asserts a copy of the immutable cupcakes array on line 92 as a mutable List now
        List<Cupcake> cupcakesList = new ArrayList<Cupcake>(Arrays.asList(cupcakes));
        Cupcake cupcakeToAdd = new Cupcake(cupcakes[cupcakes.length - 1].getId() + 1, data.getFlavor(), encryptor.encrypt(data.getInstructions()));
        cupcakesList.add(cupcakeToAdd);

        // this accesses the seedData and writes in a new value, the 2nd argument. It adds
        mapper.writeValue(resourceLoader.getResource("classpath:seedData.json").getFile(), cupcakesList);
        // responds with newly created cupcake and HttpStatus upon success
        return new ResponseEntity<>(cupcakeToAdd, HttpStatus.OK);
    }
}
