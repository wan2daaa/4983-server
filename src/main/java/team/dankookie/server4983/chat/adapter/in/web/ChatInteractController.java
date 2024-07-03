package team.dankookie.server4983.chat.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.application.port.in.ChatInteractUseCase;
import team.dankookie.server4983.jwt.dto.AccessToken;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-room")
public class ChatInteractController {

    private final ChatInteractUseCase chatInteractUseCase;

    @PostMapping("/interact")
    public ResponseEntity<List<ChatMessageResponse>> requestChatBot(
            @RequestBody ChatRequest chatRequest,
            AccessToken accessToken
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chatInteractUseCase.interactUserChat(chatRequest, accessToken.nickname()));
    }
}
