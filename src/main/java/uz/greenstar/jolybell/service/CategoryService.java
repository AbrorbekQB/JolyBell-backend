package uz.greenstar.jolybell.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.api.filterForm.FilterResponse;
import uz.greenstar.jolybell.dto.category.CategoryDTO;
import uz.greenstar.jolybell.dto.category.EditCategoryDTO;
import uz.greenstar.jolybell.entity.CategoryEntity;
import uz.greenstar.jolybell.exception.CategoryNotFoundException;
import uz.greenstar.jolybell.exception.ItemNotFoundException;
import uz.greenstar.jolybell.repository.CategoryRepository;
import uz.greenstar.jolybell.repository.spec.CategoryListByAdminSpecification;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public void create(CategoryDTO dto) {
        log.info("Category creating start. Category name: " + dto.getName());
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(UUID.randomUUID().toString());
        categoryEntity.setName(dto.getName());
        categoryEntity.setUrl(urlAlgorithm(dto.getName().toLowerCase()));
        categoryRepository.save(categoryEntity);
        log.info("Category created. Category name: " + dto.getName());
    }

    public CategoryDTO get(String id) {
        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(id);
        if (!optionalCategory.isPresent())
            optionalCategory = categoryRepository.findByUrl(id);
        if (!optionalCategory.isPresent())
            throw new CategoryNotFoundException("Does not exist category with this id. id=" + id);
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(optionalCategory.get(), dto);
        return dto;
    }

    public void update(CategoryDTO dto) {
        Optional<CategoryEntity> optionalCategory = categoryRepository.findById(dto.getId());
        if (!optionalCategory.isPresent())
            throw new CategoryNotFoundException("Does not exist category with this id. id=" + dto.getId());
        if (StringUtils.hasText(dto.getName()))
            optionalCategory.get().setName(dto.getName());
        if (StringUtils.hasText(dto.getUrl()))
            optionalCategory.get().setUrl(dto.getUrl().toLowerCase());
        categoryRepository.save(optionalCategory.get());
    }

    public List<CategoryDTO> getList() {
        return categoryRepository.findAllByActiveTrueOrderByNameDesc().stream().map(categoryEntity -> {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(categoryEntity.getId());
            categoryDTO.setName(categoryEntity.getName());
            categoryDTO.setUrl(categoryEntity.getUrl());
            return categoryDTO;
        }).collect(Collectors.toList());
    }

    public FilterResponse getListByAdmin(FilterRequest request) {

        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getLength());

        FilterResponse filterResponse = new FilterResponse();

        Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(CategoryListByAdminSpecification.getFilteredPayment(request),
                pageRequest);

        List<CategoryDTO> categoryDTOList = categoryEntityPage.stream().map(categoryEntity -> {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(categoryEntity.getId());
            categoryDTO.setName(categoryEntity.getName());
            categoryDTO.setUrl(categoryEntity.getUrl());
            categoryDTO.setActive(categoryEntity.getActive());
            if (Objects.nonNull(categoryEntity.getUser()))
                categoryDTO.setUsername(categoryEntity.getUser().getUsername());
            if (Objects.nonNull(categoryEntity.getCreateDateTime()))
                categoryDTO.setCreateDate(categoryEntity.getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return categoryDTO;
        }).collect(Collectors.toList());

        filterResponse.setCount(categoryDTOList.size());
        filterResponse.setData(categoryDTOList);
        filterResponse.setTotalCount(categoryEntityPage.getTotalElements());
        filterResponse.setPages(request.getPage());
        return filterResponse;
    }

    public void changeActiveByAdmin(String id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty())
            throw new ItemNotFoundException("Can not found category!");

        categoryOptional.get().setActive(!categoryOptional.get().getActive());
        categoryRepository.save(categoryOptional.get());
    }

    public void edit(EditCategoryDTO dto) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(dto.getId());
        if (categoryOptional.isEmpty())
            throw new ItemNotFoundException("Can not found category!");

        categoryOptional.get().setName(dto.getName().toUpperCase());
        categoryOptional.get().setUrl(urlAlgorithm(dto.getName().toLowerCase()));
        categoryRepository.save(categoryOptional.get());
    }

    private String urlAlgorithm(String name) {
        return name;
    }
}
