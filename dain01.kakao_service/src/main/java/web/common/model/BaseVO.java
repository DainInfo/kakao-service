package web.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.ibatis.session.RowBounds;

import web.AppConstants;
import web.common.util.NullUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @Project : SCUP
 * @Class : BaseVO
 * @Description :
 * @Author : im7015
 * @Since : 2019-10-17
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseVO {
    private String loginUserSn; // WebControllerAspect.java 에서 로그인 세션 정보에서 읽어 넣어줌
    private String loginInsttCd; // WebControllerAspect.java 에서 로그인 세션 정보에서 읽어 넣어줌

    private Integer rowNum; // 목록쿼리 페이징시 ROW_NUMBER

    // 쿼리 실행시간
    private String qlangReqreTime;

    /**
     * 엑셀 다운로드시 필수항목
     */
    private String listinqryId; // 쿼리 아이디
    private String excelstreId; // 엑셀 저장 아이디

    /**
     * 페이징 처리 관련.
     */
    @Builder.Default
    private Integer schPageNum = 1;
    @Builder.Default
    private Integer schListCnt = AppConstants.PAGE_LIST_COUNT;
    @Builder.Default
    private Integer schPageSize = AppConstants.PAGE_HTML_SIZE;
    @Builder.Default
    private Integer schTotCnt = 0;

    @Builder.Default
    private List<OrderInfo> orderInfoList = null;
    private  String column =null ;
    private  boolean flagDesc ;

    /*@JsonIgnore
    private List<String> startDtColumnList = null;

    @Builder.Default
    private Map<String, String> startDtMap = null;*/

    private RowBounds rowBounds;

    public void setSchPageNum(Integer schPageNum) {
        this.schPageNum = NullUtil.isNull(schPageNum) ? 1 : schPageNum;
    }

    public void setSchListCnt(Integer schListCnt) {
        this.schListCnt = NullUtil.isNull(schListCnt) ? AppConstants.PAGE_LIST_COUNT : schListCnt;
    }

    /**
     * 정렬 조건 추가
     * @param columnName
     * @param isDesc
     */
    public void addOrderInfo(String columnName, boolean isDesc) {
        if (orderInfoList == null) {
            orderInfoList = new ArrayList<>();
        }
        orderInfoList.add(OrderInfo.builder().orderColumnName(columnName).isDesc(isDesc).build());
    }

 /*   public void addStartDtColumn(String columnName) {
        if (startDtColumnList == null) {
            startDtColumnList = new ArrayList<>();
        }
        startDtColumnList.add(columnName);
    }*/
}
