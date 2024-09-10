package web.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Project : SCUP
 * @Class : NullUtil.java
 * @Description : NullUtil.
 * @Author : im7015
 * @Since : 2019. 5. 10
 */
public class NullUtil {

    private NullUtil() {

    }

    /**
     * <pre>
     * String Empty 확인 null, length != 0 아니면 true
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * <pre>
     * Object Null 확인 null 이면 true
     * </pre>
     *
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj) {
        return (obj == null);
    }

    /**
     * <pre>
     * String Empty 확인 null, length != 0 이면 true
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0));
    }

    /**
     * <pre>
     * List Empty 확인 null, length != 0 이면 true
     * </pre>
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(List<?> value) {
        return ((value == null) || (value.size() == 0));
    }

    /**
     * <pre>
     * Objcet 배열 Empty 확인 null, length != 0 이면 true
     * </pre>
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Object[] value) {
        return ((value == null) || (value.length == 0));
    }

    /**
     * <pre>
     * Map Empty 확인 null, length != 0 이면 true
     * </pre>
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(Map<?, ?> value) {
        return ((value == null) || (value.size() == 0));
    }

    /**
     * <pre>
     * Object Null 확인 null 이 아니면 true
     * </pre>
     *
     * @param obj
     * @return
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * <pre>
     * List Empty 확인
     * </pre>
     *
     * @param value
     * @return
     */
    public static boolean isNotEmpty(List<?> value) {
        return !isEmpty(value);
    }

    /**
     * <pre>
     * Objcet 배열 Empty 확인
     * </pre>
     *
     * @param value
     * @return
     */
    public static boolean isNotEmpty(Object[] value) {
        return !isEmpty(value);
    }

    /**
     * <pre>
     * Map Empty 확인
     * </pre>
     *
     * @param value
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> value) {
        return !isEmpty(value);
    }

    /**
     * <pre>
     * originalStr 확인 Empty 이면 defaultStr 로 변경.
     * </pre>
     *
     * @param originalStr
     * @param defaultStr
     * @return
     */
    public static String nvl(String originalStr, String defaultStr) {
        if ((originalStr == null) || (originalStr.length() < 1)) {
            return defaultStr;
        }
        return originalStr;
    }

    /**
     * <pre>
     * Object 확인 null 이면 defaultStr 로 변경.
     * </pre>
     *
     * @param object
     * @param defaultValue
     * @return
     */
    public static String nvl(Object object, String defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        return nvl(object.toString(), defaultValue);
    }

    /**
     * <pre>
     * Map 의 값이 널이면 ""(스트링널) 로 변경.
     * </pre>
     *
     * @param map
     */
    @SuppressWarnings("rawtypes")
    public static void nullToEmptyString(Map<String, Object> map) {
        if (map != null) {
            Set set = map.entrySet();

            for (Object o : set) {
                Map.Entry e = (Map.Entry)o;
                map.put((String)e.getKey(), e.getValue() == null ? "" : e.getValue());
            }
        }
    }

    /**
     * <pre>
     * List 의 값이 널이면 ""(스트링널) 로 변경.
     * </pre>
     *
     * @param list
     * @return
     */
    public static List<Map<String, Object>> nullToEmptyString(List<Map<String, Object>> list) {
        if (list != null) {
            for (Map<String, Object> map : list) {
                nullToEmptyString(map);
            }
        }
        return list;
    }

    /**
     * <pre>
     * Object 의 값이 널이면 ""(스트링널) 로 변경.
     * </pre>
     *
     * @param obj
     * @return
     */
    public static Object nullToEmptyString(Object obj) {
        return obj == null ? "" : obj;
    }
}