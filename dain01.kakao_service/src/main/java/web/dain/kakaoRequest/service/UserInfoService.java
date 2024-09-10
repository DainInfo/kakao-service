package web.dain.kakaoRequest.service;

import org.springframework.validation.annotation.Validated;

import web.dain.kakaoRequest.model.UserInfo;

@Validated
public interface UserInfoService {	
	public UserInfo getUserInfo();	
}
