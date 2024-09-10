package web.dain.mapper;

import egovframework.rte.psl.dataaccess.mapper.Mapper;
import web.dain.model.UserInfo;

@Mapper("carInfoMapper")
public interface CarInfoMapper {

	UserInfo getUserInfo();
}
