/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.peripheral.bind;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dan200.computer.api.IComputerAccess;
import xfel.mods.arp.common.peripheral.AbstractPeripheral;

public abstract class AbstractAnnotatedPeripheral extends AbstractPeripheral {

	private static class BindingData {
		String[] mnames;
		Method[] methods;
	}

	private static Map<Class<? extends AbstractAnnotatedPeripheral>, BindingData> computedData = new HashMap<Class<? extends AbstractAnnotatedPeripheral>, BindingData>();

	private static BindingData getBindingData(
			Class<? extends AbstractAnnotatedPeripheral> cls) {
		BindingData bdata=computedData.get(cls);
		if(bdata!=null){
			return bdata;
		}
		
		ArrayList<String> names=new ArrayList<String>();
		ArrayList<Method> methods=new ArrayList<Method>();
		
		for(Method method:cls.getMethods()){
			if(method.isAnnotationPresent(PeripheralMethod.class)){
				PeripheralMethod ann=method.getAnnotation(PeripheralMethod.class);
				
				String name=ann.name();
				if(name.equals(PeripheralMethod.DEFAULT_NAME)){
					name=method.getName();
				}
				
				names.add(name);
				methods.add(method);
			}
		}
		
		bdata=new BindingData();
		bdata.methods=(Method[]) methods.toArray(new Method[methods.size()]);
		bdata.mnames=(String[]) names.toArray(new String[names.size()]);
		
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
			Object[] arguments) throws Exception {
		Method method=bindingData.methods[methodIndex];
		
		
		
		return null;
	}

}
