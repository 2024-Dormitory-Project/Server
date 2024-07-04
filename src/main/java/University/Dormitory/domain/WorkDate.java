package University.Dormitory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@AllArgsConstructor
@Setter
public class WorkDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledLeaveTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualLeaveTime;
    private String Reason;

}
