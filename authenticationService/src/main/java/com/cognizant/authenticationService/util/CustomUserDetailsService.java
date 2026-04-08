package com.cognizant.authenticationService.util;

import com.cognizant.authenticationService.dto.UserDTO;

import com.cognizant.authenticationService.feignclient.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {



    @Autowired
    private UserClient userFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user =userFeignClient.getUserByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getName(),
                user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority("ROLE_"+user.getRole()))
        );
    }
}