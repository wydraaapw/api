package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.CategoryRequest;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceInUseException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.Category;
import org.example.api.repository.CategoryRepository;
import org.example.api.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DishRepository dishRepository;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Category create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())){
            throw new ResourceAlreadyExistsException("Kategoria " + request.name() + " już istnieje.");
        }

        return categoryRepository.save(Category.builder().name(request.name()).build());
    }

    public void delete(Long id){
        if (categoryRepository.findById(id).isEmpty()){
            throw new ResourceNotFoundException("Kategoria z podanym id nie istnieje.");
        }

        if (dishRepository.existsByCategoryId(id)){
            throw new ResourceInUseException("Kategoria posiada przypisane dania, usuń je zanim usuniesz tą kategorię z menu.");
        }

        categoryRepository.deleteById(id);
    }
}
