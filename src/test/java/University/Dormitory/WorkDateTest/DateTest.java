package University.Dormitory.WorkDateTest;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.User;
import University.Dormitory.domain.WorkDate;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.service.WorkService.WorkCommandService;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@SpringBootTest
@Slf4j
@Transactional
public class DateTest {
    WorkDate userWorkDate1;
    WorkDate userWorkDate2;
    WorkDate userWorkDate3;
    User user1;
    User user2;
    @Autowired
    private WorkDateRepository workDateRepository;
    @Autowired
    private CustomRepository customRepository;
    @Autowired
    private UserCommandService userCommandService;
    @Autowired
    private WorkCommandService workCommandService;

    @BeforeEach
    void beforeach() {
//        유저1 가입
        SignUpRequestDTO.SignUpDto SignUpDto = new SignUpRequestDTO.SignUpDto();
        SignUpDto.setPassword("123");
        SignUpDto.setDormitory(Dormitory.BUILDING2);
        SignUpDto.setUserId(202035505);
        SignUpDto.setAuthority("사감");
        SignUpDto.setName("권하림");
        log.info("DTO 정보: [pw:1234, 권한:사감, 권하림, BUILDING2] 입력 완료");
        user1 = userCommandService.SignUp(SignUpDto);
//        유저2 가입
        SignUpRequestDTO.SignUpDto SignUpDto2 = new SignUpRequestDTO.SignUpDto();
        SignUpDto2.setPassword("1234");
        SignUpDto2.setDormitory(Dormitory.BUILDING2);
        SignUpDto2.setUserId(202035506);
        SignUpDto2.setAuthority("조교");
        SignUpDto2.setName("림하권");
        log.info("DTO 정보: [pw:1234, 권한:조교, 림하권, BUILDING2] 입력 완료");
        user2 = userCommandService.SignUp(SignUpDto2);

//        유저1의 스케줄상 근무시간, 실제 일한시간 입력
        userWorkDate1 = WorkDate.builder()
                .user(user1)
                .actualStartTime(LocalDateTime.now()) //실제 출근시간 => 현재시간
                .actualLeaveTime(LocalDateTime.now().plusHours(1)) //실제 퇴근시간 => 현재시간+1시간
                .scheduledStartTime(LocalDateTime.now().plusHours(2))//스케줄 출근시간 => 현재시간+2시간
                .scheduledLeaveTime(LocalDateTime.now().plusHours(3))//스케줄 퇴근시간 => 현재시간+3시간
                .build();

//        유저2의 스케줄상 근무시간, 실제 일한시간 입력
        userWorkDate2 = WorkDate.builder()
                .user(user2)
                .actualStartTime(LocalDateTime.now().plusDays(0)) //실제 출근시간 => 현재시간
                .actualLeaveTime(LocalDateTime.now().plusDays(0).plusHours(1))//실제 퇴근시간 => 현재시간+1시간 
                .scheduledStartTime(LocalDateTime.now().plusDays(0).plusHours(2))//스케줄 출근시간 => 현재시간+2시간
                .scheduledLeaveTime(LocalDateTime.now().plusDays(0).plusHours(3))//스케줄 퇴근시간 => 현재시간 + 3시간
                .build();
//        유저1의 스케줄상 근무시간, 실제 일한시간 입력2
        userWorkDate3 = WorkDate.builder()
                .user(user1)
                .actualStartTime(LocalDateTime.now()) //실제 출근시간 => 현재시간
                .actualLeaveTime(LocalDateTime.now().plusDays(1).plusHours(1)) // 실제 퇴근시간 => 현재시간+1시간+1일
                .scheduledStartTime(LocalDateTime.now().plusDays(1).plusHours(2))//스케줄 출근시간 => 현재시간+2시간+1일
                .scheduledLeaveTime(LocalDateTime.now().plusDays(1).plusHours(3))//스케줄 퇴근시간 => 현재시간+3시간+1일
                .build();
        workDateRepository.save(userWorkDate1);
        workDateRepository.save(userWorkDate2);
        workDateRepository.save(userWorkDate3);
    }


    @Test
    @DisplayName("권하림의 오늘 스케줄 보기.")
    public void saveWorkDate() {
        workDateRepository.save(userWorkDate1);
        Map<String, CustomRepository.WorkTime> userNameDormitoryWorkersByDate = customRepository.findDormitoryWorkersNameByDate(LocalDate.now(), Dormitory.BUILDING2);
        CustomRepository.WorkTime userWorkTime = userNameDormitoryWorkersByDate.get("권하림");

        log.info("{}의 일하는 날짜 스케줄상 근무시작(현시간+2시간):{}," +
                        " 스케줄상 근무종료(현시간+3시간):{}",
                user1.getName(),
                userWorkTime.getStartTime(),
                userWorkTime.getLeaveTime()

        );
    }

    @Test
    @DisplayName("오늘 2긱의 근무자 스케줄 보기")
    public void seeTodayWorkers() {
        Map<String, CustomRepository.WorkTime> dormitoryWorkersNameByDate = customRepository.findDormitoryWorkersNameByDate(LocalDate.now(), Dormitory.BUILDING2);
        log.info("오늘 2긱 근무자는");
        for (String s : dormitoryWorkersNameByDate.keySet()) {
            log.info("{} 이고 시간은 {}부터 {}까지", s, dormitoryWorkersNameByDate.get(s).getStartTime(), dormitoryWorkersNameByDate.get(s).getLeaveTime());
        }
        log.info("입니다.");
    }

    @Test
    @DisplayName("나의 이번달 일한 날 세기")
    public void workDays() {
        int i = customRepository.countWorkDays(user1.getUserId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        log.info("일한 날의 수는 {} 입니다. ->2개가 나와야함.", i);
    }

    @Test
    @DisplayName("나의 이번달 일한 시간 세기")
    public void workHours() {
        int i = customRepository.countWorkHours(user1.getUserId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        log.info("일한 시간은 {} 시간입니다.", i);
    }


    @Test
    @DisplayName("나의 스케줄 근무시간 바꾸기 예외 상황")
    public void changeScheduleTime() {
        String s = workCommandService.changeScheduleTimeByUserId(user1.getUserId(),
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        log.info("{}", s);
    }

    @Test
    @DisplayName("나의 실제 출/퇴근 시간 바꾸기")
    public void changeAcutalTime() {

    }

    @Test
    @DisplayName("근무교체")
    public void exchangeWorkTime() {

    }


    @Test
    @DisplayName("근무맞교대")
    public void giveMyWorkTime() {

    }
}
