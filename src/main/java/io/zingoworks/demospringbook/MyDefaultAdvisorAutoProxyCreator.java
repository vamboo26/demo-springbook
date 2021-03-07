package io.zingoworks.demospringbook;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;

import java.util.List;

public class MyDefaultAdvisorAutoProxyCreator extends DefaultAdvisorAutoProxyCreator {
	
	@Override
	protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
		System.out.println("beanClass = " + beanClass);
		System.out.println("beanName = " + beanName);
		System.out.println("super.findEligibleAdvisors(beanClass, beanName); = " + super.findEligibleAdvisors(beanClass, beanName));
		System.out.println("============================");
		return super.findEligibleAdvisors(beanClass, beanName);
	}
}
