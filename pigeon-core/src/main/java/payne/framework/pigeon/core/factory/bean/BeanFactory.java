package payne.framework.pigeon.core.factory.bean;

import java.util.Map;

import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.exception.InexistentBeanException;

public interface BeanFactory {

	boolean contains(String name);

	<T> T get(Class<T> type) throws InexistentBeanException, BeanInitializeException;

	<T> T establish(Class<T> type) throws InexistentBeanException, BeanInitializeException;

	String value(String name);

	String value(String name, String _default);

	<T> T get(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException;

	<T> T establish(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException;

	<T> Map<String, T> find(Class<T> type) throws InexistentBeanException, BeanInitializeException;

}
