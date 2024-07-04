package University.Dormitory.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkDate is a Querydsl query type for WorkDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkDate extends EntityPathBase<WorkDate> {

    private static final long serialVersionUID = 405684928L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkDate workDate = new QWorkDate("workDate");

    public final DateTimePath<java.time.LocalDateTime> actualLeaveTime = createDateTime("actualLeaveTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> actualStartTime = createDateTime("actualStartTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath Reason = createString("Reason");

    public final DateTimePath<java.time.LocalDateTime> scheduledLeaveTime = createDateTime("scheduledLeaveTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> scheduledStartTime = createDateTime("scheduledStartTime", java.time.LocalDateTime.class);

    public final QUser user;

    public QWorkDate(String variable) {
        this(WorkDate.class, forVariable(variable), INITS);
    }

    public QWorkDate(Path<? extends WorkDate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkDate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkDate(PathMetadata metadata, PathInits inits) {
        this(WorkDate.class, metadata, inits);
    }

    public QWorkDate(Class<? extends WorkDate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

