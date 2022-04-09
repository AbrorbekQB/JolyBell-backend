package uz.greenstar.jolybell.api.pagination;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PaginationResponse {
    private Integer draw;
    private Integer count;
    private Integer totalCount;
    private Integer pages;
    private List<Object> data;
}
