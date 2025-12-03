package com.zixin.blogplatform.controller.admin;


import com.zixin.blogplatform.entity.BlogCategory;
import com.zixin.blogplatform.service.CategoryService;
import com.zixin.blogplatform.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public R getCategoryList() {
        log.info("Fetch category list");
        List<BlogCategory> list = categoryService.list();
        return R.ok(list);
    }
    @GetMapping("/info")
    public R getCategoryInfo(Long id) {
        log.info("Fetch category info id={}", id);
        BlogCategory category = categoryService.getById(id);
        return R.ok(category);
    }

    @PostMapping("/save")
    public R saveCategory(@RequestBody BlogCategory category) {
        log.info("Create category name={}", category.getCategoryName());
        categoryService.save(category);
        return R.ok();
    }

    @PutMapping("/update")
    public R updateCategory(@RequestBody BlogCategory category) {
        log.info("Update category id={}", category.getCategoryId());
        categoryService.updateById(category);
        return R.ok();
    }

    @DeleteMapping("/delete")
    public R deleteCategory(Long id) {
        log.warn("Delete category id={}", id);
        categoryService.removeById(id);
        return R.ok();
    }
}
