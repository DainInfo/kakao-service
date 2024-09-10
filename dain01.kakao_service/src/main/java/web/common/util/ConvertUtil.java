package web.common.util;

import static web.AppConstants.DEFAULT_CHARSET;
import static web.AppConstants.YYYYMMDD;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;

import com.google.common.primitives.Ints;

import web.AppConstants;
import web.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project : SCUP
 * @Class : ConvertUtil.java
 * @Description :
 * @Author : im7015
 * @Since : 2019. 5. 10
 */
@Slf4j
public class ConvertUtil {

    /**
     * int -> String 변환
     *
     * @param arg int value
     *
     * @return String
     */
    public static String toStr(int arg) {
        return Integer.toString(arg);
    }

    /**
     * long -> String 변환
     *
     * @param arg long value
     *
     * @return String
     */
    public static String toStr(long arg) {
        return Long.toString(arg);
    }

    /**
     * double -> String 변환
     *
     * @param arg double value
     *
     * @return String
     */
    public static String toStr(double arg) {
        return Double.toString(arg);
    }

    /**
     * long -> int 변환
     *
     * @param arg long value
     *
     * @return int
     */
    public static int toInt(long arg) {
        return new Long(arg).intValue();
    }

    /**
     * double -> int 변환
     *
     * @param arg double value
     *
     * @return int
     */
    public static int toInt(double arg) {
        return new Double(arg).intValue();
    }

    /**
     * String -> int 변환
     *
     * @param arg String value
     *
     * @return int
     */
    public static int toInt(String arg) {
        return Optional.ofNullable(arg).map(Ints::tryParse).orElse(0);
    }

    /**
     * Object -> int 변환
     *
     * @param arg Object value
     *
     * @return int
     */
    public static int toInt(Object arg) {
        return ((Double)arg).intValue();
    }

    /**
     * String -> double 변환
     *
     * @param arg String value
     *
     * @return double
     */
    public static double toDbl(String arg) {
        return new Double(arg.trim());
    }

    /**
     * String -> int 변환
     *
     * @param intStr    String value
     * @param initValue 디폴트 value
     *
     * @return int
     */
    public static int intValue(String intStr, int initValue) {
        int retInt = initValue;
        try {
            if (intStr != null && intStr.trim().length() > 0) {
                retInt = Integer.parseInt(intStr.trim());
            }
        } catch (NumberFormatException ignored) {}
        return retInt;
    }

    /**
     * String(yyyyMMdd) -> Date 변환
     *
     * @param arg String value
     *
     * @return Date
     */
    public static Date toDate(String arg) {
        Date date = null;
        SimpleDateFormat transFormat = new SimpleDateFormat(YYYYMMDD);
        try {
            date = transFormat.parse(arg);
        } catch (ParseException ignored) {}

        return date;
    }

    /**
     * Date -> String(yyyyMMdd) 변환
     *
     * @param arg Date value
     *
     * @return String
     */
    public static String DateToStr(Date arg) {
        String date = "";
        SimpleDateFormat transFormat = new SimpleDateFormat(YYYYMMDD);
        try {
            date = transFormat.format(arg);
        } catch (Exception ignored) {}
        return date;
    }

    /**
     * <pre>
     * list 를 배열로
     * </pre>
     *
     * @param list
     *
     * @return
     */
    public static String[] listToArray(List<String> list) {
        return list.toArray(new String[0]);
    }

    /**
     * <pre>
     * Java 오브젝트 객체를 파라미터로, Map<K,V> 컬렉션 맵으로 변환 한다.
     * </pre>
     *
     * @param bean
     *
     * @return
     */
    public static Map<String, Object> toMap(Object bean) {
        return toMap(bean, "class");
    }

    /**
     * <pre>
     * Java 오브젝트 객체를 파라미터로, Map<K,V> 컬렉션 맵으로 변환 한다.
     * </pre>
     *
     * @param bean
     * @param ignoreProperties
     *
     * @return
     */
    public static Map<String, Object> toMap(Object bean, String... ignoreProperties) {
        if (bean == null) {
            return new HashMap<>();
        }
        if (ignoreProperties == null) {
            ignoreProperties = new String[] {"class"};
        }
        Map<String, Object> row = new HashMap<>();
        try {
            PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(bean.getClass());
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                if (ignoreProperty(name, ignoreProperties)) {
                    continue;
                }
                Method getter = descriptor.getReadMethod();
                if (getter == null) {
                    continue;
                }
                String getterName = getter.getName();
                Object value = getter.invoke(bean);
                if (getterName.startsWith("get") || getterName.startsWith("is")) {
                    row.put(name, value);
                }
            }
            return row;
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * <pre>
     * bean 을 map 으로
     * </pre>
     *
     * @param bean
     *
     * @return
     *
     * @throws Exception
     */
    public static Map<String, Object> beanToMap(Object bean) {
        try {
            return org.apache.commons.beanutils.BeanUtils.describe(bean);
        } catch (Exception e) {
            throw new ApplicationException(e, "beanToMap error..");
        }
    }

    /**
     * map 을 bean 으로..
     *
     * @param params
     * @param cls
     *
     * @return
     */
    public static Object mapToBean(Map<String, Object> params, Class cls) {

        Object object;
        try {
            object = cls.newInstance();
        } catch (InstantiationException e) {
            throw new ApplicationException(e, AppConstants.FAIL, "InstantiationException : " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ApplicationException(e, AppConstants.FAIL, "IllegalAccessException : " + e.getMessage());
        }

        ConvertUtil.mapToBean(params, object);

        return object;
    }

    /**
     * <pre>
     * map 을 bean 으로
     * </pre>
     *
     * @param map
     * @param bean
     */
    public static void mapToBean(Map<String, Object> map, Object bean) {
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperties(bean, map);
        } catch (Exception e) {
            throw new ApplicationException(e, "mapToBean error..");
        }
    }

    /**
     * <pre>
     * ignoreProperty
     * </pre>
     *
     * @param name
     * @param ignoreProperties
     *
     * @return
     */
    private static boolean ignoreProperty(String name, String... ignoreProperties) {
        for (String propName : ignoreProperties) {
            if (name.equals(propName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <pre>
     *  byte를 String으로
     * </pre>
     *
     * @param bytes
     *
     * @return
     */
    public static String byteToString(byte[] bytes) {
        return byteToString(bytes, DEFAULT_CHARSET);
    }

    /**
     * <pre>
     * String 를 byte로
     * </pre>
     *
     * @param input
     *
     * @return
     */
    public static byte[] StringToByte(String input) {
        return StringToByte(input, DEFAULT_CHARSET);
    }

    /**
     * <pre>
     * byte를 String으로
     * </pre>
     *
     * @param bytes
     * @param encoding
     *
     * @return
     */
    public static String byteToString(byte[] bytes, String encoding) {
        try {
            return new String(bytes, encoding);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <pre>
     * String 를 byte로
     * </pre>
     *
     * @param input
     * @param encoding
     *
     * @return
     */
    public static byte[] StringToByte(String input, String encoding) {
        try {
            return input.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Object to BigDecimal
     *
     * @param value
     *
     * @return
     */
    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal)value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String)value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger)value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number)value).doubleValue());
            } else {
                throw new ClassCastException(
                    "Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }

    /**
     * BigDecimal to String
     * @param bigDecimal
     * @return
     */
    public static String bigDecimal2str(BigDecimal bigDecimal) {
        return String.valueOf(bigDecimal);
    }

    /**
     * BigDecimal to int : bigDecimal == null 이면 0 을 리턴.
     * @param bigDecimal
     * @return
     */
    public static int bigDecimal2int(BigDecimal bigDecimal) {
        if (NullUtil.isNull(bigDecimal)) {
            return 0;
        }
        return Integer.valueOf(bigDecimal.intValue());
    }

    /**
     * camelToSnake : camel(vo) => snake (db 호출용)
     * @param String
     * @return
     */
    public static String camelToSnake(String str)
    {

        // Empty String
        String result = "";

        // Append first character(in lower case)
        // to result string
        char c = str.charAt(0);
        result = result + Character.toUpperCase(c);

        // Tarverse the string from
        // ist index to last index
        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            // Check if the character is upper case
            // then append '_' and such character
            // (in lower case) to result string
            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result
                        = result
                        + ch ;
            }

            // If the character is lower case then
            // add such character into result string
            else {
                result = result + Character.toUpperCase(ch);
            }
        }

        // return the result
        return result;
    }

}