package University.Dormitory.service.DormitoryService;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.PostUserRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DormitoryCommandServiceImpl implements DormitoryCommandService {
    private final WorkDateRepository workDateRepository;
    private final PostUserRepository postUserRepository;
    private final CustomRepository customRepository;

    @Override
    public String viewDormitoryWorkers(LocalDate date, Dormitory dormitory) {
        HashMap<String, CustomRepository.WorkTime> userNameDormitoryWorkersByDate = customRepository.findDormitoryWorkersNameByDate(date, dormitory);
        return "DTO 만들어서 return";
    }

    @Override
    public String viewPostWorker(LocalDate date) {
        return null;
    }
}
