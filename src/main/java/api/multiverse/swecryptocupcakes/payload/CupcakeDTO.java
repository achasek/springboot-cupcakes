package api.multiverse.swecryptocupcakes.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CupcakeDTO {
    private String flavor;
    private String instructions;
}
