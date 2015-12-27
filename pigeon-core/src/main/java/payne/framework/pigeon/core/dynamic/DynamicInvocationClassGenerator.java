package payne.framework.pigeon.core.dynamic;

import java.lang.reflect.Method;

import payne.framework.pigeon.core.exception.DynamicInvocationGenerateException;

public interface DynamicInvocationClassGenerator {

	Generation generate(String implementation, Class<?> interfase, Method method) throws DynamicInvocationGenerateException;

}
