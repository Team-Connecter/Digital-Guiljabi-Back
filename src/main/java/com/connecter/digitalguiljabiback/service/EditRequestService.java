package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.domain.FirebaseToken;
import com.connecter.digitalguiljabiback.domain.board.Board;
import com.connecter.digitalguiljabiback.domain.EditRequest;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.editRequest.response.*;
import com.connecter.digitalguiljabiback.dto.editRequest.EditRequestRequest;
import com.connecter.digitalguiljabiback.exception.NotFoundException;
import com.connecter.digitalguiljabiback.firebase.PushNotificationRequest;
import com.connecter.digitalguiljabiback.firebase.PushNotificationService;
import com.connecter.digitalguiljabiback.repository.BoardRepository;
import com.connecter.digitalguiljabiback.repository.EditRequestRepository;
import com.connecter.digitalguiljabiback.repository.FirebaseTokenRepository;
import com.connecter.digitalguiljabiback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EditRequestService {

    private final EditRequestRepository editRequestRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final FirebaseTokenRepository firebaseTokenRepository;
    private final PushNotificationService pushNotificationService;


    public void addRequest(Users user, Long boardPk, EditRequestRequest editRequestRequest)
    {
        Board board = boardRepository.findById(boardPk)
                .orElseThrow(() -> new NotFoundException("해당하는 pk의 board가 존재하지 않습니다"));

        EditRequest editRequest = EditRequest.makeEditRequest(user, board, editRequestRequest.getContent());
        editRequestRepository.save(editRequest);

    }

    public MyEditRequestListResponse getMyEditRequestList(Users user, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createAt").descending());

        Page<EditRequest> editRequests = editRequestRepository.findByUser(user, pageable);

        List<MyEditRequestResponse> editRequestResponses = editRequests.stream().map(editRequest ->
                MyEditRequestResponse.builder()
                        .boardPk(editRequest.getBoard().getPk())
                        .title(editRequest.getBoard().getTitle())
                        .thumbnail(editRequest.getBoard().getThumbnailUrl())
                        .createAt(editRequest.getCreateAt())
                        .likeCnt(editRequest.getBoard().getLikeCnt())
                        .bookmarkCnt(editRequest.getBoard().getBookmarkCnt())
                        .build()
        ).toList();

        return new MyEditRequestListResponse(editRequestResponses.size(), editRequestResponses);
    }

    public EditRequestListResponse getEditRequestListAll(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createAt").descending());

        Page<EditRequest> editRequests = editRequestRepository.findAll(pageable);

        List<EditRequestResponse> editRequestResponses = editRequests.stream().map(editRequest ->
                EditRequestResponse.builder()
                        .editRequestPk(editRequest.getPk())
                        .writerName(editRequest.getBoard().getUser().getUid())
                        .reqUserName(editRequest.getUser().getNickname())
                        .title(editRequest.getBoard().getTitle())
                        .createAt(editRequest.getCreateAt())
                        .build()
        ).toList();

        return new EditRequestListResponse(editRequestResponses.size(), editRequestResponses);
    }

    public EditRequestDetailResponse getEditRequestDetail(Long editReqPk)
    {
        EditRequest editRequest = editRequestRepository.findById(editReqPk)
                .orElseThrow(() -> new NotFoundException("해당하는 수정요청글을 찾을 수 없습니다."));

        EditRequestDetailResponse editRequestDetailResponse = EditRequestDetailResponse.builder()
                        .boardPk(editRequest.getBoard().getPk())
                        .title(editRequest.getBoard().getTitle())
                        .writerPk(editRequest.getBoard().getUser().getPk())
                        .writerName(editRequest.getBoard().getUser().getNickname())
                        .reqUserPk(editRequest.getUser().getPk())
                        .reqUserName(editRequest.getUser().getNickname())
                        .requestTime(editRequest.getCreateAt())
                        .content(editRequest.getContent())
                        .build();

        return editRequestDetailResponse;
    }

    public void deleteEditReqiest(Long editReqPk) {
        EditRequest editRequest = editRequestRepository.findById(editReqPk)
                .orElseThrow(() -> new NotFoundException("해당 pk의 수정요청글이 존재하지 않습니다."));

        editRequestRepository.delete(editRequest);
    }

    public void notifyEditRequest(Long boardPk, EditRequestRequest editRequestRequest) {
        Board board = boardRepository.findById(boardPk)
                .orElseThrow(() -> new NotFoundException("해당하는 pk의 board가 존재하지 않습니다"));

        Users user = userRepository.findById(board.getUser().getPk())
                .orElseThrow(() -> new NotFoundException("해당하는 pk의 user가 존재하지 않습니다"));

        board.editRequest(editRequestRequest.getContent());


        // user 의 모든 token 찾기
        List<FirebaseToken> firebaseTokens = firebaseTokenRepository.findAllByUser(user);

        // 알림 보내기
        for (FirebaseToken firebaseToken : firebaseTokens) {
            PushNotificationRequest request = new PushNotificationRequest("\"" + board.getTitle() + "\" 글 수정요청 알림", "수정요청 사항: " + request, firebaseToken.getToken());
            pushNotificationService.sendPushNotificationToToken(request);
        }



    }

    public void notifyEditRequestAndHide(Long boardPk, EditRequestRequest editRequestRequest) {
        Board board = boardRepository.findById(boardPk)
                .orElseThrow(() -> new NotFoundException("해당하는 pk의 board가 존재하지 않습니다"));

        // 게시글 숨기기
        board.hide(editRequestRequest.getContent());

        Users user = userRepository.findById(board.getUser().getPk())
                .orElseThrow(() -> new NoSuchElementException("해당하는 pk의 user가 존재하지 않습니다"));


        // user 의 모든 token 찾기
        List<FirebaseToken> firebaseTokens = firebaseTokenRepository.findAllByUser(user);

        // 알림 보내기
        for (FirebaseToken firebaseToken : firebaseTokens) {
            PushNotificationRequest request = new PushNotificationRequest("\"" + board.getTitle() + "\" 글 수정요청 알림", "수정요청 사항: " + editRequestRequest, firebaseToken.getToken());
            pushNotificationService.sendPushNotificationToToken(request);
        }
    }


}
