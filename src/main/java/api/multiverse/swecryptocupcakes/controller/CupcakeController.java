package api.multiverse.swecryptocupcakes.controller;

import api.multiverse.swecryptocupcakes.entity.Cupcake;
import api.multiverse.swecryptocupcakes.payload.CupcakeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<Cupcake[]> getCupcakes() throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        return new ResponseEntity<>(mapper.readValue(cupcakeResource, Cupcake[].class), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getCupcake(@PathVariable Long id) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        Cupcake[] cupcakes = mapper.readValue(cupcakeResource, Cupcake[].class);
        Object[] cupcake = Arrays.stream(cupcakes).filter(c -> Objects.equals(c.getId(), id)).toArray();
        if (cupcake.length == 0) {
            return new ResponseEntity<>("Cupcake not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cupcake[0], HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Cupcake> createCupcake(@RequestBody CupcakeDTO data) throws IOException {
        File cupcakeResource = resourceLoader.getResource("classpath:seedData.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        Cupcake[] cupcakes = mapper.readValue(cupcakeResource, Cupcake[].class);

        List<Cupcake> cupcakesList = new ArrayList<Cupcake>(Arrays.asList(cupcakes));
        Cupcake cupcakeToAdd = new Cupcake(cupcakes[cupcakes.length - 1].getId() + 1, data.getFlavor(), data.getInstructions());
        cupcakesList.add(cupcakeToAdd);

        mapper.writeValue(cupcakeResource, cupcakesList);
        return new ResponseEntity<>(cupcakeToAdd, HttpStatus.OK);
    }
}
