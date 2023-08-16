package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.editRequest.response.EditRequestDetailResponse;
import com.connecter.digitalguiljabiback.dto.editRequest.response.EditRequestListResponse;
import com.connecter.digitalguiljabiback.dto.editRequest.response.MyEditRequestListResponse;
import com.connecter.digitalguiljabiback.dto.editRequest.EditRequestRequest;
import com.connecter.digitalguiljabiback.service.EditRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "수정요청", description = "수정요청 관련 API")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class EditRequestController {
    private final EditRequestService editRequestService;

    @Operation(summary = "수정요청하기", description = """
    [로그인 필요] 정보글 수정요청을 만듭니다.<br>
    200: 성공<br>
    404: 해당 pk의 정보글이 존재하지 않음
    """)
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

    @Operation(summary = "내가 수정요청한 글 목록조회", description = """
    [로그인 필요] 내가 수정요청한 글 목록조회합니다.<br>
    200: 성공<br>
    403: 로그인 필요
    """)
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
    @Operation(summary = "모든 수정요청 글 목록 조회", description = """
    [관리자] 모든 수정요청 글 목록을 조회합니다. 최신순으로 정렬됩니다<br>
    200: 성공<br>
    403: 권한없음
    """)
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


    @Operation(summary = "수정요청 자세히 보기", description = """
    [관리자] 수정요청 상세를 조회합니다.<br>
    200: 성공<br>
    404: 해당 pk의 수정요청이 존재하지 않음
    """)
    @GetMapping("/admin/edit-requests/{editReqPk}")
    public ResponseEntity<EditRequestDetailResponse> getEditRequestDetail(@PathVariable Long editReqPk)
    {
        return ResponseEntity.ok().body(editRequestService.getEditRequestDetail(editReqPk));
    }

    @Operation(summary = "수정요청 무시하기", description = """
    [관리자] 수정요청을 무시합니다(삭제)<br>
    200: 성공<br>
    404: 해당 pk의 수정요청이 존재하지 않음
    """)
    @DeleteMapping("/admin/edit-requests/{editReqPk}/ignore")
    public ResponseEntity ignoreEditRequest(@PathVariable Long editReqPk) {
        editRequestService.deleteEditReqiest(editReqPk);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "글쓴이에게 수정요청사항 알리기 - 게시글 숨기지 않기")
    @PostMapping("/admin/board/{boardPk}/edit-request/nothidden")
    public ResponseEntity notHideBoard(@PathVariable Long boardPk, @RequestBody EditRequestRequest request)
    {
        editRequestService.notifyEditRequest(boardPk, request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "글쓴이에게 수정요청사항 알리기 - 게시글 숨기기")
    @PostMapping("/admin/board/{boardPk}/edit-request/hidden")
    public ResponseEntity hideBoard(@PathVariable Long boardPk, @RequestBody EditRequestRequest request)
    {
        editRequestService.notifyEditRequestAndHide(boardPk, request);

        return ResponseEntity.ok().build();
    }


}
