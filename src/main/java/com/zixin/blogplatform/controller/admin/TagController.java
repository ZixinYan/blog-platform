package com.zixin.blogplatform.controller.admin;

import com.zixin.blogplatform.service.TagService;
import com.zixin.blogplatform.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
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
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public String tagPage(HttpServletRequest request) {
        log.info("Navigate to tag page");
        request.setAttribute("path", "tags");
        return "admin/tag";
    }

    @GetMapping("/tags/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return R.error();
        }
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        log.info("Fetch tag list page={}, limit={}", page, limit);
        return R.ok().setData(tagService.getBlogTagPage(page, limit));
    }


    @PostMapping("/tags/save")
    @ResponseBody
    public R save(@RequestParam("tagName") String tagName) {
        if (!StringUtils.hasText(tagName)) {
            return R.error();
        }
        log.info("Create tag name={}", tagName);
        if (tagService.saveTag(tagName)) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/tags/delete")
    @ResponseBody
    public R delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return R.error();
        }
        boolean deleted = tagService.deleteBatch(ids);
        log.warn("Delete tags ids={}, success={}", Arrays.toString(ids), deleted);
        return deleted ? R.ok() : R.error();
    }


}
