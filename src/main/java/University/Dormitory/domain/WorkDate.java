package University.Dormitory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@AllArgsConstructor
public class WorkDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime breakStartTime;
    private LocalDateTime breakEndTime;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledLeaveTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualLeaveTime;
    private String Reason;

}
