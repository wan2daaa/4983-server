package team.dankookie.server4983.book.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.book.dto.LockerResponse;
import team.dankookie.server4983.book.dto.LockerSaveRequest;
import team.dankookie.server4983.book.service.LockerService;
import team.dankookie.server4983.common.dto.BaseMessageResponse;
import team.dankookie.server4983.jwt.dto.AccessToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/locker")
public class LockerController {

    private final LockerService lockerService;

    @GetMapping("/chat-room-available-date")
    public ResponseEntity<List<LockerResponse>> getExistsLockerWhenChatRoomAvailableDate(@RequestParam Long chatRoomId) {

        return ResponseEntity.ok(lockerService.getExistsLockerWhenChatRoomAvailableDate(chatRoomId));
    }

    @PostMapping
    public ResponseEntity<BaseMessageResponse> saveLocker(@RequestBody LockerSaveRequest lockerSaveRequest, AccessToken accessToken) {

        boolean isSaved = lockerService.saveLocker(lockerSaveRequest, accessToken);

        if (isSaved) {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    BaseMessageResponse.of("정상적으로 저장되었습니다.")
            );
        }

        return ResponseEntity.badRequest().body(BaseMessageResponse.of("서버의 문제로 저장에 실패하였습니다."));
    }

}
