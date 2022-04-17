package uz.greenstar.jolybell.api.filterForm;

import lombok.Data;

import java.util.List;

@Data
public class FilterResponse {
    private Integer count;
    private Long totalCount;
    private Integer pages;
    private List<?> data;
}
