/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.framework.autoproxy;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * Holder for the current proxy creation context, as exposed by auto-proxy creators
 * such as {@link AbstractAdvisorAutoProxyCreator}.
 *
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @since 2.5
 */
public final class ProxyCreationContext {

	/** ThreadLocal holding the current proxied bean name during Advisor matching. */
	private static final ThreadLocal<String> currentProxiedBeanName =
			new NamedThreadLocal<>("Name of currently proxied bean");


	private ProxyCreationContext() {
	}


	/**
	 * Return the name of the currently proxied bean instance.
	 * @return the name of the bean, or {@code null} if none available
	 */
	@Nullable
	public static String getCurrentProxiedBeanName() {
		return currentProxiedBeanName.get();
	}

	/**
	 * Set the name of the currently proxied bean instance.
	 * @param beanName the name of the bean, or {@code null} to reset it
	 */
	static void setCurrentProxiedBeanName(@Nullable String beanName) {
		if (beanName != null) {
			// 通过 currentProxiedBeanName 这个线程局部变量（ThreadLocal）来存储当前代理的 Bean 名称
			currentProxiedBeanName.set(beanName);
		}
		else {
			currentProxiedBeanName.remove();
		}
	}

}
