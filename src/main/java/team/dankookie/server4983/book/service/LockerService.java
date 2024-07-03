package team.dankookie.server4983.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.book.dto.LockerResponse;
import team.dankookie.server4983.book.dto.LockerSaveRequest;
import team.dankookie.server4983.book.repository.locker.LockerRepository;
import team.dankookie.server4983.chat.adapter.out.persistence.ChatRoomRepository;
import team.dankookie.server4983.chat.application.service.ChatLogicHandler;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.jwt.dto.AccessToken;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LockerService {

    private final ChatRoomRepository chatRoomRepository;
    private final LockerRepository lockerRepository;
    private final ChatLogicHandler chatLogicHandler;

    public List<LockerResponse> getExistsLockerWhenChatRoomAvailableDate(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));

        LocalDate tradeDate = LocalDate.from(
                chatRoom.getUsedBook().getTradeAvailableDatetime());

        List<LockerResponse> responseList = lockerRepository.findByTradeDateBetweenAndIsExistsTrue(
                tradeDate.minusDays(1), tradeDate);

        addEmptyLockerToLockerResponseList(responseList);

        sortListLockerNumberAsc(responseList);

        return responseList;
    }

    private static void addEmptyLockerToLockerResponseList(List<LockerResponse> responseList) {
        for (int i = 1; i <= 32; i++) {
            int finalI = i;
            if (responseList.stream().noneMatch(response -> response.getLockerNumber() == finalI)) {
                responseList.add(LockerResponse.of(i, false));
            }
        }
    }

    private void sortListLockerNumberAsc(List<LockerResponse> responseList) {
        responseList.sort(
                (response1, response2) ->
                        Integer.compare(response1.getLockerNumber(), response2.getLockerNumber()));
    }

    @Transactional
    public boolean saveLocker(LockerSaveRequest lockerSaveRequest, AccessToken accessToken) {
        ChatRoom chatRoom = chatRoomRepository.findById(lockerSaveRequest.chatRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방이 존재하지 않습니다."));

        boolean isUserInChatRoom = chatRoomRepository.existsByBuyer_NicknameOrSeller_NicknameAndChatRoomId(
                accessToken.nickname(),
                accessToken.nickname(),
                lockerSaveRequest.chatRoomId());

        if (!isUserInChatRoom) {
            throw new RuntimeException("채팅방에 참여하지 않은 사용자입니다.");
        }

        Locker locker = lockerSaveRequest.toEntity(
                chatRoom,
                chatRoom.getUsedBook().getTradeAvailableDatetime().toLocalDate()
        );
        lockerRepository.save(locker);

        chatLogicHandler.chatLogic(lockerSaveRequest.toChatRequest());

        return true;
    }
}
