package com.zixin.blogplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixin.blogplatform.controller.vo.CommentVo;
import com.zixin.blogplatform.dao.BlogCommentMapper;
import com.zixin.blogplatform.entity.BlogComment;
import com.zixin.blogplatform.service.CommentService;
import com.zixin.blogplatform.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements CommentService {

    @Override
    public R getComments(Long id) {
        // 1. 根据 BlogId 查询所有未删除的评论
        List<BlogComment> comments = this.list(new LambdaQueryWrapper<BlogComment>()
                .eq(BlogComment::getBlogId, id)
                .eq(BlogComment::getIsDeleted, (byte) 0));

        // 2. 找出所有一级评论（parentId = 0 且 commentStatus = 0）
        List<CommentVo> commentVos = comments.stream()
                .filter(comment -> comment.getCommentStatus() != null
                        && comment.getCommentStatus() == 0L
                        && (comment.getParentCommentId() == null || comment.getParentCommentId() == 0L))
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 3. 绑定子评论
        for (CommentVo commentVo : commentVos) {
            commentVo.setReplies(getReplies(commentVo.getId(), comments));
        }
        log.debug("Loaded {} root comments for blogId={}", commentVos.size(), id);
        return R.ok(commentVos);
    }


    @Override
    public R deleteComments(Long id) {
        boolean success = this.removeById(id);
        log.warn("Delete comment id={}, success={}", id, success);
        return success ? R.ok() : R.error();
    }

    private List<CommentVo> getReplies(Long parentId, List<BlogComment> comments) {
        return comments.stream()
                .filter(comment -> comment.getParentCommentId() != null
                        && comment.getParentCommentId().equals(parentId)
                        && comment.getIsDeleted() != null
                        && comment.getIsDeleted() == 0)
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private CommentVo convertToVO(BlogComment entity) {
        CommentVo vo = new CommentVo();
        vo.setId(entity.getCommentId());
        vo.setContent(entity.getCommentBody());
        vo.setBlogId(entity.getBlogId());
        vo.setParentId(entity.getParentCommentId());
        vo.setCreateTime(entity.getCommentCreateTime());
        return vo;
    }
}
