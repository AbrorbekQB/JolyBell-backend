package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.api.filterForm.FilterResponse;
import uz.greenstar.jolybell.dto.product.CreateProductDTO;
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

    @PostMapping("/list")
    public FilterResponse list(@RequestBody FilterRequest request) {
        return productService.getAllList(request);
    }

    @PostMapping("/add")
    public void add(@RequestBody ProductCountDTO dto){
        productService.add(dto);
    }

    @GetMapping("/change/active/{id}")
    public void changeActive(@PathVariable String id) {
        productService.changeActiveByAdmin(id);
    }

    @GetMapping("/get/{id}")
    public ProductDTO get(@PathVariable("id") String productId){
        return productService.getByAdmin(productId);
    }
}
