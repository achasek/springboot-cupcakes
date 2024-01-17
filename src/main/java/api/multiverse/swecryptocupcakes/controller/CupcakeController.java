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
    public ResponseEntity<Cupcake[]> getCupcakes(@RequestParam(required = false) String flavor) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Cupcake[] cupcakes = mapper.readValue(cupcakeResource, Cupcake[].class);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        if (flavor != null) {
            cupcakes = Arrays.stream(cupcakes).filter(c -> Objects.equals(c.getFlavor(), flavor)).toArray(Cupcake[]::new);
        }

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
        Cupcake[] cupcake = Arrays.stream(mapper.readValue(cupcakeResource, Cupcake[].class)).filter(c -> Objects.equals(c.getId(), id)).toArray(Cupcake[]::new);
        TextEncryptor encryptor = Encryptors.text(encryptionPass, encryptionSalt);

        if (cupcake.length == 0) {
            return new ResponseEntity<>("Cupcake not found", HttpStatus.NOT_FOUND);
        }
        String decryptedCupcake = encryptor.decrypt(cupcake[0].getInstructions());
        cupcake[0].setInstructions(decryptedCupcake);
        return new ResponseEntity<>(cupcake[0], HttpStatus.OK);


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
