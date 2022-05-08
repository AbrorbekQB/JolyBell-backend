package uz.greenstar.jolybell.api.dashboard;

import lombok.Data;

import java.util.Map;

@Data
public class DashboardResponse {
    private Map<String, Object> data;
}
