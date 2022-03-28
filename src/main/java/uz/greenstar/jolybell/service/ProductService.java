package uz.greenstar.jolybell.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.dto.ProductDTO;
import uz.greenstar.jolybell.entity.CategoryEntity;
import uz.greenstar.jolybell.entity.ProductEntity;
import uz.greenstar.jolybell.exception.CategoryNotFoundException;
import uz.greenstar.jolybell.repository.CategoryRepository;
import uz.greenstar.jolybell.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public void create(ProductDTO productDTO) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productDTO, productEntity);

        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(productDTO.getCategoryId());
        if (optionalCategory.isEmpty())
            throw new CategoryNotFoundException("Category not found!");
        productEntity.setCategory(optionalCategory.get());
        productRepository.save(productEntity);
    }

    public List<ProductDTO> getList() {
        return productRepository.findAll().stream().map(productEntity -> {
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(productEntity, productDTO);
            productDTO.setCategoryId(productEntity.getCategory().getId());
            return productDTO;
        }).collect(Collectors.toList());
    }

    public List<ProductDTO> getListByName(String url) {
        List<ProductEntity> productEntityList = productRepository.findAllByCategoryUrl(url);
        Optional<CategoryEntity> optionalCategory = categoryRepository.findByUrl(url);
        if (optionalCategory.isEmpty())
            throw new CategoryNotFoundException("Category not found!");
        return optionalCategory.get().getProductList().stream().map(productEntity -> {
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(productEntity, productDTO);
            productDTO.setCategoryId(productEntity.getCategory().getId());
            return productDTO;
        }).collect(Collectors.toList());
    }

    public ProductDTO getById(String id) {
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(productRepository.findById(id).get(), productDTO);
//        productDTO.setSize(new ArrayList<>());
        return productDTO;
    }
}