package com.zixin.blogplatform.controller.admin;

import com.zixin.blogplatform.entity.BlogComment;
import com.zixin.blogplatform.service.CommentService;
import com.zixin.blogplatform.util.PatternUtil;
import com.zixin.blogplatform.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blog/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/list")
    public R getComments(@RequestParam Long id) {
        log.info("Fetch comments for blogId={}", id);
        return  commentService.getComments(id);
    }

    @PutMapping("/save")
    public R saveComments(@RequestBody BlogComment blogComment) {
        log.info("Save comment for blogId={}, parentId={}", blogComment.getBlogId(), blogComment.getParentCommentId());
        // 1. 决定是否是一级评论：parentId 为空或 0 视为一级
        Long parentId = blogComment.getParentCommentId();
        if (parentId == null || parentId == 0L) {
            blogComment.setParentCommentId(0L);
            blogComment.setCommentStatus(0L);
        } else {
            blogComment.setCommentStatus(1L);
        }
        // 2. 评论校验：邮箱必填且合法；网址可选，但如果传了必须是合法 URL
        if (!PatternUtil.isEmail(blogComment.getEmail())
                || (blogComment.getWebsiteUrl() != null
                && blogComment.getWebsiteUrl().length() > 0
                && !PatternUtil.isURL(blogComment.getWebsiteUrl()))) {
            return R.error();
        }
        // 3. 保存评论
        if(commentService.save(blogComment)){
            return R.ok();
        }else{
            return R.error();
        }
    }

    @DeleteMapping("/delete")
    public R deleteComments(@RequestParam Long id) {
        log.warn("Delete comment id={}", id);
        return commentService.deleteComments(id);
    }

}
