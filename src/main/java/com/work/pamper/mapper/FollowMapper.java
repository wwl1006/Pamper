package com.work.pamper.mapper;

import com.work.pamper.entity.Follow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowMapper {
    int follow(Follow follow);

    int checkFollow(Follow follow);
}
