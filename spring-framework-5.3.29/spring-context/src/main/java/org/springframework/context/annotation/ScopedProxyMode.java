/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.context.annotation;

/**
 * Enumerates the various scoped-proxy options.
 *
 * <p>For a more complete discussion of exactly what a scoped proxy is, see the
 * section of the Spring reference documentation entitled '<em>Scoped beans as
 * dependencies</em>'.
 *
 * @author Mark Fisher
 * @since 2.5
 * @see ScopeMetadata
 */
public enum ScopedProxyMode {

	/**
	 *
	 * DEFAULT（默认值）：通常等同于 NO，除非在组件扫描指令级别配置了不同的默认值。默认的代理模式通常由容器的配置来决定
	 *
	 * Default typically equals {@link #NO}, unless a different default
	 * has been configured at the component-scan instruction level.
	 */
	DEFAULT,

	/**
	 *
	 * 表示不创建作用域代理。这意味着不使用代理，Bean的生命周期由容器直接管理。
	 * 这个代理模式通常不适用于非单例作用域的Bean，因为它们通常需要代理来控制生命周期。
	 *
	 * Do not create a scoped proxy.
	 * <p>This proxy-mode is not typically useful when used with a
	 * non-singleton scoped instance, which should favor the use of the
	 * {@link #INTERFACES} or {@link #TARGET_CLASS} proxy-modes instead if it
	 * is to be used as a dependency.
	 */
	NO,

	/**
	 *
	 * 表示创建一个基于接口的JDK动态代理。当Bean的作用域为非单例时，
	 * Spring会为该Bean创建一个实现了Bean类所有接口的代理对象，用于控制Bean的生命周期。
	 *
	 * Create a JDK dynamic proxy implementing <i>all</i> interfaces exposed by
	 * the class of the target object.
	 */
	INTERFACES,

	/**
	 *
	 * 表示创建一个基于类的CGLIB代理。类似于INTERFACES模式，但是使用CGLIB库来创建代理对象，而不需要Bean实现接口。
	 *
	 * Create a class-based proxy (uses CGLIB).
	 */
	TARGET_CLASS

}
