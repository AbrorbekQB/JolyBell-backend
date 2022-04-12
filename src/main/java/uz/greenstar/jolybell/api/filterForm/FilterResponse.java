package uz.greenstar.jolybell.api.filterForm;

import lombok.Data;

import java.util.List;

@Data
public class FilterResponse {
    private Integer draw;
    private Integer count;
    private Integer totalCount;
    private Integer pages;
    private List<Object> data;
}
