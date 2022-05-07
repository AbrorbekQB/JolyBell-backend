package uz.greenstar.jolybell.dto.product;

import lombok.Data;
import uz.greenstar.jolybell.dto.DescriptionItem;
import uz.greenstar.jolybell.dto.ImageItem;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductEditDTO {
    private String id;
    private String name;
    private BigDecimal cost;
    private List<ImageItem> imageItems;
    private List<String> descriptionItems;
    private String advice;
}
