package web.dain.kakaoRequest.controller;

import org.springframework.http.HttpStatus;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import web.dain.kakaoRequest.model.UserInfo;
import web.dain.kakaoRequest.service.UserInfoService;

@Controller
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

	/**
	* 카카오 홍보/대상 ci
	**/
	@RequestMapping("/user/ci")
    public ResponseEntity<String> getUserCI() {			
		UserInfo userInfo = userInfoService.getUserInfo();
		return new ResponseEntity<String>(userInfo.getCi(), HttpStatus.OK);	
		
    }
	
	/**
	* 카카오 홍보/대상 연락처
	**/
	@RequestMapping("/user/phoneNumber")
    public ResponseEntity<String> getUserPhoneNumber() {			
		UserInfo userInfo = userInfoService.getUserInfo();
		return new ResponseEntity<String>(userInfo.getPhoneNumber(), HttpStatus.OK);	
		
    }
}
