package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.api.filterForm.FilterResponse;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.service.UserService;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserRestController {
    private final UserService userService;

    @PostMapping("/list")
    public FilterResponse getList(@RequestBody FilterRequest request) {
        return userService.getListForAdmin(request);
    }

    @PostMapping("/create")
    public void createAdmin(@RequestBody UserDTO dto){
        userService.createAdmin(dto);
    }
}
