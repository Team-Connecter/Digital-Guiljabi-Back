package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.ImgUrlRequest;
import com.connecter.digitalguiljabiback.dto.user.InfoRequest;
import com.connecter.digitalguiljabiback.dto.user.NicknameRequest;
import com.connecter.digitalguiljabiback.dto.user.response.AllUserResponse;
import com.connecter.digitalguiljabiback.dto.user.response.UsersInfoResponse;
import com.connecter.digitalguiljabiback.dto.user.response.UsersResponse;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;

  public void changeUserNickname(Users user, NicknameRequest request) throws NoSuchElementException, UsernameDuplicatedException {
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    Users findByNickname = userRepository.findByNickname(request.getNickname())
      .orElseGet(null);

    if (findByNickname != null)
      throw new UsernameDuplicatedException("같은 닉네임의 유저가 이미 존재합니다");

    findUser.updateNickname(request.getNickname());
  }

  public void changeProfileImg(Users user, ImgUrlRequest request) throws NoSuchElementException {
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    findUser.updateProfileImg(request.getImgUrl());
  }

  public void changeInfo(Users user, InfoRequest request) throws NoSuchElementException {
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 사용자가 없습니다"));

    findUser.updateInfo(request.getNickname(), request.getIntroduction(), request.getIdVms(), request.getId1365());
  }

  public UsersInfoResponse getUserInfo(Users user) {

    return UsersInfoResponse.builder()
      .nickname(user.getNickname())
      .introduction(user.getIntroduction())
      .joinAt(user.getCreateAt())
      .idVms(user.getIdvms())
      .id21365(user.getId1365())
      .imgUrl(user.getProfileUrl())
      .build();
  }

  public AllUserResponse getAll(int pageSize, int page) {
    Pageable pageable = PageRequest.of(page-1 ,pageSize, Sort.Direction.DESC, "createAt");

    List<Users> userList = userRepository.findAll(pageable).getContent();

    List<UsersResponse> responseList = UsersResponse.convertList(userList);

    return new AllUserResponse(responseList.size(), responseList);
  }

  public void delete(Users user) throws NoSuchElementException {
    Users findUser = userRepository.findById(user.getPk())
      .orElseThrow(() -> new NoSuchElementException("해당하는 user가 없습니다"));

    userRepository.delete(findUser);
  }
}
