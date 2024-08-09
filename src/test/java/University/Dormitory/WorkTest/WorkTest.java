package University.Dormitory.WorkTest;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.service.DormitoryService.DormitoryCommandService;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.service.WorkService.WorkCommandService;
import University.Dormitory.web.controller.AssistantController;
import University.Dormitory.web.controller.ScheduleController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootTest
@Slf4j
@Transactional
public class WorkTest {
    @Autowired
    UserCommandService userCommandService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WorkCommandService workCommandService;
    @Autowired
    CustomRepository customRepository;
    @Autowired
    ScheduleController scheduleController;
    @Autowired
    DormitoryCommandService dormitoryCommandService;
    @Autowired
    AssistantController assistantController;


    String schedulestartTime1 = "2023-04-18 10:30";
    String schedulestartTime2 = "2023-04-18 18:30";
    String schedulestartTime3 = "2024-04-18 10:30";
    String wantStartTime1 = "2024-04-18 10:30";
    String wantStartTime2 = "2024-04-18 15:30";
    String wantLeaveTime1 = "2024-04-18 12:30";
    String wantLeaveTime2 = "2023-04-18 16:30";

    @BeforeEach
    void beforeEach() {
//        User user1 = userCommandService.SignUp(SignUpRequestDTO.SignUpDto.builder().userId(202035505).name("권하림").authority("사감").dormitory(Dormitory.DORMITORY2).build());
//        User user2 = userCommandService.SignUp(SignUpRequestDTO.SignUpDto.builder().userId(202035506).name("조혜원").authority("사감").dormitory(Dormitory.DORMITORY2).build());
//        User user3 = userCommandService.SignUp(SignUpRequestDTO.SignUpDto.builder().userId(202035507).name("강지원").authority("조교").dormitory(Dormitory.DORMITORY2).build());
        log.info("테스트 코드 - 유저 2명 저장 완료");
    }

    @Test
    @DisplayName("근무 저장 테스트, 하루에 같은 근무자가 2번 일하는 것까지 테스트")
    void saveWorkTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime1 = LocalDateTime.parse(wantStartTime1, formatter);
        LocalDateTime endTime2 = LocalDateTime.parse(wantLeaveTime1, formatter);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035505, null, startTime1, endTime2);

        LocalDateTime startTime3 = LocalDateTime.parse(wantStartTime2, formatter);
        LocalDateTime endTime4 = LocalDateTime.parse(wantLeaveTime2, formatter);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035505, null, startTime3, endTime4);

        customRepository.findDormitoryWorkersNameByDateAndDormitory(LocalDate.from(startTime1), Dormitory.DORMITORY2);
    }

    @Test
    @DisplayName("근무 수정 테스트, wantStartTime1 -> wantStartTime2로 변경")
    void updateWorkTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime1 = LocalDateTime.parse(wantStartTime1, formatter);
        LocalDateTime endTime2 = LocalDateTime.parse(wantLeaveTime1, formatter);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035505, null, startTime1, endTime2);
//        여기까지가 저장
        LocalDateTime startTime3 = LocalDateTime.parse(wantStartTime2, formatter);
        LocalDateTime endTime4 = LocalDateTime.parse(wantLeaveTime2, formatter);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035505, startTime1, startTime3, endTime4);
//        여기까지가 수정
        customRepository.findDormitoryWorkersNameByDateAndDormitory(LocalDate.from(startTime3), Dormitory.DORMITORY2);
    }

    @Test
    @DisplayName("근무 삭제 테스트, wantStartTime1 저장 이후 삭제")
    void deleteWorkTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime1 = LocalDateTime.parse(wantStartTime1, formatter);
        LocalDateTime endTime2 = LocalDateTime.parse(wantLeaveTime1, formatter);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035505, null, startTime1, endTime2);

        workCommandService.deleteSchedule(Dormitory.DORMITORY2, LocalDate.from(startTime1));
        LocalDate deleteTime = LocalDate.of(startTime1.getYear(), startTime1.getMonth(), startTime1.getDayOfMonth());
        customRepository.findDormitoryWorkersNameByDateAndDormitory(deleteTime, Dormitory.DORMITORY2);
        //아무것도 안떠야 정상
    }

    @Test
    @DisplayName("근무 교환(exchange) 테스트, user1의 StartTime1과 user2의 StartTime2을 저장하고 둘을 교체")
    void exchangeWork() {
        LocalDateTime start1 = LocalDateTime.of(2024, 7, 30, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 7, 30, 11, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 7, 30, 16, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 7, 30, 18, 0);
        LocalDate date = LocalDate.of(2024, 7, 30);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035505, null, start1, end1);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035506, null, start2, end2);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035507, null, start2, end2);
        workCommandService.exchangeMyWorkByUserIds(202035505, 202035506, start1, start2);
        customRepository.findDormitoryWorkersNameByDateAndDormitory(date, Dormitory.DORMITORY2);
    }

    @Test
    @DisplayName("근무 주기 테스트, user1의 시간을 user2에게 준다")
    void giveWork() {
        LocalDateTime start1 = LocalDateTime.of(2024, 7, 30, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2024, 7, 30, 11, 0);
        LocalDateTime start2 = LocalDateTime.of(2024, 7, 30, 16, 0);
        LocalDateTime end2 = LocalDateTime.of(2024, 7, 30, 18, 0);
        LocalDate date = LocalDate.of(2024, 7, 30);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035505, null, start1, end1);
        workCommandService.changeOrSaveScheduleTimeByUserId(202035506, null, start2, end2);
        workCommandService.giveMyWorkByUserIds(202035505, 202035506, start1);
        customRepository.findDormitoryWorkersNameByDateAndDormitory(date, Dormitory.DORMITORY2);
    }

    @Test
    @DisplayName("우편 근무 버그")
    void postworkUser() {
        Map<Integer, List<String>> post = scheduleController.scheduleWorkTime("post", 2023, 4);
        for (Map.Entry<Integer, List<String>> integerListEntry : post.entrySet()) {
            log.info("키값:{}", integerListEntry.getKey());
            log.info("이름: {}", integerListEntry.getValue());
        }
    }

    @Test
    @DisplayName("스케줄 삭제 - 우편 근무")
    void deletePostWork() {
        String s = workCommandService.deletePostWork(LocalDate.of(2023, 4, 18));
        log.info("결과 : {}", s);
    }


    @Test
    @DisplayName("스케줄 삭제 - 조교 근무")
    void deleteWork() {
        String s = workCommandService.deleteSchedule(Dormitory.DORMITORY2, LocalDate.of(2024, 4, 20));
        log.info("결과 : {}", s);
    }

    @Test
    @DisplayName("스케줄 조회 - 조교 근무(스케즐 관리)")
    void seeWork() {
        Map<Integer, List<String>> integerListMap = scheduleController.scheduleWorkTime("2", 2024, 4);
        for (Map.Entry<Integer, List<String>> integerListEntry : integerListMap.entrySet()) {
            log.info("키값: {}", integerListEntry.getKey());
            log.info("이름: {}", integerListEntry.getValue());
        }
    }

    @Test
    @DisplayName("스케줄 조회 - 조교 근무(조교 페이지)")
    void seeWork_assitant() {
        Map<Integer, List<String>> integerListMap = scheduleController.scheduleWorkTime("2", 2024, 4);
        for (Map.Entry<Integer, List<String>> integerListEntry : integerListMap.entrySet()) {
            log.info("키값: {}", integerListEntry.getKey());
            log.info("이름: {}", integerListEntry.getValue());
        }
    }


    @Test
    @DisplayName("세부 근무자 조회")
    void seedetailWork() {
        Map<Integer, ConcurrentHashMap<String, List<LocalTime>>> integerConcurrentHashMapMap = assistantController.todayWorkersDetail(2, 4, 2024, 20);
        for (Map.Entry<Integer, ConcurrentHashMap<String, List<LocalTime>>> integerConcurrentHashMapEntry : integerConcurrentHashMapMap.entrySet()) {
            log.info("근무일:{}," + "시간: {}", integerConcurrentHashMapEntry.getKey(), integerConcurrentHashMapEntry.getValue());
        }
    }
}
