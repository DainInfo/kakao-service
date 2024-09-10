package web.table.mapper;

import java.util.List;

import egovframework.rte.psl.dataaccess.mapper.Mapper;
import web.table.model.param.QlangExecutresultParam;
import web.table.model.param.QlangExecutresultReg;
import web.table.model.result.QlangExecutresult;

/**
 * @Project : scup-modules
 * @Class : QlangExecutresultMapper
 * @Description : 질의어 실행 결과
 * @Author : im7015
 * @Since : 2019-12-13 
 */
@Mapper("qlangExecutresultMapper")
public interface QlangExecutresultMapper {

    /**
     * 질의어 실행결과 목록 조회
     * @param qlangExecutresultParam
     * @return
     */
    List<QlangExecutresult> selectQlangExecutresultList(QlangExecutresultParam qlangExecutresultParam);

    /**
     * 질의어 실행결과 등록
     * @param qlangExecutresultReg
     * @return
     */
    int insertQlangExecutresult(QlangExecutresultReg qlangExecutresultReg);
}
