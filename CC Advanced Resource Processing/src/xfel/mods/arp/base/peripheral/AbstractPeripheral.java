/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.peripheral;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

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

	/**
	 * A task which is started by a peripheral method, but should be executed on
	 * the minecraft update thread.
	 * 
	 * <br/>
	 * 
	 * The following lua code could call an async function:
	 * 
	 * <pre>
	 * function async(...)
	 * 	local taskId = peripehral.call("myasyncfunc",...)
	 * 	
	 * 	local evt, id, success, param
	 * 	while id ~= taskId do
	 * 		evt, id, success, param = os.pullEvent("task_result")
	 * 	end
	 * 	
	 * 	if success then
	 * 		if param then
	 * 			return unpack(param)
	 * 		end
	 * 	else 
	 * 		error(param)
	 * 	end
	 * end
	 * </pre>
	 * 
	 * @author Xfel
	 * 
	 */
	protected static abstract class Task {
		int id;
		IComputerAccess computer;

		/**
		 * Default constructor
		 */
		protected Task() {
		}

		/**
		 * Returns the task id
		 * 
		 * @return the task id
		 */
		protected final int getId() {
			return id;
		}

		/**
		 * Returns the computer that requested this task and will therefore
		 * recieve the results.
		 * 
		 * @return the owning computer
		 */
		protected final IComputerAccess getComputer() {
			return computer;
		}

		/**
		 * Implement your task in this method. return values and exceptions are
		 * treated like in
		 * {@link IPeripheral#callMethod(IComputerAccess, int, Object[])}
		 * 
		 * @return some results to post to
		 * @throws Exception
		 *             if an error occurs
		 * 
		 */
		protected abstract Object[] execute() throws Exception;

		// executes the task and posts a result event
		void doExecute() {
			try {
				Object[] result = execute();

				Object[] eventParams = { Integer.valueOf(id), Boolean.TRUE,
						null };

				if (result != null && result.length > 0) {
					Map<Integer, Object> table = new HashMap<Integer, Object>(
							result.length);
					for (int i = 0; i < eventParams.length; i++) {
						table.put(Integer.valueOf(i + 1), result[i]);
					}
					eventParams[2] = table;
				}

				computer.queueEvent("task_result", eventParams);
			} catch (Exception e) {
				Object[] eventParams = { Integer.valueOf(id), Boolean.FALSE,
						e.getMessage() };
				computer.queueEvent("task_result", eventParams);
			}
		}
	}

	public static String getStringArg(Object[] arguments, int index) {
		if (index < arguments.length && arguments[index] != null) {
			return arguments[index].toString();
		}
		throw new IllegalArgumentException("arg " + (index + 1)
				+ ": string expected");
	}

	public static String getStringArg(Object[] arguments, int index, String def) {
		if (index < arguments.length && arguments[index] != null) {
			return arguments[index].toString();
		}
		return def;
	}

	public static Number getNumberArg(Object[] arguments, int index) {
		if (index < arguments.length) {
			if (arguments[index] instanceof Number) {
				return (Number) arguments[index];
			} else if (arguments[index] instanceof String) {
				// lua tonumber adaption
				try {
					return Double.parseDouble((String) arguments[index]);
				} catch (NumberFormatException e) {
					// parse failed.. throw error
				}
			}
		}

		throw new IllegalArgumentException("arg " + (index + 1)
				+ ": number expected");
	}

	public static Number getNumberArg(Object[] arguments, int index, Number def) {
		if (index < arguments.length) {
			if (arguments[index] instanceof Number) {
				return (Number) arguments[index];
			} else if (arguments[index] instanceof String) {
				try {
					return Double.parseDouble((String) arguments[index]);
				} catch (NumberFormatException e) {
					// parse failed.. throw error
				}
			}
		}

		return def;
	}

	public static boolean getBooleanArg(Object[] arguments, int index) {
		if (index >= arguments.length || arguments[index] == null
				|| Boolean.FALSE.equals(arguments[index])) {
			return false;
		}

		return true;
	}

	public static Map<?, ?> getTableArg(Object[] arguments, int index) {
		if (index < arguments.length && arguments[index] instanceof Map) {
			return (Map<?, ?>) arguments[index];
		}

		return null;
	}

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

	private final AtomicInteger taskIds = new AtomicInteger();

	private Queue<Task> taskQueue = new LinkedList<Task>();

	protected int queueTask(IComputerAccess computer, Task task) {
		int taskId = task.id = taskIds.incrementAndGet();
		task.computer = computer;
		synchronized (taskQueue) {
			taskQueue.add(task);
		}
		return taskId;
	}

	@Override
	public void update() {
		Task task;
		synchronized (taskQueue) {
			task = taskQueue.poll();
		}
		if (task != null) {
			task.doExecute();
		}
	}
}
