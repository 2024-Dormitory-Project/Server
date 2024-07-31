package University.Dormitory.web.controller;

import University.Dormitory.Converter.AuthorityConverter;
import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.User;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.MainPageDTO.MainResponseDTO;
import University.Dormitory.web.dto.SignOutDTO.SignOutRequestDTO;
import University.Dormitory.web.dto.SignOutDTO.SignOutResponseDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpResponseDTO;
import University.Dormitory.web.dto.UserDTO.UserRequestDTO;
import University.Dormitory.web.dto.UserDTO.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/perfect")
public class PerfectController {

    private final UserCommandService userCommandService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public SignUpResponseDTO.SignUpDto SignUp(@RequestBody @Valid SignUpRequestDTO.SignUpDto request) {
        log.info("[PostMapping] [SignUp 실행]");
        log.info("회원가입 시도, 권한정보:{}, 기숙사:{}, 이름:{}, 학번:{}", request.getAuthority(), request.getDormitory(), request.getName(),
                request.getUserId());
        User user = userCommandService.SignUp(request);
        return SignUpResponseDTO.SignUpDto.builder()
                .isSuccess(true)
                .message("회원 정보 저장 성공").
                build();
    }

    @PostMapping("/signout")
    public SignOutResponseDTO.SignOutDTO SignOut(@RequestBody @Valid SignOutRequestDTO.SignOutDTO request) {
        log.info("[회원탈퇴 시도], 탈퇴하려는 학번:{}, 탈퇴하려는 이름:{}", request.getUserId(), request.getName());
        if (userCommandService.findUserIdByName(request.getName()).get() != request.getUserId()) {
            throw new UserNotFoundException("학번과 이름이 일치하지 않습니다.");
        }
        userCommandService.SignOut(request.getUserId());
        return SignOutResponseDTO.SignOutDTO
                .builder()
                .isSuccess(true)
                .message("조교가 사라졌습니다")
                .build();
    }

    @DeleteMapping("/changeauthority")
    public MainResponseDTO.Work changeauthority(@RequestBody UserRequestDTO.changeUserAuthorityDto info) {
        Optional<Long> userIdByName = userCommandService.findUserIdByName(info.getName());
        if (userIdByName.isPresent()) {
            if (userCommandService.findUserIdByName(info.getName()).get() != info.getStudentNumber()) {
                throw new UserNotFoundException("학번과 이름이 일치하지 않습니다.");
            }
            Authority authority = AuthorityConverter.toAuthority(info.getAuthority());
            String s = userCommandService.updateAuthorityByUserId(userIdByName.get(), authority);
            return MainResponseDTO.Work.builder()
                    .isSuccess(true)
                    .message(s)
                    .build();
        } else {
            throw new UserNotFoundException("해당 이름의 학번이 존재하지 않습니다");
        }
    }

    @GetMapping("/userinfo")
    public UserResponseDTO.userinfo userinfo(@RequestParam("name") String userName) {
        Long userId = userCommandService.findUserIdByName(userName).orElseThrow(
                () -> new UserNotFoundException("해당 이름을 찾지 못했습니다.")
        );
        User byId = userRepository.findById(userId).orElseThrow();
        return UserResponseDTO.userinfo.builder()
                .authority(byId.getAuthority())
                .dormitory(byId.getDormitory())
                .build();
    }
}