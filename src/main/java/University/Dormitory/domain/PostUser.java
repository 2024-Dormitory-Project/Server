package University.Dormitory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


// 독자적으로 운영하는 테이블. 원래 근무표 편성 테이블이랑 겹치지 않는다는 뜻
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
@AllArgsConstructor
public class PostUser {
    @Id
    private int userId;

    private String name;

    private LocalDate postWorkDate;
}