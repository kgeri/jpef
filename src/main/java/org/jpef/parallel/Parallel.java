package org.jpef.parallel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Parallel execution helper class.
 * 
 * @author Gergely Kiss
 */
public abstract class Parallel {

	/**
	 * Returns a parallel proxy for the given bean and interface.
	 * 
	 * @param <T>
	 * @param impl
	 * @param iface
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxy(Object impl, Class<T> iface) {
		// TODO proxy caching
		ParallelContext ctx = new ParallelContext();
		InvocationHandler ih = new ParallelInvocationHandler(impl, ctx);

		return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface }, ih);
	}

	/**
	 * Invokes the given method for parallel execution on the given instance.
	 * 
	 * @param impl
	 * @param methodName
	 * @param args
	 * 
	 * @return
	 * @throws InvocationTargetException
	 *             If the invocation failed
	 * @throws NoSuchMethodError
	 *             If the method cannot be found
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object impl, String methodName, Class<T> returnType, Object... args)
			throws InvocationTargetException {
		// TODO proxy caching
		ParallelContext ctx = new ParallelContext();
		InvocationHandler ih = new ParallelInvocationHandler(impl, ctx);

		try {
			Method method = null;
			Class<?> type = impl.getClass();

			// Trying to determine method by name and parameters
			if (args == null) {
				// Parameterless methods are easy - only the return type must be
				// checked
				method = type.getMethod(methodName);

				if (!method.getReturnType().equals(returnType)) {
					throw new NoSuchMethodError("No method found: "
							+ getSignature(impl, methodName, returnType, args));
				}
			} else {
				// Parametric methods are checked for method name, params
				// length, return type and param types
				Method[] methods = type.getDeclaredMethods();
				for (Method dm : methods) {
					if (!dm.getName().equals(methodName)) {
						continue;
					} else if (dm.getParameterTypes().length != args.length) {
						continue;
					} else if (!dm.getReturnType().equals(returnType)) {
						continue;
					}

					Class<?>[] rts = dm.getParameterTypes();
					boolean ok = true;
					for (int i = 0; i < rts.length; i++) {
						if (!rts[i].isAssignableFrom(args[i].getClass())) {
							ok = false;
							break;
						}
					}

					if (ok) {
						method = dm;
						break;
					}
				}
			}

			if (method == null) {
				throw new NoSuchMethodError("No method found: "
						+ getSignature(impl, methodName, returnType, args));
			}

			return (T) ih.invoke(null, method, args);
		} catch (NoSuchMethodError e) {
			throw e;
		} catch (Throwable t) {
			throw new InvocationTargetException(t);
		}
	}

	static String getSignature(Object impl, String name, Class<?> returnType, Object... args) {
		StringBuilder sb = new StringBuilder();

		sb.append(returnType.getSimpleName()).append(' ');
		sb.append(impl.getClass().getSimpleName());
		sb.append('.').append(name);
		sb.append('(');
		if (args != null) {
			boolean first = true;
			for (Object arg : args) {
				sb.append(first ? "" : ", ");
				sb.append(arg.getClass().getSimpleName());
			}
		}
		sb.append(')');

		return sb.toString();
	}
}
