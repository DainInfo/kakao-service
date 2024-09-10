package web.dain.model;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Alias("userInfo")
public class UserInfo {
	private String	ci;		//카카오 홍보 대상자 연락처
	private String	phoneNumber;		//카카오 홍보 대상자 연락처
}
