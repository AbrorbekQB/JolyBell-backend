package uz.greenstar.jolybell.api.filterForm;

import lombok.Data;

import java.util.Map;

@Data
public class FilterRequest {
    private Integer page;
    private Integer length = 10;
    private Map<String, String> filterData;
}
