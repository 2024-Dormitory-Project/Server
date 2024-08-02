package University.Dormitory.service.DormitoryService;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.PostUserRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DormitoryCommandServiceImpl implements DormitoryCommandService {
    private final WorkDateRepository workDateRepository;
    private final PostUserRepository postUserRepository;
    private final CustomRepository customRepository;

    @Override
    public MultiValueMap<String, CustomRepository.WorkTime> viewDormitoryWorkers(LocalDate date, Dormitory dormitory) {
        try {
            return customRepository.findDormitoryWorkersNameByDateAndDormitory(date, dormitory);
        }
        catch(Exception e) {
            throw new RuntimeException("기숙사 근무자를 보는 중 오류가 발생했습니다. 관리자에게 문의하세요. : " + e.getMessage());
        }

    }

    @Override
    public Map<Integer, List<String>> viewPostWorker(LocalDate date) {
        Map<Integer, List<String>> postworkes = customRepository.postworkes(date);
        return postworkes;
    }
}
