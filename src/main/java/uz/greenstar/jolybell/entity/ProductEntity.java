package uz.greenstar.jolybell.entity;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import uz.greenstar.jolybell.api.product.SizeItem;
import uz.greenstar.jolybell.dto.DescriptionItem;
import uz.greenstar.jolybell.dto.ImageItem;
import uz.greenstar.jolybell.enums.CurrencyType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Table
@Entity(name = "product")
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
})
public class ProductEntity {
    @Id
    private String id;
    @Column(unique = true)
    private String name;
    private BigDecimal cost;
    private long count;
    private String advice;
    private Boolean active;
    private Boolean withSize;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime lastUpdate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType = CurrencyType.USD;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<ImageItem> imageItems = new ArrayList<>();

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<DescriptionItem> descriptionItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
}