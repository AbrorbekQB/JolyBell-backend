package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.product.CreateProductDTO;
import uz.greenstar.jolybell.dto.product.ProductDTO;
import uz.greenstar.jolybell.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

//    @GetMapping("/{name}")
//    public void getByName(@PathVariable("name") String name) {
//        productService.getListByName(name);
//    }

    @GetMapping("/get/{id}")
    public ProductDTO getById(@PathVariable("id") String id) {
        return productService.getById(id);
    }

    @GetMapping("/list/{categoryName}")
    public List<ProductDTO> getList(@PathVariable("categoryName") String categoryName) {
        return productService.getList(categoryName);
    }
}
