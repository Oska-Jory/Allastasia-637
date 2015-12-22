package org.oracle.task;

import java.util.concurrent.ExecutorService;

/**
 * @author Oska
 * Represents a task that will be processed.
 */
public abstract class Task implements Runnable {

	/**
	 * The priority level of the task
	 */
	private Priority priority;
	
	/**
	 * The delay of the task.
	 */
	private int delay;
	
	/**
	 * The name of the task.
	 */
	private String name;
	
	/**
	 * The execution service that executes the task.
	 */
	private ExecutorService executor = null;
	
	/**
	 * The last time the Process was run.
	 */
	private long lastRun;
	
	
	/**
	 * Creates a new task.
	 * @param name
	 * @param priority
	 * @param delay
	 */
	public Task(String name, Priority priority, int delay) {
		this.name = name;
		this.priority = priority;
		this.delay = delay;
	}
	
	
	public enum Priority {
		HIGH,
		MEDIUM,
		LOW,
		DEPENDANT;
	}
	
	
	/**
	 * Returns the task's priority.
	 * @return
	 */
	public Priority getPriority() {
		return priority;
	}
	
	
	/**
	 * Returns the delay time.
	 * @return
	 */
	public int getDelay() {
		return delay;
	}
	
	
	/**
	 * Sets the time the task was executed last.
	 */
	public void setLastRun() {
		this.lastRun = System.currentTimeMillis();
	}
	
	
	/**
	 * Checks if the task is ready to be executed again.
	 * @return
	 */
	public boolean isReady() {
		return ((System.currentTimeMillis() - lastRun) > delay);
	}
	
	
	/**
	 * Returns the execution service.
	 * @return
	 */
	public ExecutorService getExecutor() {
		return executor;
	}
	
	
	/**
	 * Sets the execution service for the task.
	 * @param executor
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
	
	
	/**
	 * Returns the task's name.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
}
