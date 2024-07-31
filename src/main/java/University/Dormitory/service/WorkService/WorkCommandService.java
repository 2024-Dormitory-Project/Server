package University.Dormitory.service.WorkService;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.WorkScheduleChange;
import University.Dormitory.web.dto.WorkDTO.WorkRequestDTO;
import com.querydsl.core.Tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public interface WorkCommandService {
    /**
     * 스케줄 저장 혹은 수정
     *
     *@param userId 학번
     *@param scheduledStartTime 스케줄 상 근무 시작 시간
     * @param wantStartTime 변경하려는 출근 시간
     *@param wantLeaveTime 변경하려는 퇴근 시간
     */
    public String changeOrSaveScheduleTimeByUserId(long userId,
                                          LocalDateTime scheduledStartTime, LocalDateTime wantStartTime,
                                          LocalDateTime wantLeaveTime);

    /**
     *  근무맞교대. 신청자가 수락자에게 근무를 주는 것.
     *
     * @param userId1 신청자의 근무 ID
     * @param userId2 수락자의 사람의 ID
     * @param userId1Date 신청자의 줄 근무 시작 시간
     * return 값으로 종료시간과 시작시간 모두 주기. 그래야 명확하게 몇시부터 몇시 근무를 주는 지 알 수 있을 테니까.
     */
    String giveMyWorkByUserIds(long userId1, long userId2, LocalDateTime userId1Date);


//        근무 교체(나의 근무와 다른 사람의 근무 변경)
//    나의 근무시간과 근무를 받을 사람의 시간 모두 필요
    /**
     * 근무교체. 신청자와 수락자의 근무 시간을 변경하는 것.
     *
     * @param userId1 신청자의 학번
     * @param userId2 수락자의 학번
     * @param userId1StartTime 신청자의 근무 시작 시간
     * @param userId2StartTime 수락자의 근무 시작 시간
     * return 값으로 신청자의 시작 및 종료시간, 수락자의 신청 및 종료시간이 어떻게 바뀌는 지 알 수 있도록 시간 값 모두 주기(총 4개)
     */
    String exchangeMyWorkByUserIds(long userId1, long userId2, LocalDateTime userId1StartTime, LocalDateTime userId2StartTime);


    /**
     * 만약 스케줄상 시간표보다 늦었다면 해당 함수 실행.
     *
     * @param userId 사유 작성할 사람의 학번
     * @param reason 사유
     * @param date 사유 쓸 날짜. 이 날짜는 스케줄상의 날짜임.
     */
    String writeReasonByUserId(long userId, String reason, LocalDateTime date);



//    나의 출근 시간 임의 변경

    /**
     * 실제 해당 학번의 출/퇴근 시간을 바꿀 때
     * ex) 정시에 왔는데 실수로 출근 안 찍어서 정시 출근으로 바꿀 때.
     *
     * @param userId 출근 시간을 바꿀 사람의 학번
     * @param beforeActualTime 바꾸기 전의 시간
     * @param afterActualTime 바꿀 시간
     */
    String changeActualTimeByUserId(long userId, LocalDateTime beforeActualTime, LocalDateTime afterActualTime);

    /**
     * 처음 스케줄 짤 때 출/퇴근 넣기. 한번에 여러개일 경우 해당 함수 반복문 처리하면 될 것으로 예상.
     *
     * @param userId 학번
     * @param startTime 근무 시작 시간
     * @param leaveTime 근무 종료 시간
     */


    /**
     *처음 우편근무일 넣을 때.
     *
     * @param userId 학번
     * @param date 우편근무일
     */
//    우편 근무 날짜 넣기 기능
    String postWorkByUserId(long userId, LocalDate date);


    /**
     * 우편근무자끼리 근무일 변경할 때
     *
     * @param userId1 신청자의 학번
     * @param date 신청자의 날짜
     * @param userId2 수락자의 학번
     * @param date2 수락자의 날짜
     */
    String exchangePostWorkByUserId(long userId1, LocalDate date, long userId2, LocalDate date2);

    /**
     * 출근시간 저장하기
     * @param userId 유저아이디
     * @param localDateTime 출근시간
     * @return
     */
    String startWork(long userId, LocalDateTime schedueldTime, LocalDateTime ActualTime);

    /**
     * 스케줄 지우기
     * @param dormitory
     * @param date
     * @return
     */
    String delteSchedule(Dormitory dormitory,LocalDate date);

    /**
     * 실제 출근시간 및 스케줄 시간 가져오기
     *
     */
    List<Tuple> actualWorkTime(long userId, LocalDate date);

    List<WorkScheduleChange> changeHistoryList(long userId, LocalDate date);

    /**
     * 원래 controller에서 구현했으나 transaction 오류 통채로 서비스 구간으로 이동
     */
    String saveNewWork(List<WorkRequestDTO.worker> workerList);
}