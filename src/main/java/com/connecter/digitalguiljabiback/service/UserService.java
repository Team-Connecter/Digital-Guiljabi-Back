package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.LoginRequest;
import com.connecter.digitalguiljabiback.dto.user.LoginResponse;
import com.connecter.digitalguiljabiback.dto.user.RegisterRequest;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



//    public void register(RegisterRequest dto) throws UsernameDuplicatedException {
//
//        Users findByLoginIdUser = userRepository.findFirstByLoginId(dto.getId())
//            .orElseGet(() -> null);
//
//        if (findByLoginIdUser != null)
//            throw new UsernameDuplicatedException("이미 존재하는 아이디입니다.");
//
//        Users user = Users.makeUsers(
//          dto.getId(),
//          passwordEncoder.encode(dto.getPassword()),
//          OauthType.KAKAO
//        );
//
//        userRepository.save(user);
//    }

//    public LoginResponse authenticate(LoginRequest dto) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        dto.getId(),
//                        dto.getPassword()
//                )
//        );
//
//        Users user = userRepository.findFirstByLoginId(dto.getId())
//          .orElseThrow(() -> new NoSuchElementException());
//
//        String jwtToken = jwtService.generateAccessToken(user);
//        return new LoginResponse(jwtToken);
//    }

//    //중복 아이디 확인
//    @Transactional(readOnly = true)
//    public boolean isExistId(String id) {
//        Users user = userRepository.findById(id)
//          .orElseGet(() -> null);
//
//        return (user == null)? false : true;
//    }

}
