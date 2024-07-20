package University.Dormitory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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
    @JoinColumn(name = "applicant_id")
    private User applicant; //신청한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptor_id")
    private User acceptor;//신청받은 사람

    @Column(nullable = false)
    private LocalDateTime beforeChangeDate;

    private LocalDateTime afterChangeDate;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime timeHappend;

    @Column
    private String type;

}
