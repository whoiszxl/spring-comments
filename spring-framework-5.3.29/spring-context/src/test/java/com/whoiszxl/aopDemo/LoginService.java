package com.whoiszxl.aopDemo;

public interface LoginService {

	/**
	 * 登录接口
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户令牌
	 */
	String login(String username, String password);

	/**
	 * 登出接口
	 * @param token 用户令牌
	 * @return 登出结果
	 */
	String logout(String token);


	@Override
	public int hashCode();

	@Override
	public boolean equals(Object obj);
}
