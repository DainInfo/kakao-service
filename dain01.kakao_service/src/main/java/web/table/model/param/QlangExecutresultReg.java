package web.table.model.param;

import javax.validation.constraints.NotBlank;

import org.apache.ibatis.type.Alias;

import web.common.model.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Alias("qlangExecutresultReg")
public class QlangExecutresultReg extends BaseVO {
    @NotBlank(message = "실행 ID는 필수입력입니다.")
    private String executId;
    private String qlangId;
    private String reqreTime;
}
