package uz.greenstar.jolybell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import uz.greenstar.jolybell.api.product.SizeItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {
    private String id = UUID.randomUUID().toString();
    private String name;
    private BigDecimal cost;
    private List<String> imageItems;
    private List<String> descriptionItems;
    private long count;
    private String advice;
    private List<String> sizeItems;
    private String categoryId;
    private boolean withSize;

    public CreateProductDTO(String name, BigDecimal cost, List<String> imageItems, List<String> descriptionItems, String advice, List<String> sizeItems, String categoryId) {
        this.name = name;
        this.cost = cost;
        this.imageItems = imageItems;
        this.descriptionItems = descriptionItems;
        this.advice = advice;
        this.sizeItems = sizeItems;
        this.categoryId = categoryId;
    }
}
