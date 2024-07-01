package team.dankookie.server4983.chat.controller;

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
import team.dankookie.server4983.chat.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.dto.ChatRequest;
import team.dankookie.server4983.chat.dto.ChatRoomRequest;
import team.dankookie.server4983.chat.dto.ChatRoomResponse;
import team.dankookie.server4983.chat.dto.ChatStopRequest;
import team.dankookie.server4983.chat.service.ChatService;
import team.dankookie.server4983.jwt.dto.AccessToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-room")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody ChatRoomRequest chatRoomRequest,
            AccessToken accessToken
    ) throws AccountException {
        ChatRoomResponse result = chatService.createChatRoom(chatRoomRequest, accessToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChattingData(@PathVariable long chatRoomId,
                                                                     AccessToken accessToken) {
        List<ChatMessageResponse> chattingMessageList = chatService.getChattingData(chatRoomId,
                accessToken);

        return ResponseEntity.ok().body(chattingMessageList);
    }

    @PostMapping("/interact")
    public ResponseEntity<List<ChatMessageResponse>> requestChatBot(
            @RequestBody ChatRequest chatRequest,
            AccessToken accessToken) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.chatRequestHandler(chatRequest, accessToken));
    }

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

//  @GetMapping("/chat-room/{chatRoom}")
//  public ResponseEntity getChatRoom(@PathVariable long chatRoom, HttpServletRequest request) {
//    ChatRoomResponse result = chatService.getChatRoom(chatRoom, request);
//
//    return ResponseEntity.status(HttpStatus.OK).body(result);
//  }
}
