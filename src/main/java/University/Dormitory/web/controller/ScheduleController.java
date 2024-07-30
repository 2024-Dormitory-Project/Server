package University.Dormitory.web.controller;

import University.Dormitory.Converter.DormitoryConverter;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.exception.Handler.BadDateRequestException;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.service.DormitoryService.DormitoryCommandService;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.service.WorkService.WorkCommandService;
import University.Dormitory.web.dto.MainPageDTO.MainResponseDTO;
import University.Dormitory.web.dto.ScheduleDTO.ScheduleRequestDTO;
import University.Dormitory.web.dto.ScheduleDTO.ScheduleResponseDTO;
import University.Dormitory.web.dto.WorkDTO.WorkRequestDTO;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static University.Dormitory.domain.QWorkDate.workDate;
@Transactional
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/schedule")
public class ScheduleController {
    private final WorkCommandService workCommandService;
    private final UserCommandService userCommandService;
    private final DormitoryCommandService dormitoryCommandService;

    @GetMapping("/scheduleworktime")
    HashMap<Integer, List<String>> scheduleWorkTime(@RequestParam("dormitory") int dormitoryNum, @RequestParam("year") int year, @RequestParam("month") int month) {
        Dormitory dormitory = DormitoryConverter.toDormitory(dormitoryNum);
        HashMap<Integer, List<String>> result = new HashMap<>();
        for (int i = 1; i <= LocalDate.of(year, month, 1).lengthOfMonth(); i++) {
            LocalDate date = LocalDate.of(year, month, i);
            MultiValueMap<String, CustomRepository.WorkTime> stringListMap = dormitoryCommandService.viewDormitoryWorkers(date, dormitory);
            if (stringListMap.isEmpty()) {
                result.put(i, new ArrayList<>());
            } else {
                List<String> names = new ArrayList<>(stringListMap.keySet());
                result.put(i, names);
            }
        }
        return result;
    }

    @PostMapping("/scheduleworktime")
    public MainResponseDTO.Work saveNewSchedule(@RequestBody WorkRequestDTO.SaveWork saveWork) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        HashMap<String, List<String>> workerList = saveWork.getWorkerList();
        for (Map.Entry<String, List<String>> stringListEntry : workerList.entrySet()) {
            log.info("키값. 이름 : {}", stringListEntry.getKey());
            log.info("시간 리스트. : {}", stringListEntry.getValue().toString());
        }
        String s = workCommandService.saveNewWork(workerList, formatter);
        return MainResponseDTO.Work.builder()
                .message(s)
                .isSuccess(true)
                .build();
    }

    @DeleteMapping("/scheduleworktime")
    MainResponseDTO.Work deleteSchedule(@RequestBody ScheduleRequestDTO.todayWorkersDetailDto info) {
        Dormitory dormitory = DormitoryConverter.toDormitory(info.getDormitoryNum());
        LocalDate date = LocalDate.of(info.getYear(), info.getMonth(), info.getDay());
        log.info("삭제 스케줄의 날짜 : {}", date);
        String s = workCommandService.delteSchedule(dormitory, date);
        return MainResponseDTO.Work.builder()
                .message(s)
                .isSuccess(true)
                .build();
    }

    @GetMapping("/actualworktime")
    List<ScheduleResponseDTO.actualWorkTime> actualWorkTime(@RequestParam("dormitory") int dormitoryNum, @RequestParam("year") int year, @RequestParam("month") int month, @RequestParam("name") String name) {
        Optional<Long> userIdByName = userCommandService.findUserIdByName(name);
        if (userIdByName.isPresent()) {
            List<Tuple> tuples = workCommandService.actualWorkTime(userIdByName.get(), LocalDate.of(year, month, 1));
            List<ScheduleResponseDTO.actualWorkTime> workerList = new ArrayList<>();
            for (Tuple tuple : tuples) {
                int actualYear = tuple.get(workDate.actualStartTime).getYear();
                int actualMonth = tuple.get(workDate.actualStartTime).getMonthValue();
                int actualDay = tuple.get(workDate.actualStartTime).getDayOfMonth();
//                스케줄 시작 시간으로 가정. 만약 같은 날에 두번 근무가 들어가도 어짜피 스케줄시간으로 따지니까 그냥 이거만 가져와서 return 값에 넣어줌
//                int scheduleYear = tuple.get(workDate.scheduledStartTime).getYear();
//                int scheduleMonth = tuple.get(workDate.scheduledStartTime).getMonthValue();
//                int scheduleDay = tuple.get(workDate.scheduledStartTime).getDayOfMonth();

                String name2 = tuple.get(workDate.user.name);

                ScheduleResponseDTO.actualWorkTime worker = ScheduleResponseDTO.actualWorkTime.builder()
                        .date(LocalDate.of(actualYear, actualMonth, actualDay))
                        .scheduleTime(tuple.get(workDate.scheduledStartTime).toLocalTime())
                        .actualTime(tuple.get(workDate.scheduledStartTime).toLocalTime())
                        .name(name2)
                        .build();

                workerList.add(worker);
            }
            return workerList;
        } else {
            throw new UserNotFoundException("해당 이름의 학번이 존재하지 않습니다");
        }
    }

    @PatchMapping("/actualworktime/changeactualscheduletime")
    MainResponseDTO.Work changeActualScheduleTime(@RequestBody ScheduleRequestDTO.changeActualScheduleTime info) {
        Optional<Long> userIdByName = userCommandService.findUserIdByName(info.getName());
        if (userIdByName.isPresent()) {
            String s = workCommandService.changeActualTimeByUserId(userIdByName.get(),
                    LocalDateTime.of(info.getOriginYear(), info.getOriginMonth(), info.getOriginDay(), info.getOriginHour(), info.getOriginMin()),
                    LocalDateTime.of(info.getYear(), info.getMonth(), info.getDay(), info.getHour(), info.getMin()));
            return MainResponseDTO.Work.builder()
                    .isSuccess(true)
                    .message(s)
                    .build();
        } else {
            throw new UserNotFoundException("해당 이름의 학번이 존재하지 않습니다.");
        }
    }
}
