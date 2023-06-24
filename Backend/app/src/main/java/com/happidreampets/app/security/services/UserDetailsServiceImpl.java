package com.happidreampets.app.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.happidreampets.app.database.crud.UserCRUD;
import com.happidreampets.app.database.model.User;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserCRUD userCRUD;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userCRUD.getUserBasedOnEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with Email: " + email);
        }

        return UserDetailsImpl.build(user);
    }
}
