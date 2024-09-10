package web;

/**
 * @Project : SCUP
 * @Class : AppConstants
 * @Description : Application 상수 정의
 * (비지니스 업무에 속하는 상수는 각 업무의 상수 클래스로 정의)
 * @Author : im7015
 * @Since : 2019-05-10
 */
public class AppConstants {

    public static final String PK_SEPARATOR = "-";

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_CUST = "ROLE_CUST";
    public static final String BLANK = " ";

    public static final String USEAT_CD_Y = "Y";
    public static final String USEAT_CD_N = "N";

    /**
     * 로그인한 유저의 순번을 Map<String, Object> params 에 담아주는 키값
     */
    public static final String LOGIN_USER_SN_KEY = "loginUserSn";
    public static final String LOGIN_INSTT_CD_KEY = "loginInsttCd";

    public static final String API_SUCCESS_CODE = "1";
    public static final String API_FAIL_CODE = "2";

    /**
     * return code
     */
    public static final String SUCCESS = "00";
    public static final String FAIL = "99";
    /**
     * Example: 2014-11-21
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    /**
     * Example: 2014-11-21 16:25:32
     */
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm";
    /**
     *
     */
    public static final String DEFAULT_MESSAGE_BEAN_NAME = "messageSource";
    /**
     * URL
     */
    public static final String REDIRECT_UNAUTHORIZED = "redirect:/error/unauthorized.do";
    /**
     * 페이징 정보
     */
    // 검색조건 접두사
    public static final String SEARCH_PREFIX = "sch";
    public static final String PAGE_NUMBER_KEY = "schPageNum";
    public static final String PAGE_LIST_COUNT_KEY = "schListCnt";
    public static final String PAGE_HTML_SIZE_KEY = "schPageSize";
    public static final String PAGE_TOTAL_COUNT_KEY = "schTotCnt";
    public static final String PAGE_ROW_BOUNDS_KEY = "rowBounds"; // mybatis interceptor에서 페이징 구현시 필요한 파라미터.
    public static final int PAGE_LIST_COUNT = 10; // 한페이지의 목록(리스트)에 표시할 건수
    public static final int PAGE_HTML_SIZE = 10; // 페이징 네비게이션에 표시할 건수 : << 1 2 3 4 5 ... >>
    public static final String PAGING_EVENT_NAME = "goPage";


    // 쿼리 소요시간 키
    public static final String QLANG_REQRE_TIME_KEY = "qlangReqreTime";

    // 파입 그룹 키
    public static final String ATCHMNFLGROUP_SN = "atchmnflgroupSn";

    // 엑셀 업로드
    public static final long UPLOAD_EXCEL_MAX_SIZE = 1024 * 1024 * 20; // 업로드 최대 사이즈 설정 (20M)
    public static final int UPLOAD_EXCEL_MAX_ROW = 500000;

    public static final String JOBTY_CD_NORECOG = "001";   // 사건번호인식
    public static final String JOBTY_CD_SPOT = "002";       // 현장지점
    public static final String JOBTY_CD_SPTEQPMN = "003";   // 현장장비
    public static final String JOBTY_CD_CNTERSCHDUL = "004";   // 센터일정
    public static final String JOBTY_CD_INCDNT = "005";   // 사건
    public static final String JOBTY_CD_BSNS = "008";       // 사업관리
    public static final String JOBTY_CD_WRDOFC = "009";     // 구정지표
    public static final String JOBTY_CD_BBS = "010";        // 게시판
    public static final String JOBTY_CD_CMMNCD_DETL = "011";        // 게시판
    public static final String JOBTY_CD_POLICE_INCDNTSTTEMNT = "013";        // 경찰청 신고접수

    //밸리데이션 문구
    public static final String SIZE_MAX = " 최대길이는 {max}입니다.";;
    public static final String SIZE_MIN = " 최소길이는 {min}입니다.";
    public static final String SIZE_MIN_MAX = " 최소길이는 {min}, 최대길이는 {max}입니다.";

    public static final String MAX_VALUE = " 최대값은 {value}입니다.";
    public static final String MIN_VALUE = " 최소값은 {value}입니다.";

    public static final String ADMIN_USER = "0000001";

    public static final int LONGITUDE_MIN = 124; // 경도 최소
    public static final int LONGITUDE_MAX = 132; // 경도 최대
    public static final int LATITUDE_MIN = 33; // 위도 최소
    public static final int LATITUDE_MAX = 43; // 위도 최대

    private AppConstants() {}

}
