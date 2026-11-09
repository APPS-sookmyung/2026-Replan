package com.yuan.replan.service;

import com.yuan.replan.entity.User;
import com.yuan.replan.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class GoogleUserService extends OidcUserService {
    private final UserRepository users;
    public GoogleUserService(UserRepository users) { this.users = users; }

    @Override
    public OidcUser loadUser(OidcUserRequest request) {
        OidcUser principal = super.loadUser(request);
        saveProfile(principal);
        return principal;
    }

    public User saveProfile(OidcUser principal) {
        User user = users.findByGoogleSub(principal.getSubject())
                .orElseGet(() -> new User(principal.getSubject(), principal.getEmail(), principal.getFullName()));
        user.updateProfile(principal.getEmail(), principal.getFullName());
        return users.save(user);
    }
}
