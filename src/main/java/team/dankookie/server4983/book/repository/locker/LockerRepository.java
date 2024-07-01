package team.dankookie.server4983.book.repository.locker;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.chat.domain.ChatRoom;

public interface LockerRepository extends JpaRepository<Locker, Long>, LockerRepositoryCustom {

    Optional<Locker> findByChatRoom(ChatRoom chatRoom);

    Optional<Locker> findByLockerNumber(Integer lockerNumber);

    @Query("select l from Locker l where l.lockerNumber = :lockerNumber and l.isExists = true")
    Locker findByLockerNumberAndIsExists(@Param("lockerNumber") Integer lockerNumber);


}
