package uz.greenstar.jolybell.api.product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SizeItem {
    private String name;
    private boolean active = true;
    private long count = 0;

    public SizeItem(String name) {
        this.name = name;
    }
}
