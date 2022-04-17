package uz.greenstar.jolybell.dto.product;

import lombok.Data;
import uz.greenstar.jolybell.api.product.SizeItem;
import uz.greenstar.jolybell.dto.DescriptionItem;
import uz.greenstar.jolybell.dto.ImageItem;
import uz.greenstar.jolybell.dto.ProductCountDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private BigDecimal cost;
    private List<ImageItem> imageItems;
    private List<DescriptionItem> descriptionItems;
    private long count;
    private String advice;
    private List<ProductCountDTO> productCountList = new ArrayList<>();
    private String categoryName;
    private boolean withSize;
    private String createDate;
}
