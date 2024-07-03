package University.Dormitory.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostUser is a Querydsl query type for PostUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostUser extends EntityPathBase<PostUser> {

    private static final long serialVersionUID = 1128378604L;

    public static final QPostUser postUser = new QPostUser("postUser");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> postWorkDate = createDate("postWorkDate", java.time.LocalDate.class);

    public QPostUser(String variable) {
        super(PostUser.class, forVariable(variable));
    }

    public QPostUser(Path<? extends PostUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostUser(PathMetadata metadata) {
        super(PostUser.class, metadata);
    }

}

