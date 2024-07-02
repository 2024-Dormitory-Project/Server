package University.Dormitory.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAuthorities is a Querydsl query type for UserAuthorities
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAuthorities extends EntityPathBase<UserAuthorities> {

    private static final long serialVersionUID = 579209941L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAuthorities userAuthorities = new QUserAuthorities("userAuthorities");

    public final EnumPath<University.Dormitory.domain.Enum.Authority> authorities = createEnum("authorities", University.Dormitory.domain.Enum.Authority.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QUser user;

    public QUserAuthorities(String variable) {
        this(UserAuthorities.class, forVariable(variable), INITS);
    }

    public QUserAuthorities(Path<? extends UserAuthorities> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAuthorities(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAuthorities(PathMetadata metadata, PathInits inits) {
        this(UserAuthorities.class, metadata, inits);
    }

    public QUserAuthorities(Class<? extends UserAuthorities> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

