package uz.greenstar.jolybell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String id = UUID.randomUUID().toString();
    private String name;
    private BigDecimal cost;
    private List<ImageItem> imageItems;
    private List<DescriptionItem> descriptionItems;
    private long count;
    private String advice;
    private List<String> size;
    private String categoryId;

    public ProductDTO(String name, BigDecimal cost, List<ImageItem> imageItems, List<DescriptionItem> descriptionItems, long count, String advice, List<String> size, String categoryId) {
        this.name = name;
        this.cost = cost;
        this.imageItems = imageItems;
        this.descriptionItems = descriptionItems;
        this.count = count;
        this.advice = advice;
        this.size = size;
        this.categoryId = categoryId;
    }
}
