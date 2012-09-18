/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.peripheral.bind;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a peripheral-callable method
 * 
 * @author Xfel
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
@Documented
public @interface PeripheralMethod {
	String DEFAULT_NAME = "##default##";

	/**
	 * The name of the method from the lua point of view. By default, the java method name will be used.
	 * @return
	 */
	String name() default DEFAULT_NAME;
	
	boolean async() default false;
}
