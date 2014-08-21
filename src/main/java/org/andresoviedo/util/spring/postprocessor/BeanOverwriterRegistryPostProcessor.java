package org.andresoviedo.util.spring.postprocessor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanOverwriterRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private Logger logger = LoggerFactory.getLogger(BeanOverwriterRegistryPostProcessor.class);

	private Map<String, String> mapping;

	public Map<String, String> getMapping() {
		return mapping;
	}

	/**
	 * Sets the mapping for demo beans. Example to mock a bean is an entry having: "myProductionBean":"myMockedBean"
	 * 
	 * @param mapping
	 */
	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		try {
			if (mapping == null) {
				logger.warn("No mappin were registered to overwrite beans. Either specify mapping or remove this bean if it's not necessary");
			}
			for (final String beanName : registry.getBeanDefinitionNames()) {
				try {
					String demoBean = mapping.get(beanName);
					if (StringUtils.isBlank(demoBean)) {
						continue;
					}
					logger.info("Registering bean '" + demoBean + "' in place of bean '" + beanName + "...");
					BeanDefinition beanDemoDefinition = registry.getBeanDefinition(demoBean);
					registry.registerBeanDefinition(beanName, beanDemoDefinition);
				} catch (Exception ex) {
					logger.error("error in registry post process", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("error in registry post process", ex);
		}

	}
}