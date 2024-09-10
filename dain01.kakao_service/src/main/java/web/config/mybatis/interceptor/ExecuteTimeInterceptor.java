package web.config.mybatis.interceptor;

import static web.AppConstants.QLANG_REQRE_TIME_KEY;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import web.common.util.NullUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StopWatch;

import web.common.model.BaseVO;
import web.common.util.BeanUtil;
import web.common.util.UUIDGenerator;
import web.table.mapper.QlangExecutresultMapper;
import web.table.model.param.QlangExecutresultReg;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project : SCUP
 * @Class : ExecuteTimeInterceptor
 * @Description :
 * @Author : im7015
 * @Since : 2019-10-05
 */
@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
    RowBounds.class, ResultHandler.class})})
public class ExecuteTimeInterceptor implements Interceptor {

    private Properties properties = new Properties();

    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement)args[0];
        String queryId = mappedStatement.getId();

        String queryString = "";

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object returnObject = invocation.proceed();

        stopWatch.stop();

        double totalSeconds = stopWatch.getTotalTimeSeconds();

        if (args.length > 0) {
            Object param = args[1];

            queryString = ((MappedStatement)invocation.getArgs()[0]).getBoundSql(param).getSql();

            if (param instanceof BaseVO) {
                ((BaseVO)param).setQlangReqreTime(String.valueOf(totalSeconds));
            } else if (param instanceof Map) {
                ((Map)param).put(QLANG_REQRE_TIME_KEY, String.valueOf(totalSeconds));
            }
        }

        try {
            if (Double.compare(totalSeconds, 0.5) > 0) {
                QlangExecutresultMapper qlangExecutresultMapper = (QlangExecutresultMapper)BeanUtil.getBean("qlangExecutresultMapper");
                if (qlangExecutresultMapper != null) {

                    QlangExecutresultReg regParam = QlangExecutresultReg.builder()
                            .executId(UUIDGenerator.get())
                            .qlangId(getSimpleQueryId(queryId))
                            .reqreTime(String.valueOf(totalSeconds))
                            .build();

                    qlangExecutresultMapper.insertQlangExecutresult(regParam);
                }
            }
        } catch (Exception e) {
            log.error("질의어 실행결과 등록 실패 : {}", e);
        }

        return returnObject;
    }

    private String getSimpleQueryId(String fullQueryId) {
        int lastIndex = fullQueryId.lastIndexOf(".");
        return fullQueryId.substring(lastIndex + 1);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private String getBindSql(StatementHandler handler) throws NoSuchFieldException, IllegalAccessException {
        BoundSql boundSql = handler.getBoundSql();

        // 쿼리실행시 맵핑되는 파라미터를 구한다
        Object param = handler.getParameterHandler().getParameterObject();
        // 쿼리문을 가져온다(이 상태에서의 쿼리는 값이 들어갈 부분에 ?가 있다)
        String sql = boundSql.getSql();

        // 바인딩 파라미터가 없으면
        if (param == null) {
            sql = sql.replaceFirst("\\?", "''");
            return sql;
        }

        // 해당 파라미터의 클래스가 Integer, Long, Float, Double 클래스일 경우
        if (param instanceof Integer || param instanceof Long || param instanceof Float || param instanceof Double) {
            sql = sql.replaceFirst("\\?", param.toString());
        }
        // 해당 파라미터의 클래스가 String인 경우
        else if (param instanceof String) {
            sql = sql.replaceFirst("\\?", "'" + param + "'");
        }
        // 해당 파라미터의 클래스가 Map인 경우
        else if (param instanceof Map) {
            List<ParameterMapping> paramMapping = boundSql.getParameterMappings();
            for (ParameterMapping mapping : paramMapping) {
                String propValue = mapping.getProperty();
                Object value = ((Map)param).get(propValue);
                if (value == null) {
                    sql = sql.replaceFirst("\\?", "NULL");
                    continue;
                }

                if (value instanceof String) {
                    sql = sql.replaceFirst("\\?", "'" + value + "'");
                } else {
                    sql = sql.replaceFirst("\\?", "'" + value.toString() + "'");
                }
            }
        }
        // 해당 파라미터의 클래스가 사용자 정의 클래스인 경우
        else {
            List<ParameterMapping> paramMapping = boundSql.getParameterMappings();
            Class<? extends Object> paramClass = param.getClass();

            for (ParameterMapping mapping : paramMapping) {
                String propValue = mapping.getProperty();
                Field field = paramClass.getDeclaredField(propValue);
                field.setAccessible(true);
                Class<?> javaType = mapping.getJavaType();

                if (NullUtil.isNull(field.get(param))) {
                    sql = sql.replaceFirst("\\?", "NULL");
                    continue;
                }

                if (String.class == javaType) {
                    sql = sql.replaceFirst("\\?", "'" + field.get(param) + "'");
                } else {
                    sql = sql.replaceFirst("\\?", field.get(param).toString());
                }
            }
        }

        log.info("###############################################");
        log.info(sql);

        return sql;
    }
}
