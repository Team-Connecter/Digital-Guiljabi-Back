package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.ImgUrlRequest;
import com.connecter.digitalguiljabiback.dto.user.InfoRequest;
import com.connecter.digitalguiljabiback.dto.user.NicknameRequest;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;

  public void changeUserNickname(Users user, NicknameRequest request) {
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    findUser.updateNickname(request.getNickname());
  }

  public void changeProfileImg(Users user, ImgUrlRequest request) {
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    findUser.updateProfileImg(request.getImgUrl());
  }

  public void changeInfo(Users user, InfoRequest request) {
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    findUser.updateInfo(request.getNickname(), request.getIntroduction(), request.getIdVms(), request.getId1365());
  }
}
