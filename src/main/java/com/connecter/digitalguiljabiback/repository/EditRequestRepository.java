package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.EditRequest;
import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditRequestRepository extends JpaRepository<EditRequest, Long> {

    Page<EditRequest> findByUser(Users user, Pageable pageable);

}
