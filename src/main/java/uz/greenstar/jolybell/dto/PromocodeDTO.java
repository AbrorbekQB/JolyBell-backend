package uz.greenstar.jolybell.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromocodeDTO {
    private String id;
    private String code;
    private boolean active;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createDateTime;
    private LocalDateTime lastUpdateTime;
    private Double percent;
}
