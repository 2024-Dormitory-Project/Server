package University.Dormitory.web.controller;

import University.Dormitory.Converter.DormitoryConverter;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.WorkScheduleChange;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.exception.Handler.WrongPathRequestException;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.security.JwtTokenProvider;
import University.Dormitory.service.DateService.DateCommandService;
import University.Dormitory.service.DormitoryService.DormitoryCommandService;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.service.WorkService.WorkCommandService;
import University.Dormitory.web.dto.MainPageDTO.MainRequestDTO;
import University.Dormitory.web.dto.MainPageDTO.MainResponseDTO;
import University.Dormitory.web.dto.WorkDTO.WorkRequestDTO;
import University.Dormitory.web.dto.WorkDTO.WorkResposneDTO;
import University.Dormitory.web.dto.WorkDateDTO.WorkDateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/assistant")
public class AssistantController {
    private final WorkCommandService workCommandService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DormitoryCommandService dormitoryCommandService;
    private final UserCommandService userCommandService;
    private final DateCommandService dateCommandService;


    @PatchMapping("/givework/{type}")
    WorkResposneDTO.workResult giveWork(HttpServletRequest request,
                                        @PathVariable("type") String type, @RequestBody WorkRequestDTO.giveWorkDto time) {
        String token = request.getHeader("Authorization");
        long userId1 = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));
        if (type.equals("dormitory")) {
            LocalDateTime changeTime = LocalDateTime.of(
                    Integer.parseInt(time.getYear()), Integer.parseInt(time.getMonth()),
                    Integer.parseInt(time.getDay()), Integer.parseInt(time.getHour()), Integer.parseInt(time.getMin()));
            String acceptor = time.getUserName();
            Optional<Long> acceptorUserId = userCommandService.findUserIdByName(acceptor);
            if (acceptorUserId.isPresent()) {
                long userId2 = acceptorUserId.get();
                String s = workCommandService.giveMyWorkByUserIds(userId1, userId2, changeTime);
                return WorkResposneDTO.workResult.builder()
                        .message(s)
                        .isSuccess(true)
                        .build();
            } else {
                throw new UserNotFoundException("해당 학번의 학생을 찾을 수 없습니다");
            }
        } else if (type.equals("post")) {
            String s = workCommandService.givePostWorkByUserId(userId1, userCommandService.findUserIdByName(time.getUserName()).orElseThrow(
                            () -> new UserNotFoundException("해당 학번의 조교가 존재하지 않습니다")
                    ),
                    LocalDate.of(Integer.parseInt(time.getYear()), Integer.parseInt(time.getMonth()), Integer.parseInt(time.getDay())));
            return WorkResposneDTO.workResult.builder()
                    .message(s)
                    .isSuccess(true)
                    .build();
        } else {
            throw new WrongPathRequestException("해당 경로는 존재하지 않습니다", type);
        }
    }

    @PatchMapping("/exchangework/{type}")
    WorkResposneDTO.workResult exchangeWork(HttpServletRequest request, @PathVariable("type") String type, @RequestBody WorkRequestDTO.exchangeWorkDto exchange) {
        String token = request.getHeader("Authorization");
        long userId1 = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));
        if (type.equals("dormitory")) {
            LocalDateTime time1 = LocalDateTime.of(
                    exchange.getYear1(), exchange.getMonth1(), exchange.getDay1(), exchange.getHour1(), exchange.getMin1()
            );
            LocalDateTime time2 = LocalDateTime.of(
                    exchange.getYear2(), exchange.getMonth2(), exchange.getDay2(), exchange.getHour2(), exchange.getMin2()
            );
            Optional<Long> id2 = userCommandService.findUserIdByName(exchange.getUserName2());
            if (id2.isPresent()) {
                long userId2 = id2.get();
                String s = workCommandService.exchangeMyWorkByUserIds(userId1, userId2, time1, time2);
                return WorkResposneDTO.workResult.builder().message(s).isSuccess(true).build();
            } else {
                throw new UserNotFoundException("해당 학번을 찾을 수 없습니다.");
            }
        } else if (type.equals("post")) {
            String s = workCommandService.exchangePostWorkByUserId(userId1,
                    LocalDate.of(
                            exchange.getYear1(), exchange.getMonth1(), exchange.getDay1()),
                    userCommandService.findUserIdByName(exchange.getUserName2()).orElseThrow(
                            () -> new UserNotFoundException("해당 이름의 조교가 존재하지 않습니다")
                    ),
                    LocalDate.of(
                            exchange.getYear2(), exchange.getMonth2(), exchange.getDay2()
                    ));
            return WorkResposneDTO.workResult.builder().message(s).isSuccess(true).build();
        } else {
            throw new WrongPathRequestException("인 경로는 존재하지 않습니다", type);
        }
    }

    @GetMapping("/schedule/{type}")
    @Operation(
            summary = "달력 스케줄 조회",
            description = "/schedule/scheduleworktime/{type}과 똑같이 작동함" +
                    "같은 이름이 있어도 반환가능하고 스케줄 시작시간에 따라 정렬한 결과를 return함." +
                    "한마디로 그냥 날짜-이름 그대로 표시하면 된다는 뜻, 시간 정렬은 이미 함"
    )
    public Map<Integer, List<String>> scheduleWorkTime(@PathVariable("type") String type,
                                                       @RequestParam("year") int year, @RequestParam("month") int month) {
        if (type.equals("1") || type.equals("2") || type.equals("3")) { //기숙사 조회인 경우
            int dormitoryNum = Integer.parseInt(type);
            Dormitory dormitory = DormitoryConverter.toDormitory(dormitoryNum);
            Map<Integer, List<String>> result = new ConcurrentHashMap<>();
            for (int i = 1; i <= LocalDate.of(year, month, 1).lengthOfMonth(); i++) {
                LocalDate date = LocalDate.of(year, month, i);
                MultiValueMap<String, CustomRepository.WorkTime> stringListMap = dormitoryCommandService.viewDormitoryWorkers(date, dormitory);
                if (stringListMap.isEmpty()) {
                    result.put(i, new ArrayList<>());
                } else {
                    List<String> names = stringListMap.entrySet().stream()
                            .flatMap(entry -> entry.getValue().stream()
                                    .map(workTime -> new AbstractMap.SimpleEntry<>(entry.getKey(), workTime)))
                            .sorted((e2, e1) -> e2.getValue().getStartTime().compareTo(e1.getValue().getStartTime()))
                            .map(e -> e.getKey())
                            .collect(Collectors.toList());

                    result.put(i, names);
                }
            }
            return result;
        } else if (type.equals("post")) { //우편근무 조회인 경우
            LocalDate date = LocalDate.of(year, month, 1);
            return dormitoryCommandService.viewPostWorker(date);
        } else { //type에 이상한 값이 들어간 경우
            throw new WrongPathRequestException("인 경로는 존재하지 않습니다", type);
        }
    }

    @GetMapping("/schedule/people")
    public Map<Integer, ConcurrentHashMap<String, List<LocalTime>>> todayWorkersDetail(
            @RequestParam("dormitoryNum") int dormitoryNum,
            @RequestParam("month") int month,
            @RequestParam("year") int year,
            @RequestParam("day") int day
    ) {
        Dormitory dormitory = DormitoryConverter.toDormitory(dormitoryNum);
        MultiValueMap<String, CustomRepository.WorkTime> stringWorkTimeMultiValueMap = dormitoryCommandService.viewDormitoryWorkers(LocalDate.of(year, month, day), dormitory);
        Map<Integer, ConcurrentHashMap<String, List<LocalTime>>> result = new ConcurrentHashMap<>();
        int i = 0;

        for (Map.Entry<String, List<CustomRepository.WorkTime>> entry : stringWorkTimeMultiValueMap.entrySet()) {
            String name = entry.getKey();
            List<CustomRepository.WorkTime> value = entry.getValue();
            List<LocalTime> time = new ArrayList<>();

            for (CustomRepository.WorkTime workTime : value) {
                LocalTime start = workTime.getStartTime().toLocalTime();
                LocalTime end = workTime.getLeaveTime().toLocalTime();
                time.add(start);
                time.add(end);
            }

            ConcurrentHashMap<String, List<LocalTime>> nameAndTime = new ConcurrentHashMap<>();
            nameAndTime.put(name, time);
            result.put(i, nameAndTime);
            i++;
        }
        return result;
    }

    @GetMapping("/main/calendar")
    WorkDateResponseDTO.calendarDto calendar(HttpServletRequest request,
                                             @RequestParam("year") int year, @RequestParam("month") int month) {
        String token = request.getHeader("Authorization");
        long userId = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));
        List<LocalDate> localDates = dateCommandService.myWorkdays(year, month, userId);
        return WorkDateResponseDTO.calendarDto.builder()
                .workdates(localDates)
                .build();
    }

    @GetMapping("/main/changehistory")
    List<WorkDateResponseDTO.changeHistory> changeHistory(HttpServletRequest request, @RequestParam("year") int year, @RequestParam("month") int month) {
        String token = request.getHeader("Authorization");
        long userId = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));
        String name = jwtTokenProvider.getUserNameFromAccessToken(token);
        List<WorkScheduleChange> workScheduleChanges = workCommandService.changeHistoryList(userId, LocalDate.of(year, month, 1));
        List<WorkDateResponseDTO.changeHistory> list = new ArrayList<>();
        for (WorkScheduleChange workScheduleChange : workScheduleChanges) {
            WorkDateResponseDTO.changeHistory history = WorkDateResponseDTO.changeHistory.builder()
                    .changeDate(workScheduleChange.getAfterChangeDate())
                    .originDate(workScheduleChange.getBeforeChangeDate())
                    .status(workScheduleChange.getType())
                    .applicant(workScheduleChange.getApplicant().getName())
                    .acceptor(workScheduleChange.getAcceptor().getName())
                    .build();

            list.add(history);
        }
        return list;
    }

    @GetMapping("/main/time")
    MainResponseDTO.TimeDto time(HttpServletRequest request,
                                 @RequestParam("year") int year, @RequestParam("month") int month) {
        String token = request.getHeader("Authorization");
        long userId = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));

        return MainResponseDTO.TimeDto.builder()
                .time(dateCommandService.workHour(year, month, userId))
                .day(dateCommandService.workDays(year, month, userId)).
                build();
    }

    @PostMapping("/main/work")
    MainResponseDTO.Work work(HttpServletRequest request, @RequestBody MainRequestDTO.work workday) {
        String token = request.getHeader("Authorization");
        long userId = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));
        LocalDateTime scheduleTime = LocalDateTime.of(
                Integer.parseInt(workday.getScheduleYear()),
                Integer.parseInt(workday.getScheduleMonth()),
                Integer.parseInt(workday.getScheduleDay()),
                Integer.parseInt(workday.getScheduleHour()),
                Integer.parseInt(workday.getScheduleMin())
        );
        LocalDateTime actualTime = LocalDateTime.of(
                Integer.parseInt(workday.getActualYear()),
                Integer.parseInt(workday.getActualMonth()),
                Integer.parseInt(workday.getActualDay()),
                Integer.parseInt(workday.getActualHour()),
                Integer.parseInt(workday.getActualMin())
        );
        String s = workCommandService.startWork(userId, scheduleTime, actualTime);
        return MainResponseDTO.Work.builder()
                .isSuccess(true)
                .message(s)
                .build();
    }

    /*테스트 아직 안됨*/
    @PostMapping("/main/work/late")
    MainResponseDTO.Work lateReason(HttpServletRequest request,
                                    @RequestBody Map<String, String> data) {
        String reason = data.get("reason");
        String time = data.get("time");
        log.info("사유 : {}, 시간 : {}", reason, time);
        long userId = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(request.getHeader("Authorization")));
        String s = workCommandService.writeReasonByUserId(userId, reason, time);
        return MainResponseDTO.Work.builder()
                .isSuccess(true)
                .message(s)
                .build();
    }
}
