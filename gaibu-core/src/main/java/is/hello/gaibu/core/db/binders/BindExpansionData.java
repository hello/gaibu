package is.hello.gaibu.core.db.binders;


import is.hello.gaibu.core.models.ExpansionData;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@BindingAnnotation(BindExpansionData.JsonBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindExpansionData {

    public static class JsonBinderFactory implements BinderFactory {
        @Override
        public Binder build(Annotation annotation) {
            return new Binder<BindExpansionData, ExpansionData>() {
                @Override
                public void bind(SQLStatement q, BindExpansionData bind, ExpansionData arg) {
                        q.bind("data", arg.data);
                        q.bind("device_id", arg.deviceId);
                        q.bind("app_id", arg.appId);
                        q.bind("enabled", arg.enabled);
                        q.bind("account_id", arg.accountId.orNull());
                }
            };
        }
    }
}

