package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.greenstar.jolybell.dto.CreateProductDTO;
import uz.greenstar.jolybell.dto.ProductCountDTO;
import uz.greenstar.jolybell.dto.product.ProductDTO;
import uz.greenstar.jolybell.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductRestController {
    private final ProductService productService;

    @PostMapping("/create")
    public String create(@RequestBody CreateProductDTO createProductDTO) {
        return productService.create(createProductDTO);
    }

    @PostMapping("/update/image/{productId}")
    public void updateImage(@RequestBody List<MultipartFile> files, @PathVariable("productId") String productId) {
        productService.updateImage(productId, files);
    }

    @GetMapping("/list")
    public List<ProductDTO> list() {
        return productService.getAllList();
    }

    @PostMapping("/add")
    public void add(@RequestBody ProductCountDTO dto){
        productService.add(dto);
    }
}
