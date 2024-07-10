package University.Dormitory.service.DateService;

import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class DateCommandServiceImpl implements DateCommandService {
    private final WorkDateRepository workDateRepository;
    private final CustomRepository customRepository;

    @Override
    public int workHour(int year, int workMonth, int userId) {
        try {
            return customRepository.countWorkHours(userId, year, workMonth);
        } catch (Exception e) {
            throw new RuntimeException("근무 시간 계산 중 오류가 발생하였습니다." + e.getMessage());
        }
    }

    @Override
    public int workDays(int year, int workMonth, int userId) {
        try {
            return customRepository.countWorkDays(userId, year, workMonth);
        } catch (Exception e) {
            throw new RuntimeException("근무일 계산 중 오류가 발생하였습니다. " + e.getMessage());
        }
    }
}