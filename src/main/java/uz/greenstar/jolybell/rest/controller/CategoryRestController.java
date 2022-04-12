package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.category.CategoryDTO;
import uz.greenstar.jolybell.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class CategoryRestController {
    private final CategoryService categoryService;

    @GetMapping("/get/{id}")
    public CategoryDTO get(@PathVariable("id") String id) {
        return categoryService.get(id);
    }

    @GetMapping("/list")
    public List<CategoryDTO> list() {
        return categoryService.getList();
    }
}
