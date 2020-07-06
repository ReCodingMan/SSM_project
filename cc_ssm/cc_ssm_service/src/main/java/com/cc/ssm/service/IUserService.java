package com.cc.ssm.service;

import com.cc.ssm.domain.UserInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {

    /**
     * 查询所有用户
     * @return
     * @throws Exception
     */
    List<UserInfo> findAll() throws Exception;
}
