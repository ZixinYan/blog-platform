package com.zixin.blogplatform.controller.admin;

import com.zixin.blogplatform.entity.BlogLink;
import com.zixin.blogplatform.service.LinkService;
import com.zixin.blogplatform.util.PageQueryUtil;
import com.zixin.blogplatform.util.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class LinkController {

    private final LinkService linkService;

    @GetMapping("/links")
    public String linkPage(HttpServletRequest request) {
        log.info("Navigate to links page");
        request.setAttribute("path", "links");
        return "admin/link";
    }

    @GetMapping("/links/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return R.error();
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        log.info("Fetch link list page={}, limit={}", pageUtil.getPage(), pageUtil.getLimit());
        return R.ok().setData(linkService.getBlogLinkPage(pageUtil));
    }

    /**
     * 友链添加
     */
    @RequestMapping(value = "/links/save", method = RequestMethod.POST)
    @ResponseBody
    public R save(@RequestParam("linkType") Integer linkType,
                       @RequestParam("linkName") String linkName,
                       @RequestParam("linkUrl") String linkUrl,
                       @RequestParam("linkRank") Integer linkRank,
                       @RequestParam("linkDescription") String linkDescription) {
        if (checkLinkParam(linkType, linkName, linkUrl, linkRank, linkDescription)) return R.error();
        BlogLink link = new BlogLink();
        link.setLinkType(linkType.byteValue());
        link.setLinkRank(linkRank);
        link.setLinkName(linkName);
        link.setLinkUrl(linkUrl);
        link.setLinkDescription(linkDescription);
        boolean saved = linkService.saveLink(link);
        log.info("Create link name={}, success={}", linkName, saved);
        return saved ? R.ok() : R.error();
    }

    /**
     * 详情
     */
    @GetMapping("/links/info/{id}")
    @ResponseBody
    public R info(@PathVariable("id") Integer id) {
        BlogLink link = linkService.selectById(id);
        return R.ok().setData(link);
    }

    /**
     * 友链修改
     */
    @RequestMapping(value = "/links/update", method = RequestMethod.POST)
    @ResponseBody
    public R update(@RequestParam("linkId") Integer linkId,
                       @RequestParam("linkType") Integer linkType,
                       @RequestParam("linkName") String linkName,
                       @RequestParam("linkUrl") String linkUrl,
                       @RequestParam("linkRank") Integer linkRank,
                       @RequestParam("linkDescription") String linkDescription) {
        BlogLink tempLink = linkService.selectById(linkId);
        if (tempLink == null) {
            return R.error();
        }
        if (checkLinkParam(linkType, linkName, linkUrl, linkRank, linkDescription)) return R.error();
        tempLink.setLinkType(linkType.byteValue());
        tempLink.setLinkRank(linkRank);
        tempLink.setLinkName(linkName);
        tempLink.setLinkUrl(linkUrl);
        tempLink.setLinkDescription(linkDescription);
        boolean updated = linkService.updateLink(tempLink);
        log.info("Update link id={}, success={}", linkId, updated);
        return updated ? R.ok() : R.error();
    }

    private boolean checkLinkParam(@RequestParam("linkType") Integer linkType, @RequestParam("linkName") String linkName, @RequestParam("linkUrl") String linkUrl, @RequestParam("linkRank") Integer linkRank, @RequestParam("linkDescription") String linkDescription) {
        return linkType == null || linkType < 0 || linkRank == null || linkRank < 0 || !StringUtils.hasText(linkName) || !StringUtils.hasText(linkName) || !StringUtils.hasText(linkUrl) || !StringUtils.hasText(linkDescription);
    }

    /**
     * 友链删除
     */
    @RequestMapping(value = "/links/delete", method = RequestMethod.POST)
    @ResponseBody
    public R delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return R.error();
        }
        boolean deleted = linkService.deleteBatch(ids);
        log.warn("Delete links ids={}, success={}", Arrays.toString(ids), deleted);
        return deleted ? R.ok() : R.error();
    }

}
