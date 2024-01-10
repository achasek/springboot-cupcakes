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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/cupcakes")
public class CupcakeController {

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${encryption.password}")
    private String encryptionPass;

    @Value("${encryption.salt}")
    private String encryptionSalt;


    @GetMapping
    public ResponseEntity<Cupcake[]> getCupcakes() throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Cupcake[] cupcakes = mapper.readValue(cupcakeResource, Cupcake[].class);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        for (int i = 0; i < cupcakes.length; i++) {
            String decryptedCupcake = encryptor.decrypt(cupcakes[i].getInstructions());
            cupcakes[i].setInstructions(decryptedCupcake);
        }
        return new ResponseEntity<>(cupcakes, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCupcake(@PathVariable Long id) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Object[] cupcakeFilter = Arrays.stream(mapper.readValue(cupcakeResource, Cupcake[].class)).filter(c -> Objects.equals(c.getId(), id)).toArray();
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        if (cupcakeFilter.length == 0) {
            return new ResponseEntity<>("Cupcake not found", HttpStatus.NOT_FOUND);
        }
        Cupcake cupcake = (Cupcake) cupcakeFilter[0];
        String decryptedCupcake = encryptor.decrypt(cupcake.getInstructions());
        cupcake.setInstructions(decryptedCupcake);
        return new ResponseEntity<>(cupcake, HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Cupcake> createCupcake(@RequestBody CupcakeDTO data) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        Cupcake[] cupcakes = mapper.readValue(cupcakeResource, Cupcake[].class);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        List<Cupcake> cupcakesList = new ArrayList<Cupcake>(Arrays.asList(cupcakes));
        Cupcake cupcakeToAdd = new Cupcake(cupcakes[cupcakes.length - 1].getId() + 1, data.getFlavor(), encryptor.encrypt(data.getInstructions()));
        cupcakesList.add(cupcakeToAdd);

        mapper.writeValue(resourceLoader.getResource("classpath:seedData.json").getFile(), cupcakesList);
        return new ResponseEntity<>(cupcakeToAdd, HttpStatus.OK);
    }
}
