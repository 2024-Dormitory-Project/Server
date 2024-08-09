package University.Dormitory.web.controller;

import University.Dormitory.Converter.DormitoryConverter;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.exception.Handler.WrongPathRequestException;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.service.DormitoryService.DormitoryCommandService;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.service.WorkService.WorkCommandService;
import University.Dormitory.web.dto.MainPageDTO.MainResponseDTO;
import University.Dormitory.web.dto.ScheduleDTO.ScheduleRequestDTO;
import University.Dormitory.web.dto.ScheduleDTO.ScheduleResponseDTO;
import University.Dormitory.web.dto.WorkDTO.WorkRequestDTO;
import com.querydsl.core.Tuple;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static University.Dormitory.domain.QWorkDate.workDate;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/schedule")
@Tag(name="SCHEDULE-CONTROLLER", description = "[스케줄 관리 조교용 API], 사감, 스케줄 관리 조교만 접속 가능")
public class ScheduleController {
    private final WorkCommandService workCommandService;
    private final UserCommandService userCommandService;
    private final DormitoryCommandService dormitoryCommandService;

    @GetMapping("/scheduleworktime/{type}")
    @Operation(
            summary = "달력 스케줄 조회",
            description = "assistant/schedule/{type} API와 똑같이 작동함" +
                    "같은 이름이 있어도 반환가능하고 스케줄 시작시간에 따라 정렬한 결과를 return함." +
                    "한마디로 그냥 날짜-이름 그대로 표시하면 된다는 뜻, 시간 정렬은 내가 이미 함"
    )
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "")
//    })
    @Parameters({
            @Parameter(name = "type", description = "기숙사 조회인 경우 1,2,3 우편 근무인 경우 post"),
            @Parameter(name = "year", description = "연도 입력"),
            @Parameter(name = "month", description = "달 입력, 1~12의 값만 입력 가능")
    })
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

    @PostMapping("/scheduleworktime/{type}")
    @Operation(
            summary = "스케줄 저장 및 수정",
            description = "스케줄 저장 혹은 수정, 리스트 값 3개 넘길 시 수정, 2개일 시 저장. 수정일 때는 스케줄 시작시간을 넘기고 그 뒤에 변경할 스케줄 시작시간과 퇴근시간을 보내면 되고\" +\n" +
                    "저장인 경우에는 출근시간, 퇴근시간만 각각 보내면 됨"
    )
    @Parameters({
            @Parameter(name = "type", description = "기숙사 조회인 경우 1,2,3 우편 근무인 경우 post", required = true),
            @Parameter(name = "year", description = "연도 입력", required = true),
            @Parameter(name = "month", description = "달 입력, 1~12의 값만 입력 가능", required = true)
    })
    public MainResponseDTO.Work saveNewSchedule(@PathVariable("type") String type, @RequestBody List<WorkRequestDTO.worker> saveWork) {
        if (type.equals("1") || type.equals("2") || type.equals("3")) { //기숙사 조회인 경우
            for (WorkRequestDTO.worker worker : saveWork) {
                log.info("근무자 이름들 :{}", worker.getName());
            }
            String s = workCommandService.saveNewWork(saveWork);
            return MainResponseDTO.Work.builder()
                    .message(s)
                    .isSuccess(true)
                    .build();
        } else if (type.equals("post")) {
            String s = workCommandService.savePostWorkByUserId(saveWork);
            return MainResponseDTO.Work.builder()
                    .message(s)
                    .isSuccess(true)
                    .build();
        } else {
            throw new WrongPathRequestException("인 경로는 존재하지 않습니다", type);
        }
    }

    @DeleteMapping("/scheduleworktime/{type}")
    @Operation(
            summary = "스케줄 삭제",
            description = "스케줄 삭제"
    )
    @Parameters({
            @Parameter(name = "type", description = "기숙사 조회인 경우 1,2,3 우편 근무인 경우 post", required = true),
    })
    MainResponseDTO.Work deleteSchedule(@PathVariable("type") String type, @RequestBody ScheduleRequestDTO.todayWorkersDetailDto info) {
        if (type.equals("1") || type.equals("2") || type.equals("3")) { //기숙사 조회인 경우
            Dormitory dormitory = DormitoryConverter.toDormitory(Integer.parseInt(type));
            LocalDate date = LocalDate.of(info.getYear(), info.getMonth(), info.getDay());
            log.info("삭제 스케줄의 날짜 : {}", date);
            String s = workCommandService.deleteSchedule(dormitory, date);
            return MainResponseDTO.Work.builder()
                    .message(s)
                    .isSuccess(true)
                    .build();
        } else if (type.equals("post")) {
            String s = workCommandService.deletePostWork(LocalDate.of(info.getYear(), info.getMonth(), info.getDay()));
            return MainResponseDTO.Work.builder()
                    .message(s)
                    .isSuccess(true)
                    .build();
        } else
            throw new WrongPathRequestException("인 경로는 존재하지 않습니다", type);
    }


    @GetMapping("/actualworktime")
    @Operation(
            summary = "실제 출근 및 스케줄 시간 조회 페이지",
            description = "실제 출근시간과 스케줄 출근시간 2개를 보내주는 API, 만약 지각이라면 사유도 같이 보내줌"
    )
    @Parameters({
            @Parameter(name = "year", description = "연도 입력", required = true),
            @Parameter(name = "month", description = "달 입력, 1~12의 값만 입력 가능", required = true),
            @Parameter(name="name", description = "조회할 조교의 이름")
    })
    List<ScheduleResponseDTO.actualWorkTime> actualWorkTime(@RequestParam("year") int year, @RequestParam("month") int month, @RequestParam("name") String name) {
        Optional<Long> userIdByName = userCommandService.findUserIdByName(name);
        if (userIdByName.isPresent()) {
            List<Tuple> tuples = workCommandService.actualWorkTime(userIdByName.get(), LocalDate.of(year, month, 1));
            List<ScheduleResponseDTO.actualWorkTime> workerList = new ArrayList<>();
            for (Tuple tuple : tuples) {
               /* int actualYear = tuple.get(workDate.actualStartTime).getYear();
                int actualMonth = tuple.get(workDate.actualStartTime).getMonthValue();
                int actualDay = tuple.get(workDate.actualStartTime).getDayOfMonth();
                스케줄 시작 시간으로 가정. 만약 같은 날에 두번 근무가 들어가도 어짜피 스케줄시간으로 따지니까 그냥 이거만 가져와서 return 값에 넣어줌
                int scheduleYear = tuple.get(workDate.scheduledStartTime).getYear();
                int scheduleMonth = tuple.get(workDate.scheduledStartTime).getMonthValue();
                int scheduleDay = tuple.get(workDate.scheduledStartTime).getDayOfMonth();*/
                String name2 = tuple.get(workDate.user.name);
                ScheduleResponseDTO.actualWorkTime worker = ScheduleResponseDTO.actualWorkTime.builder()
                        .date(LocalDate.of(tuple.get(workDate.actualStartTime).getYear(), tuple.get(workDate.actualStartTime).getMonthValue(), tuple.get(workDate.actualStartTime).getDayOfMonth()))
                        .scheduleTime(tuple.get(workDate.scheduledStartTime))
                        .actualTime(tuple.get(workDate.scheduledStartTime))
                        .name(name2)
                        .Reason(tuple.get(workDate.Reason))
                        .build();

                workerList.add(worker);
            }
            return workerList;
        } else {
            throw new UserNotFoundException("해당 이름의 학번이 존재하지 않습니다");
        }
    }

    @PatchMapping("/actualworktime/changeactualscheduletime")
    @Operation(
            summary = "실제 출근시간 바꾸는 API",
            description = "스케줄시간과, 스케줄시간에 출근한 시간을 바꾸는 API.\n변경할 시간의 시간정보와 바꿀 시간 정보를 보내주면 됨\n" +
                    "ex) 2024/8/9/13:45 -> 2024/8/9/13:30 앞이 origin붙은 시간정보, 뒤가 시간정보"
    )
    @Parameters({
    })
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