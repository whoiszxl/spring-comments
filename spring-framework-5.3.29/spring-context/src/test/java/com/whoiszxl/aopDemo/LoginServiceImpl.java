package com.whoiszxl.aopDemo;

import org.springframework.stereotype.Service;

/**
 * 普通的登录服务实现
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Override
	public String login(String username, String password) {
		System.out.printf("执行登录业务: %s, %s \n", username, password);
		return "登录成功";
	}

	@Override
	public String logout(String token) {
		System.out.printf("执行登出业务: %s \n", token);
		return "登出成功";
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
