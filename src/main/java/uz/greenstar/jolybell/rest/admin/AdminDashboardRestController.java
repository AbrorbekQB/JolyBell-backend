package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.greenstar.jolybell.service.DashboardService;

import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardRestController {
    private final DashboardService dashboardService;

    @GetMapping(path = "/user")
    public Map reportUser() {
        return dashboardService.reportUser();
    }

    @GetMapping(path = "/order")
    public Map reportOrder(){
        return dashboardService.reportOrder();
    }
}
