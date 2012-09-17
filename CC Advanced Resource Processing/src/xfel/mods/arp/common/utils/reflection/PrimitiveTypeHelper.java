/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.utils.reflection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

/**
 * @author Xfel
 * 
 */
public abstract class PrimitiveTypeHelper {

	private static Map<Class<?>, PrimitiveTypeHelper> helperMap = new HashMap<Class<?>, PrimitiveTypeHelper>();

	static {
		new IntHelper();
		new BooleanHelper();
		new DoubleHelper();
		new FloatHelper();
		new ByteHelper();
		new ShortHelper();
		new LongHelper();
	}

	public static PrimitiveTypeHelper getInstance(Class<?> cls) {
		return helperMap.get(cls);
	}

	private Class<?> type;
	private Class<?> wrapper;

	protected PrimitiveTypeHelper(Class<?> type, Class<?> wrapper) {
		super();
		this.type = type;
		this.wrapper = wrapper;

		helperMap.put(type, this);
		helperMap.put(wrapper, this);
	}

	/**
	 * Will convert the given object to this primitive type. Eg.: if we are the
	 * int helper, convertToType(any instance of Number) returns and Integer
	 * object.
	 * 
	 * @param obj 
	 * @return
	 */
	public Object convertToType(Object obj){
		try{
			return doConvertToType(obj);
		}catch(ClassCastException e){
			throw new RuntimeException(type.getName()+" expected");
		}
	}
	
	protected abstract Object doConvertToType(Object obj);

	public abstract Object convertToTypeArray(Collection<?> collection);

}

class IntHelper extends PrimitiveTypeHelper {

	public IntHelper() {
		super(Integer.TYPE, Integer.class);
	}

	@Override
	protected Object doConvertToType(Object obj) {
		return new Integer(((Number) obj).intValue());
	}

	@Override
	public Object convertToTypeArray(Collection<?> collection) {
		return Ints.toArray((Collection<? extends Number>) collection);
	}

}

class BooleanHelper extends PrimitiveTypeHelper {

	public BooleanHelper() {
		super(Boolean.TYPE, Boolean.class);
	}

	@Override
	protected Object doConvertToType(Object obj) {
		return (Boolean) obj;
	}

	@Override
	public Object convertToTypeArray(Collection<?> collection) {
		return Booleans.toArray((Collection<Boolean>) collection);
	}

}

class DoubleHelper extends PrimitiveTypeHelper {

	public DoubleHelper() {
		super(Double.TYPE, Double.class);
	}

	@Override
	protected Object doConvertToType(Object obj) {
		return new Double(((Number) obj).doubleValue());
	}

	@Override
	public Object convertToTypeArray(Collection<?> collection) {
		return Doubles.toArray((Collection<? extends Number>) collection);
	}

}

class FloatHelper extends PrimitiveTypeHelper {

	public FloatHelper() {
		super(Float.TYPE, Float.class);
	}

	@Override
	protected Object doConvertToType(Object obj) {
		return new Float(((Number) obj).floatValue());
	}

	@Override
	public Object convertToTypeArray(Collection<?> collection) {
		return Floats.toArray((Collection<? extends Number>) collection);
	}

}

class LongHelper extends PrimitiveTypeHelper {

	public LongHelper() {
		super(Long.TYPE, Long.class);
	}

	@Override
	protected Object doConvertToType(Object obj) {
		return new Long(((Number) obj).longValue());
	}

	@Override
	public Object convertToTypeArray(Collection<?> collection) {
		return Longs.toArray((Collection<? extends Number>) collection);
	}

}

class ShortHelper extends PrimitiveTypeHelper {

	public ShortHelper() {
		super(Short.TYPE, Short.class);
	}

	@Override
	protected Object doConvertToType(Object obj) {
		return new Short(((Number) obj).shortValue());
	}

	@Override
	public Object convertToTypeArray(Collection<?> collection) {
		return Shorts.toArray((Collection<? extends Number>) collection);
	}

}

class ByteHelper extends PrimitiveTypeHelper {

	public ByteHelper() {
		super(Byte.TYPE, Byte.class);
	}

	@Override
	protected Object doConvertToType(Object obj) {
		return new Byte(((Number) obj).byteValue());
	}

	@Override
	public Object convertToTypeArray(Collection<?> collection) {
		return Bytes.toArray((Collection<? extends Number>) collection);
	}

}
