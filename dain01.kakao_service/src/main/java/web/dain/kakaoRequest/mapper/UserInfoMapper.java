package web.dain.kakaoRequest.mapper;

import egovframework.rte.psl.dataaccess.mapper.Mapper;
import web.dain.kakaoRequest.model.UserInfo;

@Mapper("userInfoMapper")
public interface UserInfoMapper {

	UserInfo getUserInfo();
}
