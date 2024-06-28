package University.Dormitory.domain;


import University.Dormitory.domain.Enum.Authority;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Authorities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long authorityId;

    @Column(name = "Authority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToMany(mappedBy = "authorities", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAuthorities> users = new ArrayList<>();
}