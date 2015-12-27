package payne.framework.pigeon.core.dynamic;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.Random;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.exception.DynamicInvocationGenerateException;
import payne.framework.pigeon.core.toolkit.Collections;

public class ASMDynamicInvocationClassGenerator implements DynamicInvocationClassGenerator, Opcodes {

	public Generation generate(String implementation, Class<?> interfase, Method method) throws DynamicInvocationGenerateException {
		try {
			String x = Pigeons.getOpenPath(implementation);
			String y = Pigeons.getOpenPath(interfase);
			String z = Pigeons.getOpenPath(method);

			String className = Collections.concatenate((x + y + z).split("/+"), "/", "", "\\s+");

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cw.visit(V1_6, ACC_PUBLIC, className, null, "payne/framework/pigeon/core/dynamic/DynamicInvocation", null);
			// 序列化ID
			cw.visitField(ACC_PRIVATE + ACC_STATIC + ACC_FINAL, "serialVersionUID", Type.getType(long.class).getDescriptor(), null, new Random().nextLong()).visitEnd();
			// 无参构造方法
			org.objectweb.asm.commons.Method m = org.objectweb.asm.commons.Method.getMethod("void <init> ()");
			GeneratorAdapter ga = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cw);
			ga.loadThis();
			ga.invokeConstructor(Type.getType(DynamicInvocation.class), m);
			ga.returnValue();
			ga.endMethod();
			// 参数类型为Invocation的构造方法
			m = org.objectweb.asm.commons.Method.getMethod("void <init> (payne.framework.pigeon.core.Invocation)");
			ga = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cw);
			ga.loadThis();
			ga.loadArg(0);
			ga.invokeConstructor(Type.getType(DynamicInvocation.class), m);
			ga.returnValue();
			ga.endMethod();
			// 遍历方法的参数,作为类的一个字段 名称用 argument + 参数下标
			for (int i = 0; i < method.getParameterTypes().length; i++) {
				String signature = toSignature(method.getGenericParameterTypes()[i]);
				// 字段
				String desc = Type.getType(method.getParameterTypes()[i]).getDescriptor();
				cw.visitField(ACC_PRIVATE, "argument" + i, desc, signature, null).visitEnd();
				// getter
				m = org.objectweb.asm.commons.Method.getMethod(this.toString(method.getParameterTypes()[i]) + " getArgument" + i + "()");
				ga = new GeneratorAdapter(ACC_PUBLIC, m, signature != null ? "()" + signature : null, null, cw);
				ga.loadThis();
				ga.getField(Type.getType("L" + className + ";"), "argument" + i, Type.getType(method.getParameterTypes()[i]));
				ga.returnValue();
				ga.endMethod();
				// setter
				m = org.objectweb.asm.commons.Method.getMethod("void setArgument" + i + "(" + this.toString(method.getParameterTypes()[i]) + ")");
				ga = new GeneratorAdapter(ACC_PUBLIC, m, signature != null ? "(" + signature + ")V" : null, null, cw);
				ga.loadThis();
				ga.loadArg(0);
				ga.putField(Type.getType("L" + className + ";"), "argument" + i, Type.getType(method.getParameterTypes()[i]));
				ga.returnValue();
				ga.endMethod();
			}

			if (method.getReturnType() != Void.TYPE) {
				String signature = toSignature(method.getGenericReturnType());
				// 字段
				String desc = Type.getType(method.getReturnType()).getDescriptor();
				cw.visitField(ACC_PRIVATE, "result", desc, signature != null ? signature : null, null).visitEnd();
				// getter
				m = org.objectweb.asm.commons.Method.getMethod(this.toString(method.getReturnType()) + " getResult" + "()");
				ga = new GeneratorAdapter(ACC_PUBLIC, m, signature != null ? "()" + signature : null, null, cw);
				ga.loadThis();
				ga.getField(Type.getType("L" + className + ";"), "result", Type.getType(method.getReturnType()));
				ga.returnValue();
				ga.endMethod();
				// setter
				m = org.objectweb.asm.commons.Method.getMethod("void setResult" + "(" + this.toString(method.getReturnType()) + ")");
				ga = new GeneratorAdapter(ACC_PUBLIC, m, signature != null ? "(" + signature + ")V" : null, null, cw);
				ga.loadThis();
				ga.loadArg(0);
				ga.putField(Type.getType("L" + className + ";"), "result", Type.getType(method.getReturnType()));
				ga.returnValue();
				ga.endMethod();
			}
			// 结束
			cw.visitEnd();

			return new Generation(className.replaceAll("\\/", "."), cw.toByteArray());
		} catch (Exception e) {
			throw new DynamicInvocationGenerateException(e, method);
		}
	}

	private String toString(java.lang.reflect.Type type) {
		if (type instanceof Class<?>) {
			return ((Class<?>) type).getName();
		}
		return type.toString();
	}

	private String toSignature(java.lang.reflect.Type type) {
		if (type instanceof Class<?>) {
			return Type.getDescriptor((Class<?>) type);
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			StringBuilder builder = new StringBuilder();
			Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			builder.append("L" + rawType.getName().replaceAll("\\.", "/"));
			builder.append("<");
			for (java.lang.reflect.Type t : parameterizedType.getActualTypeArguments()) {
				builder.append(toSignature(t));
			}
			builder.append(">;");
			return builder.toString();
		}
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			java.lang.reflect.Type[] lowerBounds = wildcardType.getLowerBounds();
			java.lang.reflect.Type[] upperBounds = wildcardType.getUpperBounds();
			if (lowerBounds.length > 0) {
				return "-" + toSignature(lowerBounds[0]);
			}
			if (upperBounds[0] == Object.class) {
				return "*";
			}
			return "+" + toSignature(upperBounds[0]);
		}
		if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			return "[" + toSignature(genericArrayType.getGenericComponentType());
		}
		throw new IllegalArgumentException("can not signature for type : " + type + " please avoid declare " + type.getClass().getSimpleName() + " argument");
	}
}
