package University.Dormitory.web.dto.WorkDateDTO;

import lombok.Getter;

import java.time.LocalDateTime;

public class WorkDateRequestDTO {
    /**
     *      * @param userId 학번
     *      * @param startTime 나의 스케줄 상 근무 시작 시간
     *      * @param leaveTime 나의 스케줄 상 근무 종료 시간
     */
    @Getter
    public static class changeWorkDto {
        int userId;
        LocalDateTime startTime;
        LocalDateTime leaveTIme;
    }


    public static class giveWorkDto {
        int userId1;
        int userId2;
        LocalDateTime localDateTime;
    }

    public static class saveNewWorkUser {

    }
}
