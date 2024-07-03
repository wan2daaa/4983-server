package team.dankookie.server4983.chat.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.application.port.in.FindChattingUseCase;
import team.dankookie.server4983.jwt.dto.AccessToken;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-room")
public class FindChattingController {

    private final FindChattingUseCase findChattingUseCase;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChattingData(@PathVariable long chatRoomId, AccessToken accessToken) {
        List<ChatMessageResponse> chattingMessageList = findChattingUseCase.getChattingData(chatRoomId, accessToken);

        return ResponseEntity.ok().body(chattingMessageList);
    }
}
