package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.FirebaseToken;
import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseToken, Long> {

    List<FirebaseToken> findAllByUser(Users user);
}
