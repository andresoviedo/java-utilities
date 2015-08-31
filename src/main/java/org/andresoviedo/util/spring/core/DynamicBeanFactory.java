package org.andresoviedo.util.spring.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Extensión del {@link GenericApplicationContext} de Spring que integración diferentes BeanFactories.
 * 
 * @author andresoviedo
 */
public class DynamicBeanFactory extends GenericApplicationContext {

	private Map<String, BeanFactory> additionalFactories = new HashMap<String, BeanFactory>();

	public DynamicBeanFactory() {
		super();
	}

	public DynamicBeanFactory(ApplicationContext parent) {
		super(parent);
	}

	public DynamicBeanFactory(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
		super(beanFactory, parent);
	}

	public DynamicBeanFactory(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);
	}

	public Map<String, BeanFactory> getAdditionalFactories() {
		return additionalFactories;
	}

	public void setAdditionalFactories(Map<String, BeanFactory> additionalFactories) {
		this.additionalFactories = additionalFactories;
	}

	public Object getBean(String name) throws BeansException {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).getBean(name);
		} else {
			return super.getBean(name);
		}
	}

	public Object getBean(String name, Class requiredType) throws BeansException {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).getBean(name, requiredType);
		} else {
			return super.getBean(name, requiredType);
		}
	}

	public Object getBean(String name, Object[] args) throws BeansException {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).getBean(name, args);
		} else {
			return super.getBean(name, args);
		}
	}

	public boolean containsBean(String name) {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).containsBean(name);
		} else {
			return super.containsBean(name);
		}
	}

	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).isSingleton(name);
		} else {
			return super.isSingleton(name);
		}
	}

	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).isPrototype(name);
		} else {
			return super.isPrototype(name);
		}
	}

	public boolean isTypeMatch(String name, Class targetType) throws NoSuchBeanDefinitionException {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).isTypeMatch(name, targetType);
		} else {
			return super.isTypeMatch(name, targetType);
		}
	}

	public Class getType(String name) throws NoSuchBeanDefinitionException {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).getType(name);
		} else {
			return super.getType(name);
		}
	}

	public String[] getAliases(String name) {
		if (getAdditionalFactories().containsKey(name)) {
			return getAdditionalFactories().get(name).getAliases(name);
		} else {
			return super.getAliases(name);
		}
	}

}
