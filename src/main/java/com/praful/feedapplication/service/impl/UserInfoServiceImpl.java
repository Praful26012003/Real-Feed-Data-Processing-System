package com.praful.feedapplication.service.impl;

import com.praful.feedapplication.configuration.UserInfoConfig;
import com.praful.feedapplication.dao.UserDAO;
import com.praful.feedapplication.protos.UserResponseDTO;
import com.praful.feedapplication.protos.UserResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserResponseEntity userInfo = userDAO.loadUserByUsername(username);
        UserResponseDTO userResponse = UserResponseDTO.newBuilder()
            .setUsername(userInfo.getUsername())
            .setPassword(userInfo.getPassword()).build();
        return new UserInfoConfig(userResponse);

    }
}
