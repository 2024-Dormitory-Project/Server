package University.Dormitory.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1868149588L;

    public static final QUser user = new QUser("user");

    public final EnumPath<University.Dormitory.domain.Enum.Authority> authority = createEnum("authority", University.Dormitory.domain.Enum.Authority.class);

    public final EnumPath<University.Dormitory.domain.Enum.Dormitory> dormitory = createEnum("dormitory", University.Dormitory.domain.Enum.Dormitory.class);

    public final DatePath<java.time.LocalDate> joinDate = createDate("joinDate", java.time.LocalDate.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final ListPath<WorkDate, QWorkDate> workDates = this.<WorkDate, QWorkDate>createList("workDates", WorkDate.class, QWorkDate.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

