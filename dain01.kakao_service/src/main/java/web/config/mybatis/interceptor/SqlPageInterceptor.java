package web.config.mybatis.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;

import web.AppConstants;
import web.common.model.BaseVO;
import web.common.model.OrderInfo;
import web.common.util.NullUtil;
import web.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project : SCUP
 * @Class : SqlPageInterceptor
 * @Description : 페이징 처리 구분을 넣는다.
 * @Author : lkj318
 * @Since : 2019-05-24
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlPageInterceptor implements Interceptor {
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        RowBounds rb = null;
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        Object param = statementHandler.getParameterHandler().getParameterObject();

        //파라미터 없으면 리턴.
        if (StringUtil.isNull(param)) {
            return invocation.proceed();
        }

        if (param instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>)param;
            if(paramMap.containsKey(AppConstants.PAGE_ROW_BOUNDS_KEY)) {
	            rb = (RowBounds)paramMap.get(AppConstants.PAGE_ROW_BOUNDS_KEY);

	            // RowBounds 가 있으면 전체 카운트 쿼리를 실행 한다.
	            if (StringUtil.isNotNull(rb)) {
	                Connection connection = (Connection)invocation.getArgs()[0];
	                String countSql = getBindSql(statementHandler);
	                paramMap.put(AppConstants.PAGE_TOTAL_COUNT_KEY, StringUtil.nvl(getCount(connection, countSql), 0));
	            }
            }
        } else if (!BeanUtils.isSimpleValueType(param.getClass())) { // 사용자 정의 클래스(VO 인경우)

            Class<? extends Object> paramClass = param.getClass();
            try {
                Field field = paramClass.getSuperclass().getDeclaredField(AppConstants.PAGE_ROW_BOUNDS_KEY);

                if (StringUtil.isNotNull(field)) {
                    Object type = field.getType();
                    field.setAccessible(true);
                    // rb = (RowBounds) field.get(paramClass);
                    Method method = paramClass.getSuperclass().getMethod("getRowBounds");
                    rb = (RowBounds)method.invoke(param);

                }

                // RowBounds 가 있으면 전체 카운트 쿼리를 실행 한다.
                Field toTalCountField = paramClass.getSuperclass().getDeclaredField(AppConstants.PAGE_TOTAL_COUNT_KEY);
                if (StringUtil.isNotNull(rb) && NullUtil.isNotNull(toTalCountField)) {
                    Connection connection = (Connection)invocation.getArgs()[0];
                    String countSql = getBindSql(statementHandler);
                    toTalCountField.setAccessible(true);
                    toTalCountField.set(param, StringUtil.nvl(getCount(connection, countSql), 0));
                }
            } catch (NoSuchFieldException e) {
                // 키가 없을 경우 paging 하지 않음.
                log.info("페이징 처리 하지 않음 : ({} 에 {} 존재하지 않음)", paramClass.getName(), AppConstants.PAGE_ROW_BOUNDS_KEY);
            } catch (Exception ex) {

                log.error("SqlPageInterceptor : paging FAIL... {}", ex);
            }
        }
/*
        // 검색조건 시작일자 조회 추가
        if(param instanceof BaseVO) {
            BaseVO baseVO = (BaseVO)param;
            List<String> startDtColumnList = baseVO.getStartDtColumnList();
            if(startDtColumnList != null) {
                Connection connection = (Connection)invocation.getArgs()[0];
                String sql = getBindSql(statementHandler);

                StringBuilder sb = new StringBuilder();
                sb.append("SELECT");
                for(String columnName : startDtColumnList) {
                    if(startDtColumnList.indexOf(columnName) > 0) {
                        sb.append(",");
                    }
                    sb.append(" MIN(NULLIF("+columnName+", '')) "+columnName);
                }
                sb.append(" FROM (");
                sb.append(sql);
                sb.append(" ) ZZ ");
                PreparedStatement preStat = connection.prepareStatement(sb.toString());
                ResultSet rs = preStat.executeQuery();
                if (rs.next()) {
                    Map<String, String> startDtMap = new HashMap<>();
                    for(String columnName : startDtColumnList) {
                        startDtMap.put(columnName, rs.getString(columnName));
                    }
                    Class<? extends Object> paramClass = param.getClass();
                    Field startDtMapField = paramClass.getSuperclass().getDeclaredField(AppConstants.START_DT_MAP_KEY);
                    if (StringUtil.isNotNull(rb) && NullUtil.isNotNull(startDtMapField)) {
                        startDtMapField.setAccessible(true);
                        startDtMapField.set(param, startDtMap);
                    }
                }
            }
        }*/

        log.debug("RowBounds = {}", rb);

        if (NullUtil.isNull(rb) || rb == RowBounds.DEFAULT) { // RowBounds가 없으면 그냥 실행
            return invocation.proceed();
        }

        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        String originalSql = (String)metaStatementHandler.getValue("delegate.boundSql.sql");

        // RowBounds가 있다!
        // 원래 쿼리에 limit 문을 붙여준다.
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ZZ.* ");
        sb.append("FROM (");
        sb.append(originalSql);
        sb.append(" ) ZZ ");
        // 정렬조건 추가
        if(param instanceof BaseVO) {
            BaseVO baseVO = (BaseVO)param;
            if(StringUtil.isNotEmpty(baseVO.getColumn())){
                baseVO.addOrderInfo(baseVO.getColumn(),baseVO.isFlagDesc());
            }
            List<OrderInfo> orderInfoList = baseVO.getOrderInfoList();

            if(orderInfoList != null) {
                sb.append("ORDER BY ");
                for(OrderInfo orderInfo : orderInfoList) {
                    if(orderInfoList.indexOf(orderInfo) > 0) {
                        sb.append(", ");
                    }
                    sb.append(orderInfo.orderColumnName).append(orderInfo.isDesc ? " DESC " : " ");
                }
            }
        }
        sb.append("LIMIT ").append(rb.getLimit()).append(", ").append(rb.getOffset());

        // sb.append("SELECT * ");
        // sb.append("FROM (SELECT ROWNUM AS RNUM, ZZ.* ");
        // sb.append("FROM (");
        // sb.append(originalSql);
        // sb.append(" ) ZZ ");
        // sb.append(") ");
        // sb.append("WHERE RNUM > ").append(rb.getLimit()).append(" AND RNUM <= ").append(rb.getOffset());

        // 페이징시 order by 를 주지 않거나, 인덱스가 타지 않는다면, 뒤죽박죽 데이터가 섞인다.
        // sb.append("SELECT * ");
        // sb.append("FROM (SELECT ROWNUM AS RNUM, ZZ.* ");
        // sb.append("FROM (");
        // sb.append(originalSql);
        // sb.append(" ) ZZ ");
        // sb.append("WHERE ROWNUM <= " + rb.getOffset());
        // sb.append(") ");
        // sb.append("WHERE RNUM > " + rb.getLimit());

        //log.debug("sql = {}", sb.toString());

        // RowBounds 정보 제거
        metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        // 변경된 쿼리로 바꿔치기
        metaStatementHandler.setValue("delegate.boundSql.sql", sb.toString());

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private String getCountSQL(String originalSql) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) AS CNT FROM ( ");
        sb.append(originalSql);
        sb.append(" ) ZZ");
        return sb.toString();
    }

    private int getCount(Connection connection, String originalSql) throws Throwable {
        int count = 0;
        PreparedStatement preStat = connection.prepareStatement(getCountSQL(originalSql));

        ResultSet rs = preStat.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        log.debug("Total count: {}", count);
        return count;
    }

    /**
     * 파라미터가 바인드된 sql을 리턴한다.
     *
     * @param handler
     *
     * @return
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
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