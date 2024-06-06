package com.khu.cloudcomputing.khuropbox.auth.controller;

import com.khu.cloudcomputing.khuropbox.auth.dto.ResponseDTO;
import com.khu.cloudcomputing.khuropbox.auth.dto.UserDTO;
import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import com.khu.cloudcomputing.khuropbox.auth.security.TokenProvider;
import com.khu.cloudcomputing.khuropbox.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        try{
            if(userDTO==null||userDTO.getPassword()==null){
                throw new RuntimeException("Invalid password value");
            }
            //request를 사용해 저장할 user 만들기
            UserEntity user = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();
            //service를 사용해 repository에 user 저장
            UserEntity registeredUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                            .build();
            return ResponseEntity.ok(responseUserDTO);
        } catch (Exception e){
            //user information은 항상 하나이므로 리스트로 마들어야하는데 ResponseDTO
            //를 사용하지 않고 그냥 UsrDTO리턴
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        UserEntity user = userService.getByCredentials(
                userDTO.getUsername(),
                userDTO.getPassword(),
                passwordEncoder
        );

        if(user!=null){
            final String token = tokenProvider.create(user);//token 생성
            final UserDTO responseUserDTO = UserDTO.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok(responseUserDTO);
        } else {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder().error("Invalid username or password").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
