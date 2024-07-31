package University.Dormitory.service.WorkService;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.PostUser;
import University.Dormitory.domain.WorkDate;
import University.Dormitory.domain.WorkScheduleChange;
import University.Dormitory.exception.Handler.BadDateRequestException;
import University.Dormitory.exception.Handler.PKDuplicateException;
import University.Dormitory.exception.Handler.TooLongException;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.PostUserRepository;
import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import University.Dormitory.repository.JPARepository.WorkScheduleChangeRepository;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.WorkDTO.WorkRequestDTO;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkCommandServiceImpl implements WorkCommandService {
    private final WorkDateRepository workDateRepository;
    private final CustomRepository customRepository;
    private final PostUserRepository postUserRepository;
    private final UserRepository userRepository;
    private final WorkScheduleChangeRepository workScheduleChangeRepository;
    private final UserCommandService userCommandService;

    @Override
    public String changeOrSaveScheduleTimeByUserId(long userId,
                                                   LocalDateTime scheduledStartTime,
                                                   LocalDateTime wantStartTime,
                                                   LocalDateTime wantLeaveTime) {
        // `scheduledStartTime`이 `null`인 경우 새로 저장
        if (scheduledStartTime == null) {
            if (customRepository.existsByUserIdAndscheduledStartTime(userId, wantStartTime)) {
                throw new BadDateRequestException("해당 조교의 해당 시간은 이미 저장되었습니다.");
            }
            // 입력된 시작 시간과 종료 시간으로 새 WorkDate 객체 생성
            WorkDate workDate = WorkDate.builder()
                    .user(userRepository.findById(userId).orElseThrow(
                            () -> new UserNotFoundException("해당 학번을 찾을 수 없습니다")
                    ))
                    .scheduledStartTime(wantStartTime)
                    .scheduledLeaveTime(wantLeaveTime)
                    .build();

            try {
                workDateRepository.save(workDate);
            } catch (Exception e) {
                log.error("스케줄 저장 도중에 에러가 발생했습니다. 관리자에게 문의하십시오", e);
                throw new RuntimeException("1saveScheduleTimeByUserId 함수 저장 도중 에러 발생: " + e.getMessage());
            }
            log.info("[{}의 스케줄 저장], 출근시간: {}, 퇴근 시간: {}", userId, wantStartTime, wantLeaveTime);
            return "스케줄 저장";
        } else {
            // `scheduledStartTime`이 `null`이 아닌 경우 스케줄 수정임.
            Optional<WorkDate> optionalWorkDate = workDateRepository.findByUserIdAndScheduledStartTime(userId, scheduledStartTime);

            WorkDate workDate;

            if (optionalWorkDate.isPresent()) {
                workDate = optionalWorkDate.get();
                workDate.setScheduledStartTime(wantStartTime);
                workDate.setScheduledLeaveTime(wantLeaveTime);
            } else {
                throw new BadDateRequestException("해당 날짜가 존재하지 않습니다. 해당 조교가 일하는 날짜를 입력해주세요");
            }

            try {
                workDateRepository.save(workDate);
            } catch (Exception e) {
                log.error("스케줄 변경 도중에 에러가 발생했습니다. 관리자에게 문의하십시오", e);
                throw new RuntimeException("changeScheduleTimeByUserId 함수 저장 도중 에러 발생: " + e.getMessage());
            }
            log.info("[{}의 스케줄 변경], 출근시간: {}, 퇴근 시간: {}", userId, wantStartTime, wantLeaveTime);
            return "스케줄 출/퇴근 시간 변경";
        }
    }

    @Override
    public String giveMyWorkByUserIds(long userId1, long userId2, LocalDateTime userId1Date) {
        long i = customRepository.changeWorkDate(userId1, userId2, userId1Date);
        if (i != 1) {
            throw new UserNotFoundException("오류가 발생했습니다. 관리자에게 문의해주세요.");
        }
        try {
            workScheduleChangeRepository.save(WorkScheduleChange.builder()
                    .applicant(userRepository.findById(userId1).orElseThrow(
                            () -> new UserNotFoundException("신청자의 학번이 존재하지 않습니다.")
                    ))
                    .acceptor((userRepository.findById(userId2).orElseThrow(
                            () -> new UserNotFoundException("바꾸려는 조교가 존재하지 않습니다.")
                    )))
                    .beforeChangeDate(userId1Date)
                    .afterChangeDate(null)//주는 거니까 신청자, 수락자가 존재하고 이후의 날짜는 null로 입력
                    .type("맞교대")
                    .build());
            /**
             * 신청자 기준으로 저장
             * 즉 신청자가(1) 만약 2024.07.19일 마감을 수락자의(2)에게 준다면
             * applicant    acceptor    beforeChangeDate    afterChangeDate
             * 1            2           2024.07.19T09:00    null
             * 으로 저장되는 것
             * acceptor 입장에서는 반대로 봐야함.
             */
        } catch (Exception e) {
            throw new RuntimeException("로그 저장 중 문제가 발생했습니다. 맞교대가 취소되었습니다.");
        }
        return "근무 맞교대 완료";
    }

    @Override
    public String exchangeMyWorkByUserIds(long userId1, long userId2, LocalDateTime userId1StartTime, LocalDateTime userId2StartTime) {
        WorkDate workDate1 = workDateRepository.findByUserIdAndScheduledStartTime(userId1, userId1StartTime).orElseThrow(
                () -> new UserNotFoundException("해당 근무시간에 해당 학번 조교가 존재하지 않습니다.")
        );
        WorkDate workDate2 = workDateRepository.findByUserIdAndScheduledStartTime(userId2, userId2StartTime).orElseThrow(
                () -> new UserNotFoundException("해당 근무시간에 해당 학번 조교가 존재하지 않습니다.")
        );

        try {// ID 값 교환
            workDate1.setUser(customRepository.findByUserId(userId2));
//            workDate1.setId(workDate2.getId());


            workDate2.setUser(customRepository.findByUserId(userId1));
//            workDate2.setId(tempId);

            // 변경사항 저장
            workDateRepository.save(workDate1);
            workDateRepository.save(workDate2);

            workScheduleChangeRepository.save(WorkScheduleChange.builder()
                    .applicant(userRepository.findById(userId1).orElseThrow(
                            () -> new UserNotFoundException("신청자의 학번이 존재하지 않습니다.")
                    ))
                    .acceptor((userRepository.findById(userId2).orElseThrow(
                            () -> new UserNotFoundException("바꾸려는 조교가 존재하지 않습니다.")
                    )))
                    .beforeChangeDate(userId1StartTime)
                    .afterChangeDate(userId2StartTime)
                    /**
                     * 신청자 기준으로 저장
                     * 즉 신청자가(1) 만약 2024.07.19일 마감을 수락자의(2) 2024.07.19 오픈이랑 바꾼다면
                     * applicant    acceptor    beforeChangeDate    afterChangeDate
                     * 1            2           2024.07.19T09:00    2024.07.19T16:00
                     * 으로 저장되는 것
                     * acceptor 입장에서는 반대로 봐야함.
                     */
                    .type("근무교체")
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("근무교체 도중 에러가 발생했습니다.(exchangeMyWorkByUserIds 함수)" + e.getMessage());
        }
        return "근무교체 완료";
    }

    @Override
    public String writeReasonByUserId(long userId, String reason, LocalDateTime date) {
        int length = reason.length();
        if (length > 254) {
            throw new TooLongException("글자 수가 너무 많습니다. 관리자에게 문의하거나 길이를 줄여주세요");
        }
        try {
            long l = customRepository.writeReason(userId, reason, date);
        } catch (Exception e) {
            throw new RuntimeException("사유 작성 저장 도중 에러가 발생했습니다. 관리자에게 문의해주세요" + e.getMessage());
        }
        return "사유 작성 완료";
    }

    @Override
    public String changeActualTimeByUserId(long userId, LocalDateTime beforeActualTime, LocalDateTime afterActualTime) {
        Optional<WorkDate> optionalWorkDate = workDateRepository.findByUserIdAndActualStartTime(userId, beforeActualTime);
        if (optionalWorkDate.isEmpty()) {
            throw new UserNotFoundException("학번 혹은 근무시간이 잘못되었습니다. 해당 근무시간에 해당 학번이 존재하는지 확인해주세요");
        }
        try {
            WorkDate workDate = optionalWorkDate.get();
            workDate.setActualStartTime(afterActualTime);
            workDateRepository.save(workDate);
        } catch (Exception e) {
            throw new RuntimeException("실제 근무시간 변경이 완료되지 않았습니다. 관리자에게 문의하세요");
        }
        return "출근 시간 변경 성공";
    }

    @Override
    public String postWorkByUserId(long userId, LocalDate date) {
        PostUser postUser = PostUser.builder()
                .postWorkDate(date)
                .user(userRepository.findById((long) userId).orElseThrow(() -> new UserNotFoundException("해당 학번은 존재하지 않습니다.")))
                .build();
        try {
            postUserRepository.save(postUser);
        } catch (Exception e) {
            throw new RuntimeException("우편 근무 저장 중 오류가 발생했습니다. 관리자에게 문의하세요" + e.getMessage());
        }
        return "성공적으로 저장되었습니다";
    }

    @Override
    public String exchangePostWorkByUserId(long userId1, LocalDate date, long userId2, LocalDate date2) {
        PostUser postUser1 = postUserRepository.findByUserIdAndPostWorkDate(userId1, date).orElseThrow(
                () -> new UserNotFoundException("해당 조교가 해당 근무일에 존재하지 않거나 해당 근무일에 해당 조교가 없습니다.")
        );
        PostUser postUser2 = postUserRepository.findByUserIdAndPostWorkDate(userId2, date2).orElseThrow(
                () -> new UserNotFoundException("해당 조교가 해당 근무일에 존재하지 않거나 해당 근무일에 해당 조교가 없습니다.")
        );
        try {
            int tempId = postUser1.getId();
            postUser1.setId(postUser2.getId());
            postUser2.setId(tempId);
            postUserRepository.save(postUser1);
            postUserRepository.save(postUser2);

            workScheduleChangeRepository.save(WorkScheduleChange.builder()
                    .applicant(userRepository.findById(userId1).orElseThrow(
                            () -> new UserNotFoundException("신청자의 학번이 존재하지 않습니다.")
                    ))
                    .acceptor((userRepository.findById(userId2).orElseThrow(
                            () -> new UserNotFoundException("바꾸려는 조교가 존재하지 않습니다.")
                    )))
                    .beforeChangeDate(date.atStartOfDay())
                    .afterChangeDate(date2.atStartOfDay())
                    /**
                     * 신청자 기준으로 저장
                     * 즉 신청자가(1) 만약 2024.07.19일 우편을 수락자의(2) 2024.07.20 우편이랑 바꾼다면
                     * applicant    acceptor    beforeChangeDate    afterChangeDate
                     * 1            2           2024.07.19          2024.07.20
                     * 으로 저장되는 것
                     * acceptor 입장에서는 반대로 봐야함.
                     */
                    .type("우편근무교대")
                    .build());
        } catch (DataIntegrityViolationException e) {
            throw new PKDuplicateException("PostUser 근무교환 저장 중 오류 발생 - 중복된 기본 키: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("PostUser 근무교환 저장 중 오류 발생" + e.getMessage());
        }
        return "우편근무교체가 완료되었습니다.";
    }

    @Override
    public String startWork(long userId, LocalDateTime schedueldTime, LocalDateTime ActualTime) {
        customRepository.updateActualStartTime(userId, schedueldTime, ActualTime);
        return "출근 확인";
    }

    //    스케줄 삭제
    @Override
    public String delteSchedule(Dormitory dormitory, LocalDate date) {
        log.info("스케줄 삭제, 받은 기숙사와 날짜: {}, {}", dormitory, date);
        return customRepository.deleteSchedule(date, dormitory);
    }

    @Override
    public List<Tuple> actualWorkTime(long userId, LocalDate date) {
        return customRepository.findScheduleTimeAndActualTimeByUserId(userId, date);
    }

    @Override
    public List<WorkScheduleChange> changeHistoryList(long userId, LocalDate date) {
        List<WorkScheduleChange> workChangeHistoryByAcceptorUserId = customRepository.getWorkChangeHistoryByAcceptorUserId(userId, date);
        List<WorkScheduleChange> workChangeHistoryByApplicantUserId = customRepository.getWorkChangeHistoryByApplicantUserId(userId, date);
        List<WorkScheduleChange> allList = new ArrayList<>();
        allList.addAll(workChangeHistoryByAcceptorUserId);
        allList.addAll(workChangeHistoryByApplicantUserId);
        return allList;
    }

    public String saveNewWork(List<WorkRequestDTO.worker> workerList, DateTimeFormatter formatter) {
        LocalDateTime endTime;
        LocalDateTime startTime;
        LocalDateTime scheduleTime;

        for (WorkRequestDTO.worker worker : workerList) {
            if (worker.getTimes().size() == 2) {
                startTime = LocalDateTime.parse(worker.getTimes().get(0), formatter);
                endTime = LocalDateTime.parse(worker.getTimes().get(1), formatter);
                String name = worker.getName();
                log.info("일하는 사람: {}", name);
                log.info("일하는 시간:{}, {}", startTime, endTime);
                long userIdByName = customRepository.findUserIdByName(name).orElseThrow(
                        () -> new UserNotFoundException("해당 학번이 존재하지 않습니다.")
                );
                changeOrSaveScheduleTimeByUserId(userIdByName, null, startTime, endTime);
                return "스케줄 저장";
            } else if (worker.getTimes().size() == 3) {
                scheduleTime = LocalDateTime.parse(worker.getTimes().get(0), formatter);
                startTime = LocalDateTime.parse(worker.getTimes().get(1), formatter);
                endTime = LocalDateTime.parse(worker.getTimes().get(2), formatter);
                String name = worker.getName();
                long userIdByName = customRepository.findUserIdByName(name).orElseThrow(
                        () -> new UserNotFoundException("해당 학번이 존재하지 않습니다.")
                );
                changeOrSaveScheduleTimeByUserId(userIdByName, scheduleTime, startTime, endTime);
                return "스케줄 수정";
            }
        }
        return "";
    }
}

