package University.Dormitory.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostUser is a Querydsl query type for PostUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostUser extends EntityPathBase<PostUser> {

    private static final long serialVersionUID = 1128378604L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostUser postUser = new QPostUser("postUser");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DatePath<java.time.LocalDate> postWorkDate = createDate("postWorkDate", java.time.LocalDate.class);

    public final QUser user;

    public QPostUser(String variable) {
        this(PostUser.class, forVariable(variable), INITS);
    }

    public QPostUser(Path<? extends PostUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostUser(PathMetadata metadata, PathInits inits) {
        this(PostUser.class, metadata, inits);
    }

    public QPostUser(Class<? extends PostUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

