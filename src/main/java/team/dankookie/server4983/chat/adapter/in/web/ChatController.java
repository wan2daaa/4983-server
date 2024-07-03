package team.dankookie.server4983.chat.adapter.in.web;

import java.util.List;
import javax.security.auth.login.AccountException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRoomRequest;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRoomResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatStopRequest;
import team.dankookie.server4983.chat.application.service.ChatService;
import team.dankookie.server4983.jwt.dto.AccessToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-room")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/not-read/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getNotReadChattingData(
            @PathVariable Long chatRoomId,
            AccessToken accessToken) {
        List<ChatMessageResponse> responseList = chatService.getNotReadChattingData(chatRoomId,
                accessToken);

        return ResponseEntity.ok().body(responseList);
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopTrade(@RequestBody ChatStopRequest chatStopRequest, AccessToken accessToken) {
        chatService.stopTrade(chatStopRequest, accessToken);

        return ResponseEntity.ok().build();
    }

}
