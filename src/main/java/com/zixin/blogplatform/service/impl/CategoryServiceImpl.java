package com.zixin.blogplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zixin.blogplatform.dao.BlogCategoryMapper;
import com.zixin.blogplatform.entity.BlogCategory;
import com.zixin.blogplatform.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<BlogCategoryMapper, BlogCategory> implements CategoryService {
}
