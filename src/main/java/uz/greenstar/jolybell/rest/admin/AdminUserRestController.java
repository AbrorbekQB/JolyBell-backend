package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.pagination.PaginationRequest;
import uz.greenstar.jolybell.api.pagination.PaginationResponse;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class AdminUserRestController {
    private final UserService userService;

    @PostMapping("/list")
    public PaginationResponse getList(@RequestBody PaginationRequest request) {
        return userService.getListForAdmin(request);
    }

    @PostMapping("/create")
    public void createAdmin(@RequestBody UserDTO dto){
        userService.createAdmin(dto);
    }
}
