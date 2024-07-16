package University.Dormitory.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkScheduleChange is a Querydsl query type for WorkScheduleChange
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkScheduleChange extends EntityPathBase<WorkScheduleChange> {

    private static final long serialVersionUID = -285070919L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkScheduleChange workScheduleChange = new QWorkScheduleChange("workScheduleChange");

    public final DateTimePath<java.time.LocalDateTime> afterChangeDate = createDateTime("afterChangeDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> beforeChangeDate = createDateTime("beforeChangeDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> timeHappend = createDateTime("timeHappend", java.time.LocalDateTime.class);

    public final QUser user1;

    public final QUser user2;

    public QWorkScheduleChange(String variable) {
        this(WorkScheduleChange.class, forVariable(variable), INITS);
    }

    public QWorkScheduleChange(Path<? extends WorkScheduleChange> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkScheduleChange(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkScheduleChange(PathMetadata metadata, PathInits inits) {
        this(WorkScheduleChange.class, metadata, inits);
    }

    public QWorkScheduleChange(Class<? extends WorkScheduleChange> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user1 = inits.isInitialized("user1") ? new QUser(forProperty("user1")) : null;
        this.user2 = inits.isInitialized("user2") ? new QUser(forProperty("user2")) : null;
    }

}

