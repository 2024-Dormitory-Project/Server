package University.Dormitory.web.controller;

import University.Dormitory.Converter.DormitoryConverter;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.WorkScheduleChange;
import University.Dormitory.exception.Handler.UserNotFoundException;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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


    @PatchMapping("/givework")
    WorkResposneDTO.workResult giveWork(HttpServletRequest request, @RequestBody WorkRequestDTO.giveWorkDto time) {
        String token = request.getHeader("Authorization");
        long userId1 = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));

        LocalDateTime changeTime = LocalDateTime.of(
                Integer.parseInt(time.getYear()), Integer.parseInt(time.getMonth()),
                Integer.parseInt(time.getDay()), Integer.parseInt(time.getHour()), Integer.parseInt(time.getMin()));
        String acceptor = time.getUserName();
        Optional<Long> acceptorUserId = userCommandService.findUserIdByName(acceptor);
        if (acceptorUserId.isPresent()) {
            long userId2 = acceptorUserId.get();
            String s = workCommandService.giveMyWorkByUserIds(userId1, userId2, changeTime);
            return WorkResposneDTO.workResult.builder().message(s).isSuccess(true).build();
        } else {
            throw new UserNotFoundException("해당 학번의 학생을 찾을 수 없습니다");
        }
    }

    @PatchMapping("/exchangework")
    WorkResposneDTO.workResult exchangeWork(HttpServletRequest request, @RequestBody WorkRequestDTO.exchangeWorkDto exchange) {
        String token = request.getHeader("Authorization");
        long userId1 = Long.parseLong(jwtTokenProvider.getUserIdFromAcessToken(token));

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
    }

    @GetMapping("/schedule")
    HashMap<Integer, List<String>> todayWorkersName(HttpServletRequest request,
                                                    @RequestParam("dormitoryNum") int dormitoryNum,
                                                    @RequestParam("month") int month,
                                                    @RequestParam("year") int year) {
        Dormitory dormitory = DormitoryConverter.toDormitory(dormitoryNum);
        HashMap<Integer, List<String>> result = new HashMap<>();
        List<String> names = new ArrayList<>();
        for (int i = 1; i <= LocalDate.of(year, month, 1).lengthOfMonth(); i++) {
            LocalDate date = LocalDate.of(year, month, i);
            MultiValueMap<String, CustomRepository.WorkTime> stringListMap = dormitoryCommandService.viewDormitoryWorkers(date, dormitory);
            names.addAll(stringListMap.keySet());
            result.put(i, names);
        }
        return result;
    }

    @GetMapping("/schedule/people")
    HashMap<Integer, HashMap<String, List<LocalTime>>> todayWorkersDetail(
            @RequestParam("dormitoryNum") int dormitoryNum,
            @RequestParam("month") int month,
            @RequestParam("year") int year,
            @RequestParam("day") int day
    ) {
        Dormitory dormitory = DormitoryConverter.toDormitory(dormitoryNum);
        MultiValueMap<String, CustomRepository.WorkTime> stringWorkTimeMultiValueMap = dormitoryCommandService.viewDormitoryWorkers(LocalDate.of(year, month, day), dormitory);
        HashMap<Integer, HashMap<String, List<LocalTime>>> result = new HashMap<>();
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

            HashMap<String, List<LocalTime>> nameAndTime = new HashMap<>();
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
                .day(dateCommandService.workDays(year, month, year)).
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
}
