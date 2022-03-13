package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.CategoryDTO;
import uz.greenstar.jolybell.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryRestController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public void create(@RequestBody CategoryDTO dto) {
        categoryService.create(dto);
    }

    @GetMapping("/get/{id}")
    public CategoryDTO get(@PathVariable("id") String id) {
        return categoryService.get(id);
    }

    @GetMapping("/list")
    public List<CategoryDTO> list(){
        return categoryService.getList();
    }
}
