package University.Dormitory.domain;

import University.Dormitory.domain.Enum.Dormitory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User implements UserDetails {
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





    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

