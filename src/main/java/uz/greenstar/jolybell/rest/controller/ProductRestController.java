package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.ProductDTO;
import uz.greenstar.jolybell.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class ProductRestController {
    private final ProductService productService;

    @PostMapping("/create")
    public void create(@RequestBody ProductDTO productDTO) {
        productService.create(productDTO);
    }

    @GetMapping("/{name}")
    public void getByName(@PathVariable("name") String name){
        productService.getListByName(name);
    }

    @GetMapping("/get/{id}")
    public ProductDTO getById(@PathVariable("id") String id){
        return productService.getById(id);
    }

    @GetMapping("/list")
    public List<ProductDTO> getList() {
        return productService.getList();
    }
}
