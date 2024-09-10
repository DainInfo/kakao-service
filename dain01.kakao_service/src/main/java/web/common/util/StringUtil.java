package web.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import web.AppConstants;
import web.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project : SCUP
 * @Class : StringUtil.java
 * @Description : StringUtil.
 * @Author : im7015
 * @Since : 2019. 5. 10
 */
@Slf4j
public class StringUtil {
    protected static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

    private StringUtil() {
    }

    /**
     * limit
     *
     * @param str
     * @param limit
     *
     * @return 변환된 문자열
     *
     * @throws Exception
     */
    public static String shortCut(String str, int limit) throws Exception {

        try {
            if (str == null || limit < 4)
                return str;

            int len = str.length();
            int cnt = 0, index = 0;

            while (index < len && cnt < limit) {
                if (str.charAt(index++) < 256) {
                    cnt++;
                } else {
                    cnt += 2;
                }
            }

            if (index < len)
                str = str.substring(0, index) + "...";

        } catch (Exception e) {
            throw new Exception("[StringUtil][shortCutString]" + e.getMessage(), e);
        }

        return str;

    }

    /**
     * <pre>
     * 소문자 a~z 사이의 램덤 알파벳을 리턴한다.
     * </pre>
     *
     * @param length 생성할 크기.
     *
     * @return
     */
    public static String getRamdomString(int length) {
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)(rnd.nextInt(26) + 97));
        }

        return sb.toString();
    }

    /**
     * @param strTarget
     * @param strSearch
     * @param strReplace
     *
     * @return 변환된 문자열
     *
     * @throws Exception
     */
    public static String replace(String strTarget, String strSearch, String strReplace) {
        // modified by sahjang 070529, use also Util.replace
        if (strTarget == null || strSearch == null) {
            return null;
        }

        String strRep = "";
        if (strReplace != null) {
            strRep = strReplace;
        }
        // modify end

        String result;
        try {

            String strCheck = strTarget;
            StringBuilder strBuf = new StringBuilder();

            while (strCheck.length() != 0) {
                int begin = strCheck.indexOf(strSearch);
                if (begin == -1) {
                    strBuf.append(strCheck);
                    break;
                } else {
                    int end = begin + strSearch.length();
                    strBuf.append(strCheck, 0, begin);
                    strBuf.append(strRep);
                    strCheck = strCheck.substring(end);
                }
            }

            result = strBuf.toString();
        } catch (Exception e) {
            throw new ApplicationException(e, AppConstants.FAIL, "[StringUtil][replace]" + e.getMessage());
        }

        return result;
    }

    /**
     * @param strTarget
     * @param strDelete
     *
     * @return 삭제 후 문자열
     *
     */
    public static String delete(String strTarget, String strDelete) {
        return replace(strTarget, strDelete, "");
    }

    /**
     * <PRE>
     * 숫자를 천단위로 "," 구분자를 추가하여 변환
     * </PRE>
     *
     * @param amount
     *
     * @return 숫자를 천단위로 변환한 결과 ex)"1,234","200,324"
     */
    public static String getAmount(String amount) {

        String convertedAmount = "";
        if (amount != null && amount.length() != 0) {

            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < amount.length(); i++) {
                int j = (amount.length() - (i + 1)) % 3;

                if (i != (amount.length() - 1) && j == 0) {
                    buffer.append(amount.charAt(i));
                    buffer.append(",");
                } else {
                    buffer.append(amount.charAt(i));
                }
            }
            convertedAmount = buffer.toString();
        }

        return convertedAmount;
    }

    /**
     * 전달받은 숫자를 지정된 형태로 출력한다.
     * 숫자가 아닌 값이 들어오면 입력값을 그대로 돌려준다.<BR>
     * <BR>
     * <p>
     * 사용예) getFormattedNumber(1, "00000")<BR>
     * 결과) "00001"<BR>
     * <BR>
     *
     * @param num
     * @param format
     *
     * @return
     */

    public static String getFormmatedNumber(long num, String format) {
        StringBuilder formattedNum = new StringBuilder();
        String strNum = "" + num;

        if (format(format, formattedNum, strNum))
            return strNum;

        return formattedNum.toString();
    }

    /**
     * 전달받은 숫자를 지정된 형태로 출력한다.
     * 숫자가 아닌 값이 들어오면 입력값을 그대로 돌려준다.<BR>
     * <BR>
     * <p>
     * 사용예) getFormattedNumber(1, "00000")<BR>
     * 결 과 ) "00001"<BR>
     * <BR>
     *
     * @param num
     * @param format
     *
     * @return
     */
    public static String getFormatedNumber(int num, String format) {
        StringBuilder formattedNum = new StringBuilder();
        String strNum = "" + num;

        if (format(format, formattedNum, strNum))
            return strNum;

        return formattedNum.toString();
    }

    private static boolean format(String format, StringBuilder formattedNum, String strNum) {
        if (format == null) {
            return true;
        }

        try {
            for (int i = 0; i < format.length() - strNum.length(); i++) {
                formattedNum.append(format.charAt(i));
            }
            formattedNum.append(strNum);
        } catch (Exception ignored) {

        }
        return false;
    }

    public static boolean isDigit(String digitStr) {
        if (null == digitStr || digitStr.length() < 1) {
            return false;
        }

        return digitStr.replaceAll("\\D", "").length() == digitStr.length();
    }

    /**
     * L pad.
     *
     * @param str the str
     * @param len the len
     * @param pad the pad
     * @return the string
     */
    public static String lPad(String str, int len, char pad) {
        return lPad(str, len, pad, false);
    }

    /**
     * L pad.
     *
     * @param str    the str
     * @param len    the len
     * @param pad    the pad
     * @param isTrim the is trim
     * @return the string
     */
    public static String lPad(String str, int len, char pad, boolean isTrim) {
        if (isNull(str)) {
            return null;
        }

        if (isTrim) {
            str = str.trim();
        }

        StringBuilder strBuilder = new StringBuilder(str);
        for (int i = strBuilder.length(); i < len; i++) {
            strBuilder.insert(0, pad);
        }
        str = strBuilder.toString();

        return str;
    }

    /**
     * R pad.
     *
     * @param str the str
     * @param len the len
     * @param pad the pad
     * @return the string
     */
    public static String rPad(String str, int len, char pad) {
        return rPad(str, len, pad, false);
    }

    /**
     * R pad.
     *
     * @param str    the str
     * @param len    the len
     * @param pad    the pad
     * @param isTrim the is trim
     * @return the string
     */
    public static String rPad(String str, int len, char pad, boolean isTrim) {
        if (isNull(str)) {
            return null;
        }

        if (isTrim) {
            str = str.trim();
        }

        StringBuilder strBuilder = new StringBuilder(str);
        for (int i = strBuilder.length(); i < len; i++) {
            strBuilder.append(pad);
        }
        str = strBuilder.toString();

        return str;
    }

    /**
     * 문자열을 원하는 길이만큼 지정한 문자로 padding 처리한다.
     *
     * @param origin padding 처리할 문자열
     * @param limit  padding 처리할 범위
     * @param pad    padding 될 문자
     *
     * @return padding 처리된 문자열
     */
    public static String padding(String origin, int limit, String pad) {

        String originStr = "";
        if (origin != null) {
            originStr = origin;
        }

        String padStr = "";
        if (pad != null) {
            padStr = pad;
        }

        int size = Objects.requireNonNull(origin).length();

        if (limit <= size) {
            return originStr;

        } else {
            StringBuilder sb = new StringBuilder(originStr);

            for (int inx = size; inx < limit; inx++) {
                sb.append(padStr);
            }

            return sb.toString();
        }
    }

    /**
     * 문자열을 원하는 길이만큼 지정한 문자로 left padding 처리한다.
     *
     * @param origin padding 처리할 문자열
     * @param limit  padding 처리할 size
     * @param pad    padding 될 문자
     *
     * @return padding 처리된 문자열
     */
    public static String leftPadding(String origin, int limit, String pad) {
        String temp = pad;
        if (pad == null) {
            temp = "";
        }

        String originStr = "";
        if (origin != null) {
            originStr = origin;
        }

        int size = originStr.length();

        if (limit <= size) {
            return originStr;

        } else {
            StringBuilder sb = new StringBuilder(temp);

            for (int inx = size + 1; inx < limit; inx++) {
                sb.append(temp);
            }

            return sb.append(originStr).toString();
        }
    }

    /**
     * 해당문자열 중 특정 문자열을 치환한다. <BR>
     * <BR>
     * <p>
     * 사용예) replaceSecOutput("&<>#\'" )<BR>
     * 결 과 ) &amp;&lt;&gt;&#35;&quot;&#39;<BR>
     * <BR>
     *
     * @param pInstr String
     *
     * @return String 치환된 문자열
     */
    public static String replaceSecOutput(String pInstr) {

        if (pInstr == null || pInstr.equals("")) {
            return "";
        }

        String result = pInstr;
        try {
            result = replace(result, "&", "&amp;");
            result = replace(result, "<", "&lt;");
            result = replace(result, ">", "&gt;");
            result = replace(result, "#", "&#35;");
            result = replace(result, "\"", "&quot;");
            result = replace(result, "'", "&#39;");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return pInstr;
        }

        return result;
    }

    /**
     * 해당문자열 중 특정 문자열을 치환한다. <BR>
     * <BR>
     * <p>
     * 사용예) replaceSecOutput("&<>#\'" )<BR>
     * 결 과 ) &amp;&lt;&gt;&#35;&quot;&#39;<BR>
     * <BR>
     *
     * @param pInstr String
     *
     * @return String 치환된 문자열
     */
    public static String replaceSecXmlKeyOutput(String pInstr) {

        if (pInstr == null || pInstr.equals("")) {
            return "_";
        }

        String result = pInstr;
        if (!isValidXmlKey(pInstr)) {
            result = pInstr.trim();
            result = result.substring(0, 1).replaceFirst("[^a-zA-Z_]", "_")
                + result.substring(1).replaceAll("[^a-zA-Z0-9_.-]", "_");
        }

        return result;
    }

    /**
     * XML 엘리먼트 명으로 합당한지 여부 체크
     * 1. 엘리먼트는 전체가 문자, 숫자, 특수기호(., _, -)로만 이루어져야 한다.
     * 2. 첫 시작은 문자, _ 만이 가능하다. (공백 안 됨) 편의상 영문자로 제한을 한다.
     *
     * @param pInstr
     *
     * @return
     */
    public static boolean isValidXmlKey(String pInstr) {
        if (pInstr == null || pInstr.trim().length() < 1) {
            return false;
        }

        return pInstr.matches("[a-zA-Z_][a-zA-Z0-9_.-]*");
    }

    /**
     * 해당문자열 중 특정 문자열을 Javascript에서 사용할수 있도록 치환한다. <BR>
     * <BR>
     * <p>
     * 사용예) replaceJSEncodeOutput(""'" )<BR>
     * 결 과 ) %22%27<BR>
     * <BR>
     *
     * @param pInstr String
     *
     * @return String 치환된 문자열
     */
    public static String replaceJSEncodeOutput(String pInstr) {

        if (pInstr == null || pInstr.equals("")) {
            return "";
        }

        String result = pInstr;

        try {
            result = replace(result, "\"", "%22");
            result = replace(result, "'", "%27");
        } catch (Exception e) {
            return pInstr;
        }

        return result;
    }

    /**
     * 영문을 한글로 Conversion해주는 Method.
     * (8859_1 --> KSC5601)
     *
     * @param english 한글로 바꾸어질 영문 String
     *
     * @return 한글로 바꾸어진 String
     */
    public static synchronized String koreanForPortal(String english) {
        String korean;

        if (english == null) {
            return null;
        }

        try {
            korean = new String(english.getBytes("8859_1"), "euc-kr");
        } catch (UnsupportedEncodingException e) {
            korean = english;
        }
        return korean;
    }

    /**
     * 숫자 앞의 빈칸을 자릿수만큼 메꿔주는 Method.
     * ex) blankToString( "1", 4, "0");
     * "1" -> "0001"
     *
     * @param orig   빈칸 채우기 전의 본래 String
     * @param length 문자의 자릿수 int
     * @param add    빈칸을 채울 문자 String
     *
     * @return 빈칸이 채워진 String
     */
    public static String blankToString(String orig, int length, String add) {
        if (orig == null) {
            orig = "";
        }
        int space = length - orig.length();

        int i;

        StringBuilder buf = new StringBuilder();

        for (i = 0; i < space; i++)

            buf.append(add);

        orig = buf + orig;

        return orig;

    }

    /**
     * 숫자 앞의 빈칸을 자릿수만큼 0으로 메꿔주는 Method.
     * ex) blankToZero( "1", 4 );
     * "1" -> "0001"
     *
     * @param orig   빈칸 채우기 전의 본래 String
     * @param length 문자의 자릿수 int
     *
     * @return 빈칸이 채워진 String
     */
    public static String blankToZero(String orig, int length) {
        String num;
        num = blankToString(orig, length, "0");

        return num;

    }

    /**
     * String 배열 객체를 toString 하는 메소드
     * ex) [AAA,BBB,CCC]
     *
     * @param inAraayStr 출력할 String 배열
     *
     * @return toString
     */
    public static String toArrayString(String[] inAraayStr) {
        String result = null;
        if (inAraayStr != null) {
            StringBuilder sb = new StringBuilder();

            sb.append("[");
            for (int i = 0; i < inAraayStr.length; i++) {
                sb.append(inAraayStr[i]);
                if (i < inAraayStr.length - 1)
                    sb.append(",");
            }
            sb.append("]");

            result = sb.toString();
        }

        return result;
    }

    /**
     * int 배열 객체를 toString 하는 메소드
     * ex) [AAA,BBB,CCC]
     *
     * @param inAraayInt 출력할 I 배열
     *
     * @return toString
     */
    public static String toArrayString(int[] inAraayInt) {
        String result = null;
        if (inAraayInt != null) {
            StringBuilder sb = new StringBuilder();

            sb.append("[");
            for (int i = 0; i < inAraayInt.length; i++) {
                sb.append(inAraayInt[i]);
                if (i < inAraayInt.length - 1)
                    sb.append(",");
            }
            sb.append("]");

            result = sb.toString();
        }

        return result;
    }

    /**
     * 파일명에서 extension을 제거한 이름만을 가져온다.
     *
     * @param fileName
     *
     * @return
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    /**
     * 파일명에서 확장자만 return 한다.
     *
     * @param fileName
     *
     * @return
     */
    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    /**
     * 비어 있으면 null String 을 리턴한다.
     *
     * @param str
     *
     * @return
     */
    public static String nvl(String str) {
        if ((str == null) || (str.equals("")) || (str.equals("null")) || (str.length() == 0)) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 비어 있으면 defaultValue 을 리턴한다.
     *
     * @param str
     * @param defaultValue
     *
     * @return
     */
    public static String nvl(String str, String defaultValue) {

        if ((str == null) || (str.equals("")) || (str.length() == 0)) {
            return defaultValue;
        } else {

            str = str.trim(); // 공백제거 한다.
            return str;
        }
    }

    /**
     * 비어 있으면 defaultValue 을 리턴한다.
     *
     * @param str
     * @param defaultValue
     *
     * @return
     */
    public static int nvl(String str, int defaultValue) {
        if ((str == null) || (str.equals("")) || (str.length() == 0)) {
            return defaultValue;
        } else {
            return Integer.parseInt(str);
        }
    }

    /**
     * 비어 있으면 defaultValue 을 리턴한다.
     *
     * @param str
     * @param defaultValue
     *
     * @return
     */
    public static long nvl(String str, long defaultValue) {
        if ((str == null) || (str.equals("")) || (str.length() == 0)) {
            return defaultValue;
        } else {
            return Long.parseLong(str);
        }
    }

    /**
     * 비어 있으면 defaultValue 을 리턴한다.
     *
     * @param val
     * @param defaultValue
     *
     * @return
     */
    public static int nvl(Integer val, int defaultValue) {
        if (val == null)
            return defaultValue;
        else
            return val;
    }

    /**
     * 해당 Object를 String으로 형변형하여 리턴한다.
     * <p>
     *
     * @param obj 변환할 객체
     *
     * @return 변환된 문자열
     */
    public static String nvl(Object obj) {
        String str;

        if (obj == null)
            str = "";
        else {
            try {
                str = (String)obj;
            } catch (Exception e) {
                try {
                    str = obj.toString();
                } catch (Exception ex) {
                    str = "";
                }
            }
        }

        if (str.equals("null") || (str.length() == 0)) {
            str = "";
        }

        return str;
    }

    public static String nvl(Object obj, String nullvalue) {
        String str;

        if (obj == null)
            str = nullvalue;

        else {
            try {
                str = (String)obj;
            } catch (Exception e) {
                try {
                    str = obj.toString();
                } catch (Exception ex) {
                    str = nullvalue;
                }
            }
        }

        return str;
    }

    /**
     * 입력값이 null이거나 ""이면 true를 리턴한다.
     *
     * @param p
     *
     * @return
     */
    public static boolean isEmpty(String p) {
        return (p == null || p.trim().equals(""));
    }

    /**
     * 입력값이 null이 아니거나 str과 같으면 true를 리턴한다.
     *
     * @param p   입력값
     * @param str 비교값
     *
     * @return
     */
    public static boolean isEqualsStr(Object p, String str) {
        return str.equals(p);
    }

    /**
     * TextArea에서 입력받은 캐리지 리턴값을 <BR>태그로 변환
     *
     * @param comment
     *
     * @return
     */
    public static String nl2br(String comment) {
        int length = comment.length();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < length; ++i) {
            String comp = comment.substring(i, i + 1);
            if ("\r".compareTo(comp) == 0) {
                comp = comment.substring(++i, i + 1);
                if ("\n".compareTo(comp) == 0)
                    buffer.append("<BR>\r");
                else
                    buffer.append("\r");
            }

            buffer.append(comp);
        }
        return buffer.toString();
    }

    /**
     * 14자리의 String 형식 날짜(20050401142323)을 date형식으로 return한다.
     *
     * @param str
     *
     * @return
     */
    public static Date getDateFrom14LengthString(String str) {
        Date aDate;

        if (str.length() != 14) {
            throw new ApplicationException(AppConstants.FAIL, "Invalid parameter length");
        }

        try {
            Long.parseLong(str);
        } catch (Exception e) {
            return null;
        }

        String YYYY = str.substring(0, 4);
        String MM = str.substring(4, 6);
        String DD = str.substring(6, 8);
        String HH = str.substring(8, 10);
        String mi = str.substring(10, 12);
        String SS = str.substring(12, 14);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(YYYY));
        calendar.set(Calendar.MONTH, Integer.parseInt(MM) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(DD));
        calendar.set(Calendar.HOUR, Integer.parseInt(HH));
        calendar.set(Calendar.MINUTE, Integer.parseInt(mi));
        calendar.set(Calendar.SECOND, Integer.parseInt(SS));

        aDate = calendar.getTime();
        return aDate;
    }

    /**
     * 한글고려한 ..붙이기    ex) cutBytes("우리1234", 3, "...") ==> "우리1..."
     *
     * @param string
     * @param maxSize
     * @param re
     *
     * @return
     */
    public static String cutString(String string, int maxSize, String re) {

        if (string == null)
            return "";

        int tLen = string.length();
        int count = 0;
        char c;
        int s;
        for (s = 0; s < tLen; s++) {
            c = string.charAt(s);
            if (count > maxSize)
                break;
            if (c > 127)
                count += 2;
            else
                count++;
        }
        return (tLen > s) ? string.substring(0, s) + re : string;
    }

    /**
     * 한글 바이트 자르기...
     *
     * UTF-8일 경우
     *
     * subStrBytes("블라블라블라라", 10, "UTF-8");
     *
     * EUC-KR일 경우
     *
     * subStrBytes("블라블라블라라", 10, "EUC-KR");
     *
     * 출처: http://egloos.zum.com/lemonfish/v/5798357
     *
     * @param data
     * @param maxBytes
     * @param encoding
     * @return
     */
    public static String subStrBytes(String data, int maxBytes, String encoding) {
        if (data == null || data.length() == 0 || maxBytes < 1) {
            return "";
        }
        Charset CS = Charset.forName(encoding);
        CharBuffer cb = CharBuffer.wrap(data);
        ByteBuffer bb = ByteBuffer.allocate(maxBytes);
        CharsetEncoder enc = CS.newEncoder();
        enc.encode(cb, bb, true);
        bb.flip();
        return CS.decode(bb).toString();
    }

    /**
     * Checks if is null.
     *
     * @param str the str
     *
     * @return true, if is null
     */
    public static boolean isNull(String str) {

        if (str != null) {
            str = str.trim();
        }

        return (str == null || "".equals(str));
    }

    /**
     * Checks if is not null.
     *
     * @param str the str
     *
     * @return true, if is null
     */
    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    /**
     * Checks if is null.
     *
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj) {
        return (obj == null);
    }

    /**
     * Checks if is not null.
     *
     * @param obj
     * @return
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * Integer2string.
     *
     * @param integer the integer
     *
     * @return the string
     */
    public static String integer2string(int integer) {
        return ("" + integer);
    }

    /**
     * Long2string.
     *
     * @param longdata the longdata
     *
     * @return the string
     */
    public static String long2string(long longdata) {
        return String.valueOf(longdata);
    }

    /**
     * Float2string.
     *
     * @param floatdata the floatdata
     *
     * @return the string
     */
    public static String float2string(float floatdata) {
        return String.valueOf(floatdata);
    }

    /**
     * Double2string.
     *
     * @param doubledata the doubledata
     *
     * @return the string
     */
    public static String double2string(double doubledata) {
        return String.valueOf(doubledata);
    }

    /**
     * Null2void.
     *
     * @param str the str
     *
     * @return the string
     */
    public static String null2void(String str) {

        if (isNull(str)) {
            str = "";
        }

        return str;
    }

    /**
     * String2integer.
     *
     * @param str the str
     *
     * @return the int
     */
    public static int string2integer(String str) {

        if (isNull(str)) {
            return 0;
        }

        return Integer.parseInt(str);
    }

    /**
     * String2float.
     *
     * @param str the str
     *
     * @return the float
     */
    public static float string2float(String str) {

        if (isNull(str)) {
            return 0.0F;
        }

        return Float.parseFloat(str);
    }

    /**
     * String2double.
     *
     * @param str the str
     *
     * @return the double
     */
    public static double string2double(String str) {

        if (isNull(str)) {
            return 0.0D;
        }

        return Double.parseDouble(str);
    }

    /**
     * String2long.
     *
     * @param str the str
     *
     * @return the long
     */
    public static long string2long(String str) {

        if (isNull(str)) {
            return 0L;
        }

        return Long.parseLong(str);
    }

    /**
     * Null2string.
     *
     * @param str          the str
     * @param defaultValue the default value
     *
     * @return the string
     */
    public static String null2string(String str, String defaultValue) {

        if (isNull(str)) {
            return defaultValue;
        }

        return str;
    }

    /**
     * String2integer.
     *
     * @param str          the str
     * @param defaultValue the default value
     *
     * @return the int
     */
    public static int string2integer(String str, int defaultValue) {

        if (isNull(str)) {
            return defaultValue;
        }

        return Integer.parseInt(str);
    }

    /**
     * String2float.
     *
     * @param str          the str
     * @param defaultValue the default value
     *
     * @return the float
     */
    public static float string2float(String str, float defaultValue) {

        if (isNull(str)) {
            return defaultValue;
        }

        return Float.parseFloat(str);
    }

    /**
     * String2double.
     *
     * @param str          the str
     * @param defaultValue the default value
     *
     * @return the double
     */
    public static double string2double(String str, double defaultValue) {

        if (isNull(str)) {
            return defaultValue;
        }

        return Double.parseDouble(str);
    }

    /**
     * String2long.
     *
     * @param str          the str
     * @param defaultValue the default value
     *
     * @return the long
     */
    public static long string2long(String str, long defaultValue) {

        if (isNull(str)) {
            return defaultValue;
        }

        return Long.parseLong(str);
    }

    /**
     * Equals.
     *
     * @param source the source
     * @param target the target
     *
     * @return true, if successful
     */
    public static boolean equals(String source, String target) {

        return null2void(source).equals(null2void(target));

    }

    /**
     * To sub string.
     *
     * @param str        the str
     * @param beginIndex the begin index
     * @param endIndex   the end index
     *
     * @return the string
     */
    public static String toSubString(String str, int beginIndex, int endIndex) {

        if (equals(str, "")) {
            return str;
        } else if (str.length() < beginIndex) {
            return "";
        } else if (str.length() < endIndex) {
            return str.substring(beginIndex);
        } else {
            return str.substring(beginIndex, endIndex);
        }

    }

    /**
     * To sub string.
     *
     * @param source     the source
     * @param beginIndex the begin index
     *
     * @return the string
     */
    public static String toSubString(String source, int beginIndex) {

        if (equals(source, "")) {
            return source;
        } else if (source.length() < beginIndex) {
            return "";
        } else {
            return source.substring(beginIndex);
        }

    }

    /**
     * Search.
     *
     * @param source the source
     * @param target the target
     *
     * @return the int
     */
    public static int search(String source, String target) {
        int result = 0;
        String strCheck = source;
        for (int i = 0; i < source.length(); ) {
            int loc = strCheck.indexOf(target);
            if (loc == -1) {
                break;
            } else {
                result++;
                i = loc + target.length();
                strCheck = strCheck.substring(i);
            }
        }
        return result;
    }

    /**
     * Trim.
     *
     * @param str the str
     *
     * @return the string
     */
    public static String trim(String str) {
        if (StringUtil.isEmpty(str)) {
            return "";
        }
        return str.trim();
    }

    /**
     * Ltrim.
     *
     * @param str the str
     *
     * @return the string
     */
    public static String ltrim(String str) {

        int index = 0;

        while (' ' == str.charAt(index++))
            ;

        if (index > 0)
            str = str.substring(index - 1);

        return str;
    }

    /**
     * Rtrim.
     *
     * @param str the str
     *
     * @return the string
     */
    public static String rtrim(String str) {

        int index = str.length();

        while (' ' == str.charAt(--index))
            ;

        str = str.substring(0, index + 1);

        return str;
    }

    /**
     * 왼쪽에 특정문자 붙이기
     *
     * @param str
     * @param pad
     * @param len
     *
     * @return
     */
    public static String lPad(String str, String pad, int len) {
        return lPad(str, pad, len, false);
    }

    /**
     * 왼쪽에 특정문자 붙이기
     *
     * @param str
     * @param pad
     * @param len
     * @param isTrim
     *
     * @return
     */
    public static String lPad(String str, String pad, int len, boolean isTrim) {

        if (isNull(str)) {
            return null;
        }

        if (isTrim) {
            str = str.trim();
        }

        StringBuilder strBuilder = new StringBuilder(str);
        for (int i = strBuilder.length(); i < len; i++) {
            strBuilder.insert(0, pad);
        }
        str = strBuilder.toString();

        return str;
    }

    /**
     * 오른쪽에 특정문자 붙이기
     *
     * @param str
     * @param pad
     * @param len
     *
     * @return
     */
    public static String rPad(String str, String pad, int len) {
        return rPad(str, pad, len, false);
    }

    /**
     * 오른쪽에 특정문자 붙이기
     *
     * @param str    the str
     * @param len    the len
     * @param pad    the pad
     * @param isTrim the is trim
     *
     * @return the string
     */
    public static String rPad(String str, String pad, int len, boolean isTrim) {

        if (isNull(str)) {
            return null;
        }

        if (isTrim) {
            str = str.trim();
        }

        StringBuilder strBuilder = new StringBuilder(str);
        for (int i = strBuilder.length(); i < len; i++) {
            strBuilder.append(pad);
        }
        str = strBuilder.toString();

        return str;
    }

    /**
     * 첫자 대문자로
     *
     * @param str the str
     *
     * @return the string
     */
    public static String capitalize(String str) {
        return !isNull(str) ? str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase() : str;
    }

    /**
     * Checks if is pattern match.
     *
     * @param str     the str
     * @param pattern the pattern
     *
     * @return true, if is pattern match
     */
    public static boolean isPatternMatch(String str, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(str);

        return matcher.matches();
    }

    /**
     * To eng.
     *
     * @param kor the kor
     *
     * @return the string
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static String toEng(String kor) throws UnsupportedEncodingException {

        if (isNull(kor)) {
            return null;
        }

        return new String(kor.getBytes("KSC5601"), "8859_1");

    }

    /**
     * To kor.
     *
     * @param en the en
     *
     * @return the string
     *
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public static String toKor(String en) throws UnsupportedEncodingException {

        if (isNull(en)) {
            return null;
        }

        return new String(en.getBytes("8859_1"), "euc-kr");
    }

    /**
     * Count of.
     *
     * @param str        the str
     * @param charToFind the char to find
     *
     * @return the int
     */
    public static int countOf(String str, String charToFind) {
        int findLength = charToFind.length();
        int count = 0;

        for (int idx = str.indexOf(charToFind); idx >= 0; idx = str.indexOf(charToFind, idx + findLength)) {
            count++;
        }

        return count;
    }

    /**
     * String 를 배열 형태로 리턴한다.
     *
     * @param str
     * @param strToken
     *
     * @return
     */
    public static String[] getStringArray(String str, String strToken) {
        if (str.contains(strToken)) {
            StringTokenizer st = new StringTokenizer(str, strToken);
            String[] stringArray = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++) {
                stringArray[i] = st.nextToken();
            }
            return stringArray;
        }
        return new String[] {str};
    }

    /**
     * 비어있지 않으면 true
     *
     * @param str
     *
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * @param str
     * @param pattern
     *
     * @return
     */
    public static boolean isPatternMatching(String str, String pattern) {
        if (pattern.indexOf('*') >= 0) {
            pattern = pattern.replaceAll("\\*", ".*");
        }
        pattern = "^" + pattern + "$";
        return Pattern.matches(pattern, str);
    }

    /**
     * It returns true if string contains a sequence of
     * the same character.
     *
     * <pre>
     * StringUtil.containsMaxSequence('password', '2')  = true
     * StringUtil.containsMaxSequence('my000', '3')     = true
     * StringUtil.containsMaxSequence('abbbbc', '5')    = false
     * </pre>
     *
     * @param str          original String
     * @param maxSeqNumber a sequence of the same character
     *
     * @return which contains a sequence of the same
     * character
     */
    public static boolean containsMaxSequence(String str, String maxSeqNumber) {
        int occurence = 1;
        int max = string2integer(maxSeqNumber);
        if (str == null) {
            return false;
        }

        int sz = str.length();
        for (int i = 0; i < (sz - 1); i++) {
            if (str.charAt(i) == str.charAt(i + 1)) {
                occurence++;

                if (occurence == max)
                    return true;
            } else {
                occurence = 1;
            }
        }
        return false;
    }

    /**
     * <p>
     * Checks that the String contains certain
     * characters.
     * </p>
     * <p>
     * A <code>null</code> String will return
     * <code>false</code>. A <code>null</code> invalid
     * character array will return <code>false</code>.
     * An empty String ("") always returns false.
     * </p>
     *
     * @param str          the String to check, may be null
     * @param invalidChars an array of invalid chars, may be null
     *
     * @return false if it contains none of the invalid
     * chars, or is null
     */

    public static boolean containsInvalidChars(String str, char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return false;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (char invalidChar : invalidChars) {
                if (invalidChar == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Checks that the String contains certain
     * characters.
     * </p>
     * <p>
     * A <code>null</code> String will return
     * <code>false</code>. A <code>null</code> invalid
     * character array will return <code>false</code>.
     * An empty String ("") always returns false.
     * </p>
     *
     * @param str          the String to check, may be null
     * @param invalidChars a String of invalid chars, may be null
     *
     * @return false if it contains none of the invalid
     * chars, or is null
     */
    public static boolean containsInvalidChars(String str, String invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        return containsInvalidChars(str, invalidChars.toCharArray());
    }

    /**
     * 숫자 형태 인지 판단한다.
     *
     * @param str
     *
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        if (sz == 0)
            return false;
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Make a new String that filled original to a
     * special char as cipers.
     *
     * @param originalStr original String
     * @param ch          a special char
     * @param cipers      cipers
     *
     * @return filled String
     */
    public static String fillString(String originalStr, char ch, int cipers) {
        int originalStrLength = originalStr.length();

        if (cipers < originalStrLength)
            return null;

        int difference = cipers - originalStrLength;

        StringBuilder strBuf = new StringBuilder();
        for (int i = 0; i < difference; i++)
            strBuf.append(ch);

        strBuf.append(originalStr);
        return strBuf.toString();
    }

    /**
     * InputStream 읽어 String 리턴.
     *
     * @param is
     *
     * @return
     *
     * @throws IOException
     */
    public static String getStringFromInputStream(InputStream is) throws IOException {

        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "EUC-KR"))) {

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();

    }

    /**
     * <pre>
     * 포맷된 데이터를 리턴한다.
     * </pre>
     *
     * @param formatString
     * @param strings
     *
     * @return
     */
    public static String getStringFormat(String formatString, String[] strings) {
        String strFormatData = "";
        if (!isNull(formatString) && strings.length > 0) {
            strFormatData = MessageFormat.format(formatString, strings);
        }
        return strFormatData;
    }

    /**
     * <pre>
     * class 의 필드의 값을 String[] 로 리턴한다.
     * </pre>
     *
     * @param cls
     *
     * @return
     *
     * @throws Exception
     */
    public static <T> String[] getField(T cls) throws Exception {
        List<String> list = new ArrayList<>();
        Field[] fields = cls.getClass().getFields();

        for (Field field : fields) {
            if (NullUtil.isNull(field.get(cls))) {
                list.add("");
            } else {
                list.add((String)field.get(cls));
            }
        }
        return ConvertUtil.listToArray(list);
    }

    // /**
    // * <pre>
    // * 패스워드 암호화
    // * </pre>
    // *
    // * @param password
    // * @return
    // */
    // public static String encodePassword(String password) {
    // ShaPasswordEncoder shaEncoder = new ShaPasswordEncoder(256);
    // return shaEncoder.encodePassword(password, null);
    // }

    /**
     * <pre>
     * 이미지의 fullPath를 추출한다.
     * </pre>
     *
     * @param context
     *
     * @return
     */
    public static List<String> getImageSrcList(String context) {
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
        Matcher matcher = pattern.matcher(context);

        List<String> imageSrcList = new ArrayList<>();

        while (matcher.find()) {
            imageSrcList.add(matcher.group(1));
        }

        return imageSrcList;
    }

    /**
     * <pre>
     * 이미지의 src 에 특정 문자를 제거후 리턴
     * </pre>
     *
     * @param context
     * @param delString
     *
     * @return
     *
     */
    public static List<String> getImageSrcList(String context, String delString) {
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
        Matcher matcher = pattern.matcher(context);

        List<String> imageSrcList = new ArrayList<>();

        while (matcher.find()) {
            imageSrcList.add(replace(matcher.group(1), delString, ""));
        }

        return imageSrcList;
    }

    /**
     * <pre>
     * 이메일 주소 마스킹 처리
     * </pre>
     *
     * @param email
     *
     * @return maskedEmailAddress
     */
    @SuppressWarnings("Annotator")
    public static String getMaskedEmail(String email) {
        /*
         * 요구되는 메일 포맷
         * {userId}@domain.com
         */
        String regex = "\\b(\\S+)+@(\\S+.\\S+)";
        Matcher matcher = Pattern.compile(regex).matcher(email);
        if (matcher.find()) {
            String id = matcher.group(1); // 마스킹 처리할 부분인 userId
            /*
             * userId의 길이를 기준으로 최소 마스킹 길이를 제외한 앞글자 길이가
             * 앞글자 길이가 세글자 이상인 경우 세글자 뒤에 모든 글자를 마스킹 처리하고,
             * 앞글자 길이가 세글자 미만인 경우 앞글자 뒤에 모두 마스킹 처리하고,
             * 앞글자 길이가 0보다 작거나 같을 경우 최소 마스킹 길이만큼 마스킹 처리
             */
            int MIN_MASK = 4; // 최소 마스킹 길이
            int length = id.length() - MIN_MASK;
            if (length <= 0) {
                char[] c = new char[MIN_MASK];
                Arrays.fill(c, '*');
                return email.replace(id, String.valueOf(c));
            } else if (length < 3) {
                return email.replaceAll("(?<=.{" + length + "}).(?=[^@]*?@)", "*");
            } else {
                return email.replaceAll("(?<=.{3}).(?=[^@]*?@)", "*");
            }
        }
        return email;
    }

    /**
     * <pre>
     * 이름 마스킹 처리
     * </pre>
     *
     * @param name
     *
     * @return maskedName
     */
    public static String getMaskedName(String name) {
        /*
         * name의 길이를 기준으로 세글자 이상인 경우 가운데 글자를 마스킹 처리하고,
         * 두글자인 경우 마지막 글자를 마스킹 처리
         */
        int length = name.length();
        if (length == 2) {
            return name.replaceAll("(?<=.).", "O");
        } else if (length > 2) {
            return name.replaceAll("(?<=.).(?=.)", "O");
        }
        return name;
    }

    /**
     * 숫자를 대문자 알파벳으로..
     * ex) 0 -> A, 1 -> B ...
     * @param i
     * @return
     */
    public static String toAlphabetic(int i) {
        if (i < 0) {
            return "-" + toAlphabetic(-i - 1);
        }

        int quot = i / 26;
        int rem = i % 26;
        char letter = (char)((int)'A' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return toAlphabetic(quot - 1) + letter;
        }
    }

    /*
        double diffLatitude = LatitudeInDifference(500);
        double diffLongitude = LongitudeInDifference(37.51937, 500);

        txtLatitude.setText((37.51937-diffLatitude)+" ~ "+(37.51937+diffLatitude));
        txtLongitude.setText((126.940114-diffLongitude)+" ~ "+(126.940114+diffLongitude));
     */
    //반경 m이내의 위도차(degree)
    public static double LatitudeInDifference(int diff){
        //지구반지름
        final int earth = 6371000;    //단위m
        return (diff * 360.0) / (2 * Math.PI*earth);
    }

    //반경 m이내의 경도차(degree)
    public static double LongitudeInDifference(double _latitude, int diff){
        //지구반지름
        final int earth = 6371000;    //단위m
        double ddd = Math.cos(0);
        double ddf = Math.cos(Math.toRadians(_latitude));
        return (diff*360.0) / (2*Math.PI*earth*Math.cos(Math.toRadians(_latitude)));
    }

    public static String boolean2YN(Boolean param) {
        String result = "";
        if (param) {
            result = "Y";
        } else {
            result = "N";
        }
        return result;
    }

}