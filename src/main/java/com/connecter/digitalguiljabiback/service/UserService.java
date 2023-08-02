package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

//    //중복 아이디 확인
//    @Transactional(readOnly = true)
//    public boolean isExistId(String id) {
//        Users user = userRepository.findById(id)
//          .orElseGet(() -> null);
//
//        return (user == null)? false : true;
//    }

}
