package uz.greenstar.jolybell.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.dto.user.UserDTO;
import uz.greenstar.jolybell.enums.OrderState;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.enums.PaymentType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private Boolean fullReserved;
    private LocalDateTime bookedDateTime;
    private String note;
    private LocalDateTime createdDateTime = LocalDateTime.now();
    private LocalDateTime lastUpdateTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.NONE;
    @Enumerated(EnumType.STRING)
    private OrderState state = OrderState.PENDING;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, List<OrderItem>> orderItems = new HashMap();

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private UserDTO receiverDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private PromoCodeEntity promoCode;
}
