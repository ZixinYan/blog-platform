package com.zixin.blogplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zixin.blogplatform.entity.BlogTag;
import com.zixin.blogplatform.util.PageResult;

import java.util.List;

/**
 * 标签业务接口
 * 统一继承 MyBatis-Plus 的 IService，保留原有自定义方法以兼容旧代码。
 */
public interface TagService extends IService<BlogTag> {

    /**
     * 查询标签的分页数据（使用 MyBatis-Plus 分页能力）
     */
    PageResult getBlogTagPage(int page, int limit);

    int getTotalTags();

    Boolean saveTag(String tagName);

    Boolean deleteBatch(Integer[] ids);
}
