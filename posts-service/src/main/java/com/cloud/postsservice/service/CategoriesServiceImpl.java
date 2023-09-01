package com.cloud.postsservice.service;

import com.cloud.postsservice.dto.CategoryRequestDto;
import com.cloud.postsservice.entity.Category;
import com.cloud.postsservice.repository.CategoriesRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;
    public CategoriesServiceImpl(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }
    @Override
    public void addCategory(CategoryRequestDto dto) {
        if(categoriesRepository.existsByCommunityIdAndName(dto.getCommunityId(), dto.getCategory()))
            throw new EntityExistsException("Category already exists");
        var category = new Category(dto.getCommunityId(), dto.getCategory());
        categoriesRepository.save(category);
        categoriesRepository.findAll().forEach(c -> System.out.println("CATEGORIES " + c));
    }
}
