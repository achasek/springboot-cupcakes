package api.multiverse.swecryptocupcakes.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cupcake {
    private Long id;
    private String flavor;
    private String instructions;
}
