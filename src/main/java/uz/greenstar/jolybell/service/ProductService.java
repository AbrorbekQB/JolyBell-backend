package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.api.filterForm.FilterResponse;
import uz.greenstar.jolybell.dto.DescriptionItem;
import uz.greenstar.jolybell.dto.ImageItem;
import uz.greenstar.jolybell.dto.ProductCountDTO;
import uz.greenstar.jolybell.dto.product.CreateProductDTO;
import uz.greenstar.jolybell.dto.product.ProductDTO;
import uz.greenstar.jolybell.dto.product.ProductEditDTO;
import uz.greenstar.jolybell.entity.*;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.enums.ReservedProductStatus;
import uz.greenstar.jolybell.exception.CategoryNotFoundException;
import uz.greenstar.jolybell.exception.ItemNotFoundException;
import uz.greenstar.jolybell.exception.ProductNotFoundException;
import uz.greenstar.jolybell.repository.*;
import uz.greenstar.jolybell.repository.spec.ProductListByAdminSpecification;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final Environment environment;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCountRepository productCountRepository;
    private final ReservedProductRepository reservedProductRepository;

    List<String> sizeList = Arrays.asList("xs", "x", "m", "l", "xl", "2xl", "3xl");

    public String create(CreateProductDTO createProductDTO) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(createProductDTO, productEntity);

        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(createProductDTO.getCategoryId());
        if (optionalCategory.isEmpty())
            throw new CategoryNotFoundException("Category not found!");
        productEntity.setCategory(optionalCategory.get());
        productEntity.setDescriptionItems(createProductDTO.getDescriptionItems().stream().map(s -> {
            DescriptionItem descriptionItem = new DescriptionItem();
            descriptionItem.setText(s);
            return descriptionItem;
        }).collect(Collectors.toList()));
        productEntity.setWithSize(createProductDTO.isWithSize());
        productRepository.save(productEntity);

        if (createProductDTO.isWithSize()) {
            sizeList.forEach(size -> {
                ProductCountEntity productCount = new ProductCountEntity();
                productCount.setSize(size);
                productCount.setProduct(productEntity);
                productCountRepository.save(productCount);
            });
        } else {
            ProductCountEntity productCount = new ProductCountEntity();
            productCount.setProduct(productEntity);
            productCountRepository.save(productCount);
        }

        return productEntity.getId();
    }

    public List<ProductDTO> getList(String categoryName) {

        List<ProductEntity> productEntityList = productRepository.findAllByCategoryUrl(categoryName);
        return productEntityList.stream().filter(product -> {
            List<ProductCountEntity> productCountEntityList = productCountRepository.findAllByProduct(product);
            productCountEntityList.forEach(productCountEntity -> {
                product.setCount(product.getCount() + productCountEntity.getCount());
            });
            if (product.getCount() > 0 && product.getActive())
                return true;
            return false;
        }).map(productEntity -> {
            ProductDTO productDTO = new ProductDTO();

            productDTO.setCost(productEntity.getCost());
            productDTO.setName(productEntity.getName());
            productDTO.setId(productEntity.getId());
            productDTO.setImageItems(productEntity.getImageItems());

            return productDTO;
        }).collect(Collectors.toList());
    }

//    public List<ProductDTO> getListByName(String url) {
//        List<ProductEntity> productEntityList = productRepository.findAllByCategoryUrl(url);
//        Optional<CategoryEntity> optionalCategory = categoryRepository.findByUrl(url);
//        if (optionalCategory.isEmpty())
//            throw new CategoryNotFoundException("Category not found!");
//        return optionalCategory.get().getProductList().stream().map(productEntity -> {
//            ProductDTO productDTO = new ProductDTO();
//            BeanUtils.copyProperties(productEntity, productDTO);
//            productDTO.setCategoryId(productEntity.getCategory().getId());
//            return productDTO;
//        }).collect(Collectors.toList());
//    }

    public ProductDTO getById(String id) {
        ProductDTO productDTO = new ProductDTO();
        Optional<ProductEntity> productEntityOptional = productRepository.findById(id);
        ProductEntity productEntity = productEntityOptional.get();

        productDTO.setId(productEntity.getId());
        productDTO.setAdvice(productEntity.getAdvice());
        productDTO.setImageItems(productEntity.getImageItems());
        productDTO.setCost(productEntity.getCost());
        productDTO.setName(productEntity.getName());
        productDTO.setDescriptionItems(productEntity.getDescriptionItems());

        List<ProductCountEntity> productCountEntityList = productCountRepository.findAllByProduct(productEntityOptional.get());
        productDTO.setProductCountList(productCountEntityList.stream().filter(productCountEntity -> {
            if (productCountEntity.getCount() > 0)
                return true;
            return false;
        }).map(productCountEntity -> {
            ProductCountDTO productCountDTO = new ProductCountDTO();
            BeanUtils.copyProperties(productCountEntity, productCountDTO);
            return productCountDTO;
        }).collect(Collectors.toList()));

        productDTO.setProductCountList(sortProductCountList(productDTO.getProductCountList()));

        return productDTO;
    }

    private List<ProductCountDTO> sortProductCountList(List<ProductCountDTO> productCountList) {
        Map<String, ProductCountDTO> productCountDTOMap = productCountList.stream().
                collect(Collectors.toMap(ProductCountDTO::getSize, dto -> dto));
        List<ProductCountDTO> productCountDTOS = new ArrayList<>();
        sizeList.forEach(s -> {
            if (productCountDTOMap.containsKey(s)) {
                productCountDTOS.add(productCountDTOMap.get(s));
            }
        });
        return productCountDTOS;
    }

    public void updateImage(String productId, List<MultipartFile> files) {
        Optional<ProductEntity> productOptional = productRepository.findById(productId);
        Set<String> imagesList = new HashSet<>();
        if (productOptional.isEmpty())
            throw new ProductNotFoundException("Can not found product!");
        ProductEntity productEntity = productOptional.get();
        if (productEntity.getImageItems().isEmpty()) {
            String datePath = "assets/img/product/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/";
            File file = new File(environment.getProperty("image.source.base-url") + datePath);
            if (!file.exists())
                file.mkdirs();
            files.forEach(file1 -> {
                String image = datePath + UUID.randomUUID() + "." + Objects.requireNonNull(file1.getContentType()).split("/")[1];
                try (FileOutputStream fos = new FileOutputStream(environment.getProperty("image.source.base-url") + image)) {
                    imagesList.add(image);
                    fos.write(file1.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        productEntity.getImageItems().addAll(imagesList.stream().map(s -> {
            ImageItem imageItem = new ImageItem();
            imageItem.setUrl(s);
            return imageItem;
        }).collect(Collectors.toList()));
        productRepository.save(productEntity);
    }

    public FilterResponse getAllList(FilterRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getLength());

        FilterResponse filterResponse = new FilterResponse();

        Page<ProductEntity> productEntityPage = productRepository.findAll(
                ProductListByAdminSpecification.getFilteredPayment(request),
                pageRequest);
        List<ProductDTO> productDTOList = productEntityPage.stream().map(productEntity -> {
            ProductDTO productDTO = new ProductDTO();
            BeanUtils.copyProperties(productEntity, productDTO);
            productDTO.setCategoryName(productEntity.getCategory().getName().toUpperCase());
            productDTO.setCreateDate(productEntity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            sortProductCount(productDTO, productEntity);

            return productDTO;
        }).collect(Collectors.toList());

        filterResponse.setData(productDTOList);
        filterResponse.setTotalCount(productEntityPage.getTotalElements());
        filterResponse.setPages(request.getPage());
        return filterResponse;
    }

    public void add(ProductCountDTO dto) {
        Optional<ProductCountEntity> productCountEntityOptional = productCountRepository.findById(dto.getId());
        if (productCountEntityOptional.isEmpty())
            throw new ItemNotFoundException("Can not found ProductCount");
        ProductCountEntity productCount = productCountEntityOptional.get();
        productCount.setCount(productCount.getCount() + Math.abs(dto.getCount()));
        productCount.setLastUpdateDate(LocalDateTime.now());
        productCountRepository.save(productCount);
    }

    public void changeActiveByAdmin(String id) {
        Optional<ProductEntity> productEntityOptional = productRepository.findById(id);
        if (productEntityOptional.isEmpty())
            throw new ItemNotFoundException("Can not found category!");

        productEntityOptional.get().setActive(!productEntityOptional.get().getActive());
        productEntityOptional.get().setLastUpdate(LocalDateTime.now());
        productRepository.save(productEntityOptional.get());
    }

    public ProductDTO getByAdmin(String productId) {
        Optional<ProductEntity> productEntityOptional = productRepository.findById(productId);
        if (productEntityOptional.isEmpty())
            throw new ItemNotFoundException("Can not found product!");

        ProductEntity productEntity = productEntityOptional.get();
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(productEntity, productDTO);
        productDTO.setCategoryName(productEntity.getCategory().getName().toUpperCase());
        productDTO.setCreateDate(productEntity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        sortProductCount(productDTO, productEntity);
        return productDTO;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long reservingProduct(String productCountId, Long count, String orderId) {
        Long reservedCount = 0L;
        Optional<ProductCountEntity> productCountEntityOptional = productCountRepository.findById(productCountId);
        if (productCountEntityOptional.isEmpty() || productCountEntityOptional.get().getCount().equals(0L))
            return reservedCount;

        ProductCountEntity productCount = productCountEntityOptional.get();
        if (productCount.getCount() >= count) {
            productCount.setCount(productCount.getCount() - count);
            productCountRepository.save(productCount);

            ReservedProductEntity reservedProductEntity = new ReservedProductEntity();
            reservedProductEntity.setProductCountId(productCount.getId());
            reservedProductEntity.setCount(count);
            reservedProductEntity.setSize(productCount.getSize());
            reservedProductEntity.setOrderId(orderId);

            reservedProductRepository.save(reservedProductEntity);
            return count;
        }
        reservedCount = productCount.getCount();

        productCount.setCount(productCount.getCount() - reservedCount);
        productCountRepository.save(productCount);

        ReservedProductEntity reservedProductEntity = new ReservedProductEntity();
        reservedProductEntity.setProductCountId(productCount.getId());
        reservedProductEntity.setCount(reservedCount);
        reservedProductEntity.setSize(productCount.getSize());
        reservedProductEntity.setOrderId(orderId);

        reservedProductRepository.save(reservedProductEntity);
        return reservedCount;
    }

    private void sortProductCount(ProductDTO productDTO, ProductEntity productEntity) {
        List<ProductCountEntity> productCountEntityList = productCountRepository.findAllByProduct(productEntity);

        Map<String, ProductCountEntity> sorting = new HashMap<>();
        productCountEntityList.forEach(productCountEntity -> {
            productDTO.setCount(productDTO.getCount() + productCountEntity.getCount());
            sorting.put(productCountEntity.getSize(), productCountEntity);
        });

        if (sorting.containsKey("all")) {
            ProductCountDTO productCountDTO = new ProductCountDTO();
            BeanUtils.copyProperties(sorting.get("all"), productCountDTO);
            productDTO.getProductCountList().add(productCountDTO);
            return;
        }

        sizeList.forEach(size -> {
            if (sorting.containsKey(size)) {
                ProductCountDTO productCountDTO = new ProductCountDTO();
                BeanUtils.copyProperties(sorting.get(size), productCountDTO);
                productDTO.getProductCountList().add(productCountDTO);
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void returnReservedProduct(List<ReservedProductEntity> reservedProductEntityList) {
        if (!reservedProductEntityList.isEmpty())
            log.info("returning start. count = " + reservedProductEntityList.size() + " time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        reservedProductEntityList.forEach(reservedProductEntity -> {
            Optional<ProductCountEntity> productCountEntityOptional = productCountRepository.findById(reservedProductEntity.getProductCountId());
            if (productCountEntityOptional.isPresent()) {
                productCountEntityOptional.get().setCount(productCountEntityOptional.get().getCount() + reservedProductEntity.getCount());
                productCountEntityOptional.get().setLastUpdateDate(LocalDateTime.now());
                productCountRepository.save(productCountEntityOptional.get());
            }
            reservedProductEntity.setStatus(ReservedProductStatus.RETURNED);
            reservedProductEntity.setLastUpdateDate(LocalDateTime.now());
            reservedProductRepository.save(reservedProductEntity);

            Optional<OrderEntity> optionalOrder = orderRepository.findByIdAndStatusNotIn(reservedProductEntity.getOrderId(), Arrays.asList(OrderStatus.PENDING, OrderStatus.CANCELED, OrderStatus.CANCELED_BY_EXPIRED));
            if (optionalOrder.isPresent()) {
                optionalOrder.get().setStatus(OrderStatus.CANCELED_BY_EXPIRED);
                orderRepository.save(optionalOrder.get());
            }
        });
    }

    public void edit(ProductEditDTO dto) {
        Optional<ProductEntity> productEntityOptional = productRepository.findById(dto.getId());
        if (productEntityOptional.isEmpty())
            throw new BadCredentialsException("Product not found");

        ProductEntity productEntity = productEntityOptional.get();
        productEntity.setName(dto.getName());
        productEntity.setCost(dto.getCost());
        productEntity.setAdvice(dto.getAdvice());
        productEntity.setImageItems(dto.getImageItems());
        productEntity.setDescriptionItems(dto.getDescriptionItems().stream().map(s -> {
            DescriptionItem descriptionItem = new DescriptionItem();
            descriptionItem.setText(s);
            return descriptionItem;
        }).collect(Collectors.toList()));
        productEntity.setLastUpdate(LocalDateTime.now());

        productRepository.save(productEntity);
    }
}