package uz.greenstar.jolybell.api.pagination;

import lombok.Data;

import java.util.Map;

@Data
public class PaginationRequest {
    private Integer draw;
    private Integer page;
    private Integer length = 20;
    private Map<String, String> filterData;
}
