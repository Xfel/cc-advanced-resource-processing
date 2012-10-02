/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.peripheral.bind;

import java.io.ObjectInputStream.GetField;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Primitives;

import dan200.computer.api.IComputerAccess;
import xfel.mods.arp.base.peripheral.AbstractPeripheral;
import xfel.mods.arp.base.utils.reflection.PrimitiveTypeHelper;

public abstract class AbstractAnnotatedPeripheral extends AbstractPeripheral {

	private static class BindingData {
		String[] mnames;
		Method[] methods;
		boolean[] async;
	}

	private static Map<Class<? extends AbstractAnnotatedPeripheral>, BindingData> computedData = new HashMap<Class<? extends AbstractAnnotatedPeripheral>, BindingData>();

	private static BindingData getBindingData(
			Class<? extends AbstractAnnotatedPeripheral> cls) {
		BindingData bdata = computedData.get(cls);
		if (bdata != null) {
			return bdata;
		}

		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Method> methods = new ArrayList<Method>();
		ArrayList<Boolean> async=new ArrayList<Boolean>();

		for (Method method : cls.getMethods()) {
			if (method.isAnnotationPresent(PeripheralMethod.class)) {
				PeripheralMethod ann = method
						.getAnnotation(PeripheralMethod.class);

				String name = ann.name();
				if (name.equals(PeripheralMethod.DEFAULT_NAME)) {
					name = method.getName();
				}

				names.add(name);
				methods.add(method);
				async.add(ann.async());
			}
		}

		bdata = new BindingData();
		bdata.methods = (Method[]) methods.toArray(new Method[methods.size()]);
		bdata.mnames = (String[]) names.toArray(new String[names.size()]);
		bdata.async=Booleans.toArray(async);

		computedData.put(cls, bdata);

		return bdata;
	}

	private final BindingData bindingData;

	protected AbstractAnnotatedPeripheral() {
		super();
		bindingData = getBindingData(getClass());
	}

	protected AbstractAnnotatedPeripheral(String type) {
		super(type);
		bindingData = getBindingData(getClass());
	}

	@Override
	public String[] getMethodNames() {
		return bindingData.mnames;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int methodIndex,
			final Object[] arguments) throws Exception {
		final Method method = bindingData.methods[methodIndex];
		
		if(bindingData.async[methodIndex]){
			return new Object[]{queueTask(computer, new Task() {
				
				@Override
				protected Object[] execute() throws Exception {
					return doCallMethod(arguments, method);
				}
			})};
		}

		return doCallMethod(arguments, method);
	}

	protected Object[] doCallMethod(Object[] arguments, Method method)
			throws Exception, IllegalAccessException {
		Class<?>[] argtypes = method.getParameterTypes();
		Annotation[][] argannos = method.getParameterAnnotations();

		Object[] newargs = new Object[argtypes.length];

		for (int i = 0; i < newargs.length; i++) {
			if (i == newargs.length - 1 && method.isVarArgs()) {
				Class<?> cls = argtypes[i].getComponentType();

				ArrayList<Object> list = new ArrayList<Object>();
				for (int j = i; j < arguments.length; j++) {
					list.add(adaptArg(arguments[j], cls, argannos[i], j));
				}
				if (cls.isPrimitive()) {
					newargs[i] = PrimitiveTypeHelper.getInstance(cls)
							.convertToTypeArray(list);
				}
				newargs[i] = list.toArray();
			} else {
				Object argval = null;
				if (i < arguments.length) {
					argval = arguments[i];
				}

				newargs[i] = adaptArg(argval, argtypes[i], argannos[i], i);
			}
		}

		Object result;
		try {
			result = method.invoke(this, newargs);
		} catch (InvocationTargetException e) {
			throw (Exception) e.getTargetException();
		}

		if (result == null) {
			return new Object[0];
		}

		if (result.getClass().isArray()) {
			return (Object[]) result;
		}

		return new Object[] { result };
	}

	private Object adaptArg(Object argval, Class<?> cls,
			Annotation[] annotations, int argIndex) throws Exception {
		if (cls.isPrimitive()) {
			Class<?> wrapper = Primitives.wrap(cls);

			if (argval == null) {
				Default def = getAnnotation(annotations, Default.class);
				if (def != null) {
					argval = Double.valueOf(def.value());
				} else {
					throw new Exception("arg " + (argIndex + 1) + ": "
							+ cls.getSimpleName() + " expected");
				}
			}

			if (wrapper.isInstance(argval)) {
				return argval;
			}

			return PrimitiveTypeHelper.getInstance(cls).convertToType(argval);
		}

		if (cls.isInstance(argval) || argval == null) {
			return argval;
		}

		if (argval instanceof Map) {
			Map table = (Map) argval;

			if (List.class.isAssignableFrom(cls) || cls.isArray()) {
				Class<?> ctype = cls.isArray() ? cls.getComponentType()
						: Object.class;

				ArrayList<Object> list = new ArrayList<Object>();

				for (int i = 1; table.containsKey(Double.valueOf(i)); i++) {
					list.add(adaptArg(table.get(Double.valueOf(i)), ctype,
							new Annotation[0], argIndex));
				}

				if (cls.isArray()) {
					if (ctype.isPrimitive()) {
						return PrimitiveTypeHelper.getInstance(ctype)
								.convertToTypeArray(list);
					}
					return list.toArray((Object[]) Array.newInstance(ctype,
							list.size()));
				}
				return list;
			}

		}

		throw new Exception("arg " + (argIndex + 1) + ": "
				+ cls.getSimpleName() + " expected");
	}

	private static <A extends Annotation> A getAnnotation(
			Annotation[] annotations, Class<A> type) {
		for (Annotation annotation : annotations) {
			if (type.isInstance(annotation))
				return (A) annotation;
		}

		return null;
	}

}
