package com.work.pamper.mapper;

import com.work.pamper.entity.ActionLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActionLogMapper {
    int insert(ActionLog log);
}