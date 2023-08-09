package com.connecter.digitalguiljabiback.repository.board;

import com.connecter.digitalguiljabiback.domain.board.VersionDiff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionDiffRepository extends JpaRepository<VersionDiff, Long> {
}
