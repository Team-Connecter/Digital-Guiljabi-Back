package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.EditRequest;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.domain.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EditRequestRepository extends JpaRepository<EditRequest, Long> {

    Page<EditRequest> findByUser(Users user, Pageable pageable);

    List<EditRequest> findByBoard(Board board);
}
