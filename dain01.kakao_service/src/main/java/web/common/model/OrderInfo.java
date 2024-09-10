package web.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
public class OrderInfo {
    public String orderColumnName;
    public boolean isDesc;
}
