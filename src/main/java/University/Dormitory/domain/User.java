package University.Dormitory.domain;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    private long userId; // ID의 역할. 학번

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Dormitory dormitory;

    @Column(nullable = false)
    private String password; //여기 이름이 들어갈 예정

    @Column(nullable = false)
    private LocalDate joinDate;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Authority authority;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = false)
//    User가 삭제되더라도 해당 User의 근무 기록은 남아있어야 함. orphanRemoval = False로 설정
    private List<WorkDate> workDates = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = false)
    private List<PostUser> postUsers = new ArrayList<>();

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return Integer.toString((int) this.userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.authority.name()));
        return authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", dormitory=" + dormitory +
                ", password='" + password + '\'' +
                ", joinDate=" + joinDate +
                ", name='" + name + '\'' +
                ", authority=" + authority +
                ", workDates=" + workDates +
                '}';
    }
}
