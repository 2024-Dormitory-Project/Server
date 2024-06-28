package University.Dormitory.domain;

import University.Dormitory.domain.Enum.Dormitory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    private long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Dormitory dormitory;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAuthorities> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
//    User가 삭제되더라도 해당 User의 근무 기록은 남아있어야 함. orphanRemoval = False로 설정
    private List<WorkDate> workDates = new ArrayList<>();
}

