package web.dain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import web.dain.mapper.CarInfoMapper;
import web.dain.model.UserInfo;

@Validated
@Service("carInfoService")
public class CarInfoServiceImple implements CarInfoService{

    @Autowired 
    private CarInfoMapper carInfoMapper;

	@Override
	public UserInfo getUserInfo() {
		return carInfoMapper.getUserInfo();
	}
}
