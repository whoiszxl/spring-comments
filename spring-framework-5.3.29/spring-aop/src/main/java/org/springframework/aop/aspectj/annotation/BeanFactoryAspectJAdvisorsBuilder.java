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

package org.springframework.aop.aspectj.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.reflect.PerClauseKind;

import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Helper for retrieving @AspectJ beans from a BeanFactory and building
 * Spring Advisors based on them, for use with auto-proxying.
 *
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see AnnotationAwareAspectJAutoProxyCreator
 */
public class BeanFactoryAspectJAdvisorsBuilder {

	private final ListableBeanFactory beanFactory;

	private final AspectJAdvisorFactory advisorFactory;

	@Nullable
	private volatile List<String> aspectBeanNames;

	private final Map<String, List<Advisor>> advisorsCache = new ConcurrentHashMap<>();

	private final Map<String, MetadataAwareAspectInstanceFactory> aspectFactoryCache = new ConcurrentHashMap<>();


	/**
	 * Create a new BeanFactoryAspectJAdvisorsBuilder for the given BeanFactory.
	 * @param beanFactory the ListableBeanFactory to scan
	 */
	public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory) {
		this(beanFactory, new ReflectiveAspectJAdvisorFactory(beanFactory));
	}

	/**
	 * Create a new BeanFactoryAspectJAdvisorsBuilder for the given BeanFactory.
	 * @param beanFactory the ListableBeanFactory to scan
	 * @param advisorFactory the AspectJAdvisorFactory to build each Advisor with
	 */
	public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {
		Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
		Assert.notNull(advisorFactory, "AspectJAdvisorFactory must not be null");
		this.beanFactory = beanFactory;
		this.advisorFactory = advisorFactory;
	}


	/**
	 * Look for AspectJ-annotated aspect beans in the current bean factory,
	 * and return to a list of Spring AOP Advisors representing them.
	 * <p>Creates a Spring Advisor for each AspectJ advice method.
	 * @return the list of {@link org.springframework.aop.Advisor} beans
	 * @see #isEligibleBean
	 */
	public List<Advisor> buildAspectJAdvisors() {
		// 获取到 @Aspect 注解标记的 bean 名称，首次访问时为 null
		List<String> aspectNames = this.aspectBeanNames;

		// 为 null，表示是第一次访问，需要重新构建 advisor
		if (aspectNames == null) {
			synchronized (this) {
				// 双重检查
				aspectNames = this.aspectBeanNames;
				if (aspectNames == null) {
					// 开始构建
					List<Advisor> advisors = new ArrayList<>();
					aspectNames = new ArrayList<>();

					// 获取 Spring 容器中所有的 bean 名称
					String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
							this.beanFactory, Object.class, true, false);

					// 开始遍历所有的 bean
					for (String beanName : beanNames) {
						// 将不符合规则的 bean 过滤掉
						if (!isEligibleBean(beanName)) {
							continue;
						}
						// We must be careful not to instantiate beans eagerly as in this case they
						// would be cached by the Spring container but would not have been weaved.
						// 获取 bean 的 class
						Class<?> beanType = this.beanFactory.getType(beanName, false);
						if (beanType == null) {
							continue;
						}

						// 判断这个 bean 是否添加了 @Aspect 注解，并且 class 不是 AspectJ 编译的
						if (this.advisorFactory.isAspect(beanType)) {
							// 将这个 bean 的名称添加到切面名集合 aspectNames 中
							aspectNames.add(beanName);

							// 包装一个切面元数据对象
							AspectMetadata amd = new AspectMetadata(beanType, beanName);

							// 判断 AspectJ 切面的声明方式是否是 SINGLETON ，即它是否是一个单例切面
							if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
								MetadataAwareAspectInstanceFactory factory =
										new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);

								// 获取到当前切面类中的 advisor，也就是添加了 @Before 和 @After 的方法
								List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);

								// 判断这个 bean 在容器中是否是单例，如果是，则添加到 advisor 缓存集合中方便下次高效获取
								// 因为项目启动之后切面类是不会发生变化了，所以没必要每次都将获取的流程走一遍
								if (this.beanFactory.isSingleton(beanName)) {
									this.advisorsCache.put(beanName, classAdvisors);
								}
								else {
									// 不是单例则缓存工厂，方便下次创建
									this.aspectFactoryCache.put(beanName, factory);
								}
								// 添加到 advisors 集合中
								advisors.addAll(classAdvisors);
							}
							else {
								// Per target or per this.
								if (this.beanFactory.isSingleton(beanName)) {
									throw new IllegalArgumentException("Bean with name '" + beanName +
											"' is a singleton, but aspect instantiation model is not singleton");
								}
								MetadataAwareAspectInstanceFactory factory =
										new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
								this.aspectFactoryCache.put(beanName, factory);
								advisors.addAll(this.advisorFactory.getAdvisors(factory));
							}
						}
					}

					// 添加到 aspectBeanNames 实例中的数组，方便下次获取
					this.aspectBeanNames = aspectNames;
					return advisors;
				}
			}
		}

		// 如果 aspectNames 是集合，但是是空的，那直接返回空集合
		if (aspectNames.isEmpty()) {
			return Collections.emptyList();
		}

		List<Advisor> advisors = new ArrayList<>();

		// aspectNames 存在元素的逻辑，遍历一下
		for (String aspectName : aspectNames) {
			// 从单例切面缓存中拿到对应 bean 的所有 advisor
			List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
			// 如果不为空，则添加到返回结果的集合 advisors 中
			if (cachedAdvisors != null) {
				advisors.addAll(cachedAdvisors);
			}
			else {
				// 如果为空，则从切面工厂缓存中拿到工厂实例，然后通过工厂将 advisor 创建出来
				MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
				advisors.addAll(this.advisorFactory.getAdvisors(factory));
			}
		}
		return advisors;
	}

	/**
	 * Return whether the aspect bean with the given name is eligible.
	 * @param beanName the name of the aspect bean
	 * @return whether the bean is eligible
	 */
	protected boolean isEligibleBean(String beanName) {
		return true;
	}

}
