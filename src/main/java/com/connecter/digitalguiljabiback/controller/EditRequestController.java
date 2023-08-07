package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.editRequest.response.EditRequestDetailResponse;
import com.connecter.digitalguiljabiback.dto.editRequest.response.EditRequestListResponse;
import com.connecter.digitalguiljabiback.dto.editRequest.response.MyEditRequestListResponse;
import com.connecter.digitalguiljabiback.dto.editRequest.EditRequestRequest;
import com.connecter.digitalguiljabiback.service.EditRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "EditRequestController", description = "수정요청 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class EditRequestController {
    private final EditRequestService editRequestService;

    // 수정요청하기
    @Secured("USER")
    @PostMapping("/boards/{boardPk}/edit-requests")
    public ResponseEntity editRequest(
            @AuthenticationPrincipal Users user,
            @PathVariable Long boardPk,
            @RequestBody EditRequestRequest editRequest
            )
    {
        editRequestService.addRequest(user, boardPk, editRequest);

        return ResponseEntity.ok().build();
    }

    // 내가 수정요청한 글 목록조회
    @Secured("USER")
    @GetMapping("/edit-requests/my")
    public ResponseEntity<MyEditRequestListResponse> getMyEditRequest(
            // 최신순
            @AuthenticationPrincipal Users user,
            @RequestParam(value = "size",required = false, defaultValue = "10") int size,
            @RequestParam(value = "page",required = false, defaultValue = "0") int page
    )
    {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(editRequestService.getMyEditRequestList(user, pageable));

    }

    // ADMIN -------------------------------------
    // 모든 수정요청 글 목록 조회 (최신순)
    @GetMapping("/admin/edit-requests")
    public ResponseEntity<EditRequestListResponse> getEditRequest(
            // 최신순
            @RequestParam(value = "size",required = false, defaultValue = "10") int size,
            @RequestParam(value = "page",required = false, defaultValue = "0") int page
    )
    {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(editRequestService.getEditRequestListAll(pageable));

    }


    // 수정요청 자세히 보기
    @GetMapping("/admin/edit-requests/{editReqPk}")
    public ResponseEntity<EditRequestDetailResponse> getEditRequestDetail(@PathVariable Long editReqPk)
    {
        return ResponseEntity.ok().body(editRequestService.getEditRequestDetail(editReqPk));
    }

    // 수정요청 무시하기
    @DeleteMapping("/admin/edit-requests/{editReqPk}/ignore")
    public ResponseEntity ignoreEditRequest(@PathVariable Long editReqPk) {
        editRequestService.deleteEditReqiest(editReqPk);

        return ResponseEntity.ok().build();
    }

    // 글쓴이에게 수정요청사항 알리기 - 게시글 숨기지 않기
    @PostMapping("/admin/board/{boardPk}/edit-request/nothidden")
    public ResponseEntity notHideBoard(@PathVariable Long boardPk, @RequestBody String reason)
    {
        editRequestService.notifyEditRequest(boardPk, reason);

        return ResponseEntity.ok().build();
    }

    // 글쓴이에게 수정요청사항 알리기 - 게시글 숨기기
    @PostMapping("/admin/board/{boardPk}/edit-request/hidden")
    public ResponseEntity hideBoard(@PathVariable Long boardPk, @RequestBody String reason)
    {
        editRequestService.notifyEditRequestAndHide(boardPk, reason);

        return ResponseEntity.ok().build();
    }


}
