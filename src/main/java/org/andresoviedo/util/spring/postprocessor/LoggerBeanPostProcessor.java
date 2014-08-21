package org.andresoviedo.util.spring.postprocessor;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

@Named
public final class LoggerBeanPostProcessor implements BeanPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(LoggerBeanPostProcessor.class);

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (field.getAnnotation(Inject.class) != null) {
					if (!field.getType().equals(Logger.class)) {
						return;
					}

					logger.info("Injecting Logger '" + bean.getClass() + "' for bean '" + beanName + "'...");
					Logger log = LoggerFactory.getLogger(bean.getClass());
					field.setAccessible(true);
					field.set(bean, log);
				}
			}
		});
		return bean;
	}
}