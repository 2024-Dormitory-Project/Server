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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/perfect")
@Tag(name="PERFECT-CONTROLLER", description = "[사감용 API], 사감만 접속 가능")
public class PerfectController {

    private final UserCommandService userCommandService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입 API",
            description = "입력한 정보를 바탕으로 회원가입.\n" +
                    "authority는 [사감], [스케줄 관리 조교], [조교]로만 기입.\n" +
                    "dormitory는 [DORMITORY1], [DORMITORY2], [DORMITORY3]로만 기입"
    )
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

    @DeleteMapping("/signout")
    @Operation(
            summary = "탈퇴 API",
            description = "이름이랑 학번이랑 주면 탈퇴"
    )
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

    @PatchMapping("/changeauthority")
    @Operation(
            summary = "권한 변경 API",
            description = "변경할 권한 ([사감], [스케줄 관리 조교], [조교]), 이름, 학번 주면 권한 변경"
    )
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
    @Operation(
            summary = "이름 주면 해당 유저의 정보를 반환합니다.",
            description = "이름,비밀번호,회원가입날짜,권한,기숙사 정보 반환"
    )
    public UserResponseDTO.userinfo userinfo(@RequestParam("name") String userName) {
        Long userId = userCommandService.findUserIdByName(userName).orElseThrow(
                () -> new UserNotFoundException("해당 이름을 찾지 못했습니다.")
        );
        User byId = userRepository.findById(userId).orElseThrow();
        return UserResponseDTO.userinfo.builder()
                .password(byId.getPassword())
                .userId(byId.getUserId())
                .joinDate(byId.getJoinDate())
                .authority(byId.getAuthority())
                .dormitory(byId.getDormitory())
                .build();
    }
}