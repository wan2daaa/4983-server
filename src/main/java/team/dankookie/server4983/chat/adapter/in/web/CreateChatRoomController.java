package team.dankookie.server4983.chat.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRoomRequest;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRoomResponse;
import team.dankookie.server4983.chat.application.port.in.CreateChatRoomCommand;
import team.dankookie.server4983.chat.application.port.in.CreateChatRoomUseCase;
import team.dankookie.server4983.jwt.dto.AccessToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-room")
public class CreateChatRoomController {

    private final CreateChatRoomUseCase createChatRoomUseCase;

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody ChatRoomRequest chatRoomRequest,
            AccessToken accessToken
    ) {
        CreateChatRoomCommand command = CreateChatRoomCommand.of(chatRoomRequest.getUsedBookId(), accessToken.nickname());

        ChatRoomResponse response = createChatRoomUseCase.createChatRoom(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
