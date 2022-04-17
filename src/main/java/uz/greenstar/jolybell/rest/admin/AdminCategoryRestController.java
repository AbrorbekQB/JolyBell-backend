package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.dto.category.CategoryDTO;
import uz.greenstar.jolybell.dto.category.EditCategoryDTO;
import uz.greenstar.jolybell.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryRestController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public void create(@RequestBody CategoryDTO dto) {
        categoryService.create(dto);
    }

    @GetMapping("/get/{id}")
    public CategoryDTO get(@PathVariable("id") String id) {
        return categoryService.get(id);
    }

    @PostMapping("/list")
    public List<CategoryDTO> list(@RequestBody FilterRequest request) {
        return categoryService.getListByAdmin(request);
    }

    @PostMapping("/edit")
    public void edit(@RequestBody EditCategoryDTO dto){
        categoryService.edit(dto);
    }

    @GetMapping("/change/active/{id}")
    public void changeActive(@PathVariable String id){
        categoryService.changeActiveByAdmin(id);
    }
}
