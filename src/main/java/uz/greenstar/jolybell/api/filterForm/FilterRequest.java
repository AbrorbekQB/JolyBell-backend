package uz.greenstar.jolybell.api.filterForm;

import lombok.Data;

import java.util.Map;

@Data
public class FilterRequest {
    private Integer draw;
    private Integer page;
    private Integer length = 20;
    private Map<String, String> filterData;
}
