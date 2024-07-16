package University.Dormitory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WorkScheduleChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user1; //신청한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user2;//신청받은 사람

    @Column(nullable = false)
    private LocalDateTime beforeChangeDate;

    private LocalDateTime afterChangeDate;



    @Column(nullable = false)
    private LocalDateTime timeHappend;

}
