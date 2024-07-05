package University.Dormitory.service.WorkService;

import University.Dormitory.Converter.WorkDateConverter;
import University.Dormitory.domain.PostUser;
import University.Dormitory.domain.WorkDate;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.PostUserRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import University.Dormitory.web.dto.WorkDateDTO.WorkDateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class WorkCommandServiceImpl implements WorkCommandService {
    private final WorkDateRepository workDateRepository;
    private final CustomRepository customRepository;
    private final PostUserRepository postUserRepository;

    @Override
    public void changeMyWorkTimeByUserIds(WorkDateRequestDTO.changeWorkDto request) {
        WorkDate workDateByUserId = workDateRepository.findById((long) request.getUserId()).orElseThrow();
//        예외 추가하기. 해당 유저 존재X
        workDateByUserId.setScheduledStartTime(request.getStartTime());
        workDateByUserId.setScheduledLeaveTime(request.getLeaveTIme());

        workDateRepository.save(workDateByUserId);
//        아직 테스트 안함. 테스트 필요
    }

    @Override
    public String giveMyWorkByUserIds(int userId1, int userId2, LocalDateTime userId1Date) {
        long i = customRepository.changeWorkDate(userId1, userId2, userId1Date);
        if (i != 1) {
            throw new RuntimeException("무언가 오류가 발생했습니다. 관리자에게 문의 바랍니다.");
        }
        return "성공 DTO";
    }

    @Override
    public String exchangeMyWorkByUserIds(int userId1, int userId2, LocalDateTime userId1StartTime, LocalDateTime userId2StartTime) {
        WorkDate workDate1 = workDateRepository.findByUserIdAndScheduledStartTime((long) userId1, userId1StartTime).orElseThrow();
        WorkDate workDate2 = workDateRepository.findByUserIdAndScheduledStartTime((long) userId2, userId2StartTime).orElseThrow();

        // ID 값 교환
        Long tempId = workDate1.getId();
        workDate1.setId(workDate2.getId());
        workDate2.setId(tempId);

        // 변경사항 저장
        workDateRepository.save(workDate1);
        workDateRepository.save(workDate2);

        return "교환이 성공적으로 완료되었습니다.";
    }

    @Override
    public String writeReasonByUserId(int userId, String reason, LocalDateTime date) {
        customRepository.writeReason(userId, reason, date);
        return "사유 작성 완료";
    }

    @Override
    public String changeActualTimeByUserId(int userId, LocalDateTime beforeActualTime, LocalDateTime afterActualTime) {
        Optional<WorkDate> optionalWorkDate = workDateRepository.findByUserIdAndActualStartTime((long) userId, beforeActualTime);
        if (optionalWorkDate.isEmpty()) {
            throw new RuntimeException("학번 혹은 근무시간이 잘못되었습니다.");
        }
        WorkDate workDate = optionalWorkDate.get();
        workDate.setActualLeaveTime(afterActualTime);
        workDateRepository.save(workDate);
        return "근무시간이 성공적으로 변경되었습니다.";
    }



    @Override
    public String saveWorkByUserId(int userId, LocalDateTime startTime, LocalDateTime leaveTime) {
        WorkDate workDate = WorkDateConverter.SaveWorkDate(userId, startTime, leaveTime);
        workDateRepository.save(workDate);
        return "성공적으로 저장되었습니다";
    }

    @Override
    public String postWorkByUserId(int userId, LocalDate date) {
        PostUser postUser = new PostUser(userId, date);
        postUserRepository.save(postUser);
        return "성공적으로 저장되었습니다";
    }

    @Override
    public String exchangePostWorkByUserId(int userId1, LocalDate date, int userId2, LocalDate date2) {
        PostUser postUser1 = postUserRepository.findById((long) userId1).orElseThrow();
        PostUser postUser2 = postUserRepository.findById((long) userId2).orElseThrow();
        int tempId = postUser1.getUserId();
        postUser1.setUserId(postUser2.getUserId());
        postUser2.setUserId(tempId);
        postUserRepository.save(postUser1);
        postUserRepository.save(postUser2);
        return "교환이 완료되었습니다.";
    }
}

