package com.zixin.blogplatform.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixin.blogplatform.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
}
