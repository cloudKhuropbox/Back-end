package com.khu.cloudcomputing.khuropbox.auth.service;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity create(final UserEntity userEntity) {
        if(userEntity == null || userEntity.getUsername()==null) {
            throw new RuntimeException("userEntity is null or empty");
        }
        final String username = userEntity.getUsername();
        if(userRepository.existsByUsername(username)) {
            log.warn("username {} already exists", username);
            throw new RuntimeException("username already exists");
        }
        return userRepository.save(userEntity);
    }

    public UserEntity getByCredentials(final String username, final String password, final PasswordEncoder passwordEncoder) {
        final UserEntity originalUser = userRepository.findByUsername(username);

        if(originalUser != null&&
        passwordEncoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }
}
