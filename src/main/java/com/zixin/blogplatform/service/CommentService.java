package com.zixin.blogplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zixin.blogplatform.entity.BlogComment;
import com.zixin.blogplatform.util.R;


public interface CommentService extends IService<BlogComment> {
    R getComments(Long id);

    R deleteComments(Long id);
}
