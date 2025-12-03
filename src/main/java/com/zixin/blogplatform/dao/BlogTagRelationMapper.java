package com.zixin.blogplatform.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixin.blogplatform.entity.BlogTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogTagRelationMapper extends BaseMapper<BlogTagRelation> {
    List<Long> selectDistinctTagIds(Integer[] tagIds);



    int batchInsert(@Param("relationList") List<BlogTagRelation> blogTagRelationList);

}