package com.zixin.blogplatform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixin.blogplatform.entity.BlogComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogCommentMapper extends BaseMapper<BlogComment> {
}
