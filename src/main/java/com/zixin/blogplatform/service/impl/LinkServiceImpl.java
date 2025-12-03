package com.zixin.blogplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixin.blogplatform.dao.BlogLinkMapper;
import com.zixin.blogplatform.entity.BlogLink;
import com.zixin.blogplatform.service.LinkService;
import com.zixin.blogplatform.util.PageQueryUtil;
import com.zixin.blogplatform.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 友链业务实现
 * 统一继承 ServiceImpl，保留原有 PageQueryUtil 方式，方便平滑迁移到 JDK17。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl extends ServiceImpl<BlogLinkMapper, BlogLink> implements LinkService {

    private final BlogLinkMapper blogLinkMapper;

    @Override
    public PageResult getBlogLinkPage(PageQueryUtil pageUtil) {
        List<BlogLink> links = blogLinkMapper.findLinkList(pageUtil);
        int total = blogLinkMapper.getTotalLinks(pageUtil);
        log.debug("Loaded link page={}, limit={}, total={}", pageUtil.getPage(), pageUtil.getLimit(), total);
        return new PageResult(links, total, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public int getTotalLinks() {
        return blogLinkMapper.getTotalLinks(null);
    }

    @Override
    public Boolean saveLink(BlogLink link) {
        boolean saved = blogLinkMapper.insertSelective(link) > 0;
        log.info("Create link name={}, success={}", link.getLinkName(), saved);
        return saved;
    }

    @Override
    public BlogLink selectById(Integer id) {
        return blogLinkMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean updateLink(BlogLink tempLink) {
        boolean updated = blogLinkMapper.updateByPrimaryKeySelective(tempLink) > 0;
        log.info("Update link id={}, success={}", tempLink.getLinkId(), updated);
        return updated;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        boolean deleted = blogLinkMapper.deleteBatch(ids) > 0;
        log.warn("Delete links ids={}, success={}", Arrays.toString(ids), deleted);
        return deleted;
    }

    @Override
    public Map<Byte, List<BlogLink>> getLinksForLinkPage() {
        // 获取所有链接数据
        List<BlogLink> links = blogLinkMapper.findLinkList(null);
        if (!CollectionUtils.isEmpty(links)) {
            // 根据 type 进行分组
            return links.stream().collect(Collectors.groupingBy(BlogLink::getLinkType));
        }
        return null;
    }
}
