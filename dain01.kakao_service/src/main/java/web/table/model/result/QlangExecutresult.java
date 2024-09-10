package web.table.model.result;

import org.apache.ibatis.type.Alias;

import web.common.model.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @Project : scup-modules
 * @Class : QlangExecutresult
 * @Description : 
 * @Author : im7015
 * @Since : 2019-12-13 
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Alias("qlangExecutresult")
public class QlangExecutresult extends BaseVO {
    private String executId;
    private String qlangId;
    private String reqreTime;
    private String creatDt;
    private String creatDtMk;
}
