package com.work.pamper.service;

import com.work.pamper.entity.Follow;
import org.springframework.stereotype.Service;

@Service
public interface FollowService {
    Object follow(Follow follow);
}