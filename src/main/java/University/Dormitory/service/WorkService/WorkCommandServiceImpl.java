package University.Dormitory.service.WorkService;

import University.Dormitory.Converter.WorkDateConverter;
import University.Dormitory.domain.PostUser;
import University.Dormitory.domain.WorkDate;
import University.Dormitory.exception.PKDuplicateException;
import University.Dormitory.exception.TooLongException;
import University.Dormitory.exception.UserNotFoundException;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.PostUserRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    public String changeScheduleTimeByUserId(int userId, LocalDateTime scheduledStartTime, LocalDateTime wantStartTime, LocalDateTime wantLeaveTime) {
        WorkDate workDateByUserId = workDateRepository.findByUserIdAndScheduledStartTime((long) userId, scheduledStartTime)
                .orElseThrow(() -> new UserNotFoundException("해당 근무시간에 해당 학번 조교가 존재하지 않습니다."));
        workDateByUserId.setScheduledStartTime(wantStartTime);
        workDateByUserId.setScheduledLeaveTime(wantLeaveTime);

        workDateRepository.save(workDateByUserId);
        return "스케줄 출/퇴근 시간이 변경되었습니다.";
//        아직 테스트 안함. 테스트 필요
    }

    @Override
    public String giveMyWorkByUserIds(int userId1, int userId2, LocalDateTime userId1Date) {
        long i = customRepository.changeWorkDate(userId1, userId2, userId1Date);
        if (i != 1) {
            throw new UserNotFoundException("오류가 발생했습니다. 관리자에게 문의해주세요.");
        }
        return "근무 맞교대가 완료되었습니다.";
    }

    @Override
    public String exchangeMyWorkByUserIds(int userId1, int userId2, LocalDateTime userId1StartTime, LocalDateTime userId2StartTime) {
        WorkDate workDate1 = workDateRepository.findByUserIdAndScheduledStartTime((long) userId1, userId1StartTime).orElseThrow(
                () -> new UserNotFoundException("해당 근무시간에 해당 학번 조교가 존재하지 않습니다.")
        );
        WorkDate workDate2 = workDateRepository.findByUserIdAndScheduledStartTime((long) userId2, userId2StartTime).orElseThrow(
                () -> new UserNotFoundException("해당 근무시간에 해당 학번 조교가 존재하지 않습니다.")
        );

        // ID 값 교환
        Long tempId = workDate1.getId();
        workDate1.setId(workDate2.getId());
        workDate2.setId(tempId);

        // 변경사항 저장
        workDateRepository.save(workDate1);
        workDateRepository.save(workDate2);

        return "근무교체가 성공적으로 완료되었습니다.";
    }

    @Override
    public String writeReasonByUserId(int userId, String reason, LocalDateTime date) {
        int length = reason.length();
        if (length > 254) {
            throw new TooLongException("글자 수가 너무 많습니다. 관리자에게 문의하거나 길이를 줄여주세요");
        }
        long l = customRepository.writeReason(userId, reason, date);
        return "사유 작성 완료";
    }

    @Override
    public String changeActualTimeByUserId(int userId, LocalDateTime beforeActualTime, LocalDateTime afterActualTime) {
        Optional<WorkDate> optionalWorkDate = workDateRepository.findByUserIdAndActualStartTime((long) userId, beforeActualTime);
        if (optionalWorkDate.isEmpty()) {
            throw new UserNotFoundException("학번 혹은 근무시간이 잘못되었습니다.");
        }
        WorkDate workDate = optionalWorkDate.get();
        workDate.setActualLeaveTime(afterActualTime);
        workDateRepository.save(workDate);
        return "출/퇴근 시간이 성공적으로 변경되었습니다.";
    }


    @Override
    public String saveWorkByUserId(int userId, LocalDateTime startTime, LocalDateTime leaveTime) {
        WorkDate workDate = WorkDateConverter.SaveWorkDate(userId, startTime, leaveTime);
        try {
            workDateRepository.save(workDate);
        } catch (DataIntegrityViolationException e) {
            throw new PKDuplicateException("WorkDate 저장 중 오류 발생 - 중복된 기본 키: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("WorkDate 저장 중 오류 발생: " + e.getMessage());
        }
        return "성공적으로 저장되었습니다";
    }

    @Override
    public String postWorkByUserId(int userId, LocalDate date) {
        PostUser postUser = new PostUser(userId, date);
        try {
            postUserRepository.save(postUser);
        } catch (DataIntegrityViolationException e) {
            throw new PKDuplicateException("우편근무 저장 중 오류 발생 - 중복된 기본 키:" + e.getMessage() + "하루에 같은 학번의 근무자가 2번 들어갈 수 없습니다. 해당 근무가 필요한 경우 관리자에게 문의하십시오");
        } catch (Exception e) {
            throw new RuntimeException("우편 근무 저장 중 오류가 발생했습니다. 관리자에게 문의하세요" + e.getMessage());
        }
        return "성공적으로 저장되었습니다";
    }

    @Override
    public String exchangePostWorkByUserId(int userId1, LocalDate date, int userId2, LocalDate date2) {
        PostUser postUser1 = postUserRepository.findById((long) userId1).orElseThrow(
                () -> new UserNotFoundException("해당 학번인 조교가 존재하지 않습니다.")
        );
        PostUser postUser2 = postUserRepository.findById((long) userId2).orElseThrow(
                () -> new UserNotFoundException("해당 학번인 조교가 존재하지 않습니다.")
        );
        int tempId = postUser1.getUserId();
        postUser1.setUserId(postUser2.getUserId());
        postUser2.setUserId(tempId);
        try {
            postUserRepository.save(postUser1);
            postUserRepository.save(postUser2);
        } catch (DataIntegrityViolationException e) {
            throw new PKDuplicateException("PostUser 근무교환 저장 중 오류 발생 - 중복된 기본 키: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("PostUser 근무교환 저장 중 오류 발생" + e.getMessage());
        }
        return "우편근무교체가 완료되었습니다.";
    }
}

