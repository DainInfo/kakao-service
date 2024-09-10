package web.dain.kakaoRequest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import web.dain.kakaoRequest.mapper.UserInfoMapper;
import web.dain.kakaoRequest.model.UserInfo;

@Validated
@Service("carInfoService")
public class UserInfoServiceImple implements UserInfoService{

    @Autowired 
    private UserInfoMapper userInfoMapper;

	@Override
	public UserInfo getUserInfo() {
		return userInfoMapper.getUserInfo();
	}
}
