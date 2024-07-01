package team.dankookie.server4983.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.scheduler.entity.AdminReport;
import team.dankookie.server4983.scheduler.repository.AdminReportRepository;
import team.dankookie.server4983.scheduler.repository.SchedulerRepository;

import java.util.List;

import static team.dankookie.server4983.scheduler.constant.ReportType.*;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final AdminReportRepository adminReportRepository;
    private final SchedulerRepository schedulerRepository;

    //    @Scheduled(cron = "* 0/10 * * * *")
    @Transactional
    public void execute() {
        addAdminReportByNotBookArrangement();
        addAdminReportByNotDeposit();
        addAdminReportByNotBootPickup();
    }

    @Transactional
    public void addAdminReportByNotBookArrangement() {
        List<ChatRoom> result = schedulerRepository.findChatRoomAfterTime(30, 3);

        for (ChatRoom chatRoom : result) {
            if (!schedulerRepository.findByChatRoomAndReportType(chatRoom, NOT_BOOK_ARRANGEMENT).isPresent()) {
                adminReportRepository.save(AdminReport.createAdminReport(chatRoom, NOT_BOOK_ARRANGEMENT));
            }
            ;
        }
    }

    @Transactional
    public void addAdminReportByNotDeposit() {
        List<ChatRoom> result = schedulerRepository.findChatRoomAfterTime(144, 2);

        for (ChatRoom chatRoom : result) {
            if (!schedulerRepository.findByChatRoomAndReportType(chatRoom, NOT_DEPOSIT).isPresent()) {
                adminReportRepository.save(AdminReport.createAdminReport(chatRoom, NOT_DEPOSIT));
            }
        }
    }

    @Transactional
    public void addAdminReportByNotBootPickup() {
        List<ChatRoom> result = schedulerRepository.findChatRoomPreviouslyTime(30, 4);

        for (ChatRoom chatRoom : result) {
            if (!schedulerRepository.findByChatRoomAndReportType(chatRoom, NOT_BOOK_PICKUP).isPresent()) {
                adminReportRepository.save(AdminReport.createAdminReport(chatRoom, NOT_BOOK_PICKUP));
            }
        }
    }

}
