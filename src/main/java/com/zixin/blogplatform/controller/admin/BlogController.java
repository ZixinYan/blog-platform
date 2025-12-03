package com.zixin.blogplatform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zixin.blogplatform.config.Constants;
import com.zixin.blogplatform.entity.Blog;
import com.zixin.blogplatform.service.BlogService;
import com.zixin.blogplatform.util.MyBlogUtils;
import com.zixin.blogplatform.util.PageResult;
import com.zixin.blogplatform.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
@Slf4j
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/hot")
    public R queryHotBlog(@RequestParam(value = "current", defaultValue = "0") Integer current) {
        log.info("Query hot blogs, current page: {}", current);
        return blogService.queryHotBlog(current);
    }

    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        log.info("Query blog list with params: {}", params);
        PageResult page = blogService.queryPage(params);
        return R.ok(page);
    }

    @GetMapping("/info")
    public R info(@RequestParam("id") Long id){
        log.info("Query blog detail, id={}", id);
        Blog Blog = blogService.getById(id);
        return R.ok(Blog);
    }

    @PostMapping("/save")
    public R save(@RequestBody Blog blog) {
        log.info("Create blog, title={}", blog.getBlogTitle());
        blog.setCreateTime(new Date());
        blog.setBlogContent(MyBlogUtils.cleanString(blog.getBlogContent()));
        return blogService.saveBlog(blog);
    }



    @PutMapping("/update")
    public R update(@RequestBody Blog blog) {
        log.info("Update blog, id={}", blog.getBlogId());
        // 更新数据库中的信息
        blog.setBlogContent(MyBlogUtils.cleanString(blog.getBlogContent()));
        if(blogService.updateBlog(blog)) {
            return R.ok();
        }else {
            return R.error();
        }
    }


    @DeleteMapping("/delete")
    @Transactional
    public R delete(@RequestParam("id") Long id) {
        log.warn("Delete blog, id={}", id);
        if(blogService.deleteBlog(id)){
            return R.ok();
        }else{
            return R.error();
        }

    }
}
