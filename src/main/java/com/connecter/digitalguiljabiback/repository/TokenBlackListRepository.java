package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.JwtTokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenBlackListRepository extends JpaRepository<JwtTokenBlackList, Long> {
  @Query(value = "SELECT 1 FROM jwt_token_blacklist WHERE token = ?1 AND expired_at > now() LIMIT 1", nativeQuery = true)
  Optional<Integer> isExistToken(String token);
}
