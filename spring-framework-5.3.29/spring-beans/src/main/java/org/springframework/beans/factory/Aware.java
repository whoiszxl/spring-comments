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

package org.springframework.beans.factory;

/**
 * A marker superinterface indicating that a bean is eligible to be notified by the
 * Spring container of a particular framework object through a callback-style method.
 * The actual method signature is determined by individual subinterfaces but should
 * typically consist of just one void-returning method that accepts a single argument.
 *
 * <p>Note that merely implementing {@link Aware} provides no default functionality.
 * Rather, processing must be done explicitly, for example in a
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}.
 * Refer to {@link org.springframework.context.support.ApplicationContextAwareProcessor}
 * for an example of processing specific {@code *Aware} interface callbacks.
 *
 * 一个标记（marker）接口，用于指示一个Bean是否有资格被Spring容器通过回调式方法通知特定的框架对象。
 * 这个接口不包含任何方法，只是用于标记Bean，表示它可以接收容器的回调通知。
 *
 * public class Monkey implements BeanNameAware \{\
 *
 * 	private String beanName;
 *
 *    @Override
 *    public void setBeanName(@NotNull String beanName) {
 * 		System.out.println("BeanNameAware注入的beanName:" + beanName);
 * 		this.beanName = beanName;
 *    }
 * }
 *
 * 假如有如上的 Monkey 类，那么当此类初始化后，就会调用setBeanName进行回调
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public interface Aware {

}
