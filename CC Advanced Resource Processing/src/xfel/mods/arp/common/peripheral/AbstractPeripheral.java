/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.peripheral;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import dan200.turtle.api.ITurtlePeripheral;

/**
 * Abstract implementation of {@link IPeripheral} and {@link ITurtlePeripheral}.
 * 
 * @author Xfel
 * 
 */
public abstract class AbstractPeripheral implements ITurtlePeripheral {

	private Map<IComputerAccess, String> attachedComputers;

	/**
	 * The peripheral type name
	 */
	protected String type;

	protected AbstractPeripheral(String type) {
		this.type = type;
		attachedComputers = new HashMap<IComputerAccess, String>();
	}

	protected AbstractPeripheral() {
		attachedComputers = new HashMap<IComputerAccess, String>();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #type
	 */
	@Override
	public String getType() {
		return type;
	}

	@Override
	public void attach(IComputerAccess computer, String computerSide) {
		attachedComputers.put(computer, computerSide);
	}

	@Override
	public void detach(IComputerAccess computer) {
		attachedComputers.remove(computer);
	}

	/**
	 * Returns a list of all currently attached computers.
	 * 
	 * @return the computer list
	 * @see #attach(IComputerAccess, String)
	 * @see #detach(IComputerAccess)
	 */
	public Collection<IComputerAccess> getAttachedComputers() {
		return new ArrayList<IComputerAccess>(attachedComputers.keySet());
	}

	/**
	 * Sends an event to all attached computers.
	 * 
	 * @param event
	 *            the event name
	 * @param args
	 *            the event arguments
	 * @see IComputerAccess#queueEvent(String, Object[])
	 */
	protected void queueEvent(String event, Object... args) {
		for (IComputerAccess computer : attachedComputers.keySet()) {
			computer.queueEvent(event, args);
		}
	}

	/**
	 * Sends an event to all attached computers. The arguments are prepended
	 * with the side the computer is attached to.
	 * 
	 * @param event
	 *            the event name
	 * @param args
	 *            the event arguments
	 * @see IComputerAccess#queueEvent(String, Object[])
	 */
	protected void queueSidedEvent(String event, Object... args) {
		Object[] sidedArgs = new Object[args.length + 1];
		System.arraycopy(args, 0, sidedArgs, 1, args.length);

		for (Map.Entry<IComputerAccess, String> entry : attachedComputers
				.entrySet()) {
			sidedArgs[0] = entry.getValue();
			entry.getKey().queueEvent(event, sidedArgs);
		}
	}

	/**
	 * {@inheritDoc} <br>
	 * This default implementation returns always <code>true</code>.
	 */
	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void update() {
	}
}
