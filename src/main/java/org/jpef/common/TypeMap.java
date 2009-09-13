package org.jpef.common;

import java.util.HashMap;

/**
 * A map for associating singletons with types.
 * 
 * @author Gergely Kiss
 * 
 * @param <S>
 *            The base type stored in this map
 */
public class TypeMap<S> extends HashMap<Class<?>, S> {
	private boolean isKeyInheritable = true;
	private boolean isKeyInterfaceable = true;

	@Override
	public S get(Object key) {
		S value = super.get(key);

		if (value != null) {
			return value;
		}

		if (isKeyInheritable) {
			Class<?> type = ((Class<?>) key).getSuperclass();

			while (type != null) {
				value = super.get(type);

				if (value != null) {
					return value;
				}

				type = type.getSuperclass();
			}
		}

		if (isKeyInterfaceable) {
			Class<?> type = ((Class<?>) key).getSuperclass();
			if (type != null) {
				Class<?>[] ifaces = type.getInterfaces();

				for (Class<?> iface : ifaces) {
					value = super.get(iface);

					if (value != null) {
						return value;
					}
				}
			}
		}

		return null;
	}
}
