package com.zixin.blogplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zixin.blogplatform.entity.BlogLink;
import com.zixin.blogplatform.util.PageQueryUtil;
import com.zixin.blogplatform.util.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 友链业务接口
 * 统一继承 MyBatis-Plus 的 IService，保留原有自定义方法以兼容旧代码。
 */
public interface LinkService extends IService<BlogLink> {

    /**
     * 查询友链的分页数据
     */
    PageResult getBlogLinkPage(PageQueryUtil pageUtil);

    int getTotalLinks();

    Boolean saveLink(BlogLink link);

    BlogLink selectById(Integer id);

    Boolean updateLink(BlogLink tempLink);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 返回友链页面所需的所有数据
     */
    Map<Byte, List<BlogLink>> getLinksForLinkPage();
}
