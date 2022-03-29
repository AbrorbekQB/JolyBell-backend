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
import uz.greenstar.jolybell.api.order.AddressItem;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.enums.OrderState;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.enums.PaymentType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Table
@Entity(name = "orders")
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class OrderEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private LocalDateTime createdDateTime = LocalDateTime.now();
    private LocalDateTime lastUpdateTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.NONE;
    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.NONE;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NONE;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, OrderItem> orderItems = new HashMap();

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<AddressItem> addressItem = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private PromoCodeEntity promoCode;
}
