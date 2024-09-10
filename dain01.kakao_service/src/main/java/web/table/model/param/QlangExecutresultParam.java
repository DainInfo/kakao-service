package web.table.model.param;

import org.apache.ibatis.type.Alias;

import web.common.model.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @Project : scup-modules
 * @Class : QlangExecutresultParam
 * @Description : 
 * @Author : im7015
 * @Since : 2019-12-13 
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Alias("qlangExecutresultParam")
public class QlangExecutresultParam extends BaseVO {
    private String qlangId;
    private String schTyDate;
    private String schStartDt;
    private String schEndDt;
    private String schTyText;
}
