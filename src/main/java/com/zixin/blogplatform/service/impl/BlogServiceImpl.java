package com.zixin.blogplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixin.blogplatform.config.Constants;
import com.zixin.blogplatform.config.exception.BizCodeEnum;
import com.zixin.blogplatform.dao.BlogMapper;
import com.zixin.blogplatform.dao.BlogCategoryMapper;
import com.zixin.blogplatform.dao.BlogTagMapper;
import com.zixin.blogplatform.dao.BlogTagRelationMapper;
import com.zixin.blogplatform.entity.Blog;
import com.zixin.blogplatform.entity.BlogCategory;
import com.zixin.blogplatform.entity.BlogTag;
import com.zixin.blogplatform.entity.BlogTagRelation;
import com.zixin.blogplatform.service.BlogService;
import com.zixin.blogplatform.util.PageResult;
import com.zixin.blogplatform.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("blogService")
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    private final BlogTagMapper blogTagMapper;

    private final BlogTagRelationMapper relationMapper;

    private final BlogCategoryMapper blogCategoryMapper;

    private final PlatformTransactionManager transactionManager;

    @Override
    public R queryBlogById(Long id) {
        log.info("Load blog detail id={}", id);
        // 只查询未删除且已发布的博客
        Blog blog = this.getOne(new QueryWrapper<Blog>()
                .eq("blog_id", id)
                .eq("is_deleted", 0)
                .eq("blog_status", 1));
        if (blog == null) {
            return R.error(BizCodeEnum.BLOG_NOT_FOUND.getCode(), BizCodeEnum.BLOG_NOT_FOUND.getMsg());
        }

        // 增加浏览量
        if(blog.getViewed() < 999){
            blog.setViewed(blog.getViewed() + 1);
            this.updateById(blog);
        }

        return R.ok().setData(blog);
    }

    @Override
    public PageResult queryPage(Map<String, Object> params) {
        // 从 params 中解析分页参数，提供默认值
        int current = 1;
        int size = 10;
        if (params != null) {
            Object curObj = params.get("current");
            Object sizeObj = params.get("size");
            try {
                if (curObj != null) current = Integer.parseInt(curObj.toString());
            } catch (NumberFormatException ignored) {}
            try {
                if (sizeObj != null) size = Integer.parseInt(sizeObj.toString());
            } catch (NumberFormatException ignored) {}
        }

        IPage<Blog> page = this.page(
                new Page<>(current, size),
                new QueryWrapper<Blog>()
        );
        log.debug("Loaded blog page={}, size={}, total={}", current, size, page.getTotal());
        return new PageResult(page);
    }

    @Override
    public boolean updateBlog(Blog blog) {
        // 文章 ID 必须存在
        Long id = blog.getBlogId();
        if (id == null) return false;

        // 查询当前 version 和现有的 Blog
        Blog existingBlog = this.getById(id);
        if (existingBlog == null) return false; // 如果没有找到该文章，返回 false

        // 创建新的 Blog 来进行更新，只更新传入的字段
        Blog blogToUpdate = getBlogToUpdate(blog, id, existingBlog);
        // 执行更新
        boolean updated = this.updateById(blogToUpdate);
        if (!updated) {
            log.warn("文章更新失败，id={}, version={}", id, existingBlog.getVersion());
            return false;
        }
        log.info("文章更新成功，id={}", id);
        return true;
    }

    private static Blog getBlogToUpdate(Blog blog, Long id, Blog existingBlog) {
        Blog blogToUpdate = new Blog();
        blogToUpdate.setBlogId(id);
        blogToUpdate.setVersion(existingBlog.getVersion()); // 保留原来的 version 字段

        // 更新传入的字段
        if (blog.getBlogTitle() != null) blogToUpdate.setBlogTitle(blog.getBlogTitle());
        if (blog.getBlogContent() != null) blogToUpdate.setBlogContent(blog.getBlogContent());
        if (blog.getBlogStatus() != null) blogToUpdate.setBlogStatus(blog.getBlogStatus());
        if (blog.getBlogCategoryId() != null) blogToUpdate.setBlogCategoryId(blog.getBlogCategoryId());
        if (blog.getBlogTags() != null) blogToUpdate.setBlogTags(blog.getBlogTags());

        blogToUpdate.setUpdateTime(new Date());
        return blogToUpdate;
    }

    @Override
    public R queryHotBlog(Integer current) {
        Page<Blog> page = (Page<Blog>) query()
                .eq("is_deleted", 0)   // 未删除
                .eq("blog_status", 1)  // 已发布
                .orderByDesc("viewed")
                .page(new Page<>(current, Constants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return R.ok(records);
    }

    @Override
    public boolean deleteBlog(Long id) {
        boolean removed = this.removeById(id);
        log.warn("删除博客 id={}, success={}", id, removed);
        return removed;
    }

    @Override
    public R saveBlog(Blog blog) {
        // 1. 规范化并去重标签名（去掉空白、重复，保持原有顺序）
        List<String> rawTags = blog.getBlogTags();
        if (rawTags == null) {
            rawTags = Collections.emptyList();
        }
        LinkedHashSet<String> normalizedSet = rawTags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<String> tagNames = new ArrayList<>(normalizedSet);
        if (tagNames.isEmpty()) {
            log.error("保存博客失败，标签不能为空");
            return R.error();
        }

        // 2. 校验分类是否存在（如果传了 categoryId）
        Integer categoryId = blog.getBlogCategoryId();
        if (categoryId != null) {
            BlogCategory category = blogCategoryMapper.selectById(categoryId);
            if (category == null) {
                log.error("保存博客失败，分类不存在，categoryId={}", categoryId);
                return R.error(400, "分类不存在");
            }
        }

        // 3. 手动开启事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("saveBlogWithTags");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            // 3.1 保存 blog 主体
            blog.setCreateTime(new Date());
            blog.setViewed(0L);
            blog.setIsDeleted((byte) 0);

            boolean savedBlog = this.save(blog);
            if (!savedBlog || blog.getBlogId() == null) {
                throw new RuntimeException("保存博客失败");
            }
            Long blogId = blog.getBlogId();

            // 3.2 处理标签，得到 tagId 列表（用 Map 缓存避免重复查询）
            Map<String, Integer> tagIdMap = new HashMap<>();
            for (String tagName : tagNames) {
                BlogTag tag = blogTagMapper.selectByTagName(tagName);
                if (tag == null) {
                    tag = new BlogTag();
                    tag.setTagName(tagName);
                    // 这里 insertSelective 不一定回填主键，所以插完再查一次
                    blogTagMapper.insertSelective(tag);
                    tag = blogTagMapper.selectByTagName(tagName);
                    if (tag == null || tag.getTagId() == null) {
                        throw new RuntimeException("创建标签失败: " + tagName);
                    }
                }
                tagIdMap.put(tagName, tag.getTagId());
            }

            // 3.3 构造 blog_tag_relation 关系并批量插入
            List<BlogTagRelation> relations = tagIdMap.values().stream()
                    .map(tagId -> {
                        BlogTagRelation r = new BlogTagRelation();
                        r.setBlogId(blogId);
                        r.setTagId(tagId);
                        r.setCreateTime(new Date());
                        return r;
                    }).toList();

            if (!relations.isEmpty()) {
                int count = relationMapper.batchInsert(relations);
                if (count != relations.size()) {
                    throw new RuntimeException("批量插入标签关系失败");
                }
            }

            // 3.4 冗余更新 blog.blog_tags（List<String> -> 逗号字符串 由 TypeHandler 处理）
            blog.setBlogTags(tagNames);
            this.updateById(blog);

            // 3.5 提交事务
            transactionManager.commit(status);
            return R.ok();
        } catch (Exception e) {
            // 3.6 出错回滚事务
            transactionManager.rollback(status);
            log.error("保存博客及标签失败，发生异常", e);
            return R.error();
        }
    }


    public BlogServiceImpl(BlogTagMapper blogTagMapper,
                           BlogTagRelationMapper relationMapper,
                           BlogCategoryMapper blogCategoryMapper,
                           PlatformTransactionManager transactionManager) {
        this.blogTagMapper = blogTagMapper;
        this.relationMapper = relationMapper;
        this.blogCategoryMapper = blogCategoryMapper;
        this.transactionManager = transactionManager;
    }

}