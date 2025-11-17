package com.work.pamper.service;

import com.work.pamper.entity.Account;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
        Object Register(Account user);

        Object Login(Account user);
}
