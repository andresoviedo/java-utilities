package org.andresoviedo.util.spring.core;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.andresoviedo.util.classloader.BinaryClassLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Implementación de una BeanFactory que instancia beans a partir de un mapa que contiene su definición en forma binaria.
 * 
 * @author andresoviedo
 */
public class DynamicLoaderBeanFactory implements BeanFactory {

	private Map<String, byte[]> source = new HashMap<String, byte[]>();
	private Map<String, String> classNames = new HashMap<String, String>();

	public Map<String, byte[]> getSource() {
		return source;
	}

	public void setSource(Map<String, byte[]> source) {
		this.source = source;
	}

	public Map<String, String> getClassNames() {
		return classNames;
	}

	public void setClassNames(Map<String, String> classNames) {
		this.classNames = classNames;
	}

	public Object getBean(String name) throws BeansException {
		try {
			final Class<?> clazz = loadClass(name);
			final Constructor<?> constructor = clazz.getDeclaredConstructor(new Class<?>[0]);
			constructor.setAccessible(true);
			return constructor.newInstance(new Object[0]);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("No se ha podido instanciar el bean '" + name + "'. " + classNames.get(name), ex);
		}
	}

	private Class loadClass(String name) {
		String className = classNames.get(name);
		if (className == null) {
			className = name;
		}
		ClassLoader cl = new BinaryClassLoader(new ByteArrayInputStream(getSource().get(className)));
		try {
			return cl.loadClass(name);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Se ha producido una excepción instanciando el bean '" + name + "'", ex);
		}
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		throw new UnsupportedOperationException();
	}

	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		try {
			return (T) loadClass(name).newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("No se ha podido instanciar el bean '" + name + "' para la clase requerida '" + requiredType + "'",
					ex);
		}
	}

	public Object getBean(String name, Object[] args) throws BeansException {
		try {
			return loadClass(name).newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("No se ha podido instanciar el bean '" + name + "' con los argumentos '" + Arrays.toString(args)
					+ "'", ex);
		}
	}

	public boolean containsBean(String name) {
		return source.containsKey(name);
	}

	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return true;
	}

	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return false;
	}

	public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
		return targetType.isAssignableFrom(loadClass(name));
	}

	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return loadClass(name);
	}

	public String[] getAliases(String name) {
		return null;
	}

}
