package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.RefreshToken;
import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  List<RefreshToken> findByUser(Users user);
}
