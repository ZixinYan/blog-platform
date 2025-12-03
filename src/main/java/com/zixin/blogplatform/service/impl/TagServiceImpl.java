package com.zixin.blogplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixin.blogplatform.dao.BlogTagMapper;
import com.zixin.blogplatform.dao.BlogTagRelationMapper;
import com.zixin.blogplatform.entity.BlogTag;
import com.zixin.blogplatform.service.TagService;
import com.zixin.blogplatform.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 标签业务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements TagService {

    private final BlogTagMapper blogTagMapper;
    private final BlogTagRelationMapper relationMapper;

    @Override
    public PageResult getBlogTagPage(int page, int limit) {
        Page<BlogTag> mpPage = this.page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<BlogTag>()
                        .eq(BlogTag::getIsDeleted, (byte) 0)
                        .orderByDesc(BlogTag::getTagId)
        );
        log.debug("Loaded tag page {}, size {}, total {}", page, limit, mpPage.getTotal());
        return new PageResult(mpPage);
    }

    @Override
    public int getTotalTags() {
        return (int) this.count(new LambdaQueryWrapper<BlogTag>()
                .eq(BlogTag::getIsDeleted, (byte) 0));
    }

    @Override
    public Boolean saveTag(String tagName) {
        BlogTag temp = blogTagMapper.selectByTagName(tagName);
        if (temp == null) {
            BlogTag blogTag = new BlogTag();
            blogTag.setTagName(tagName);
            boolean saved = blogTagMapper.insertSelective(blogTag) > 0;
            log.info("Create tag name={}, success={}", tagName, saved);
            return saved;
        }
        return false;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        // 已存在关联关系不删除
        List<Long> relations = relationMapper.selectDistinctTagIds(ids);
        if (!CollectionUtils.isEmpty(relations)) {
            log.warn("Cannot delete tags {}, referenced by relations {}", Arrays.toString(ids), relations);
            return false;
        }
        // 删除 tag
        boolean deleted = blogTagMapper.deleteBatch(ids) > 0;
        log.warn("Delete tags {}, success={}", Arrays.toString(ids), deleted);
        return deleted;
    }


}
