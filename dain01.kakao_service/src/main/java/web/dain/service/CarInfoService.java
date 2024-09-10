package web.dain.service;

import org.springframework.validation.annotation.Validated;

import web.dain.model.UserInfo;

@Validated
public interface CarInfoService {	
	public UserInfo getUserInfo();	
}
