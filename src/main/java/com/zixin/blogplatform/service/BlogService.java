package com.zixin.blogplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zixin.blogplatform.entity.Blog;
import com.zixin.blogplatform.util.PageResult;
import com.zixin.blogplatform.util.R;


import java.util.Map;


public interface BlogService extends IService<Blog> {

    R queryBlogById(Long id);

    PageResult queryPage(Map<String, Object> params);

    boolean updateBlog(Blog blog);

    R queryHotBlog(Integer current);

    boolean deleteBlog(Long id);

    R saveBlog(Blog blog);
}
