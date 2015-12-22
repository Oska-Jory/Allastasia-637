package org.oracle.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.oracle.task.Task.Priority;
import org.oracle.task.execute.FlushTask;

/**
 * @author Oska
 * Manages active tasks.
 */
public class TaskManager implements Runnable {

	
	/**
	 * The instance of the Task manager class.
	 */
	private static TaskManager instance = new TaskManager();
	
	
	/**
	 * Returns the instanced Task Manager.
	 * @return
	 */
	public static TaskManager getManager() {
		return instance;
	}
	
	
	/**
	 * A list of tasks.
	 */
	private Map<String, Task> tasks = new HashMap<String, Task>();
	
	/**
	 * The executors services used to execute a task.
	 */
	private ExecutorService highExecutor = Executors.newFixedThreadPool(10),
			mediumExecutor = Executors.newFixedThreadPool(5), lowExecutor = Executors.newSingleThreadExecutor();
	
	/**
	 * Returns a list of tasks.
	 * @return
	 */
	public Map<String, Task> getTasks() {
		return tasks;
	}
	
	
	/**
	 * Stores a new task.
	 * @param task
	 * @return
	 */
	public Task store(Task task) {
		if (tasks.containsValue(task)) {
			return null;
		} else {
			return tasks.put(task.getName(), task);
		}
	}
	
	
	/**
	 * Removes a stored task.
	 * @param task
	 * @return
	 */
	public Task remove(Task task) {
		if (!tasks.containsValue(task)) {
			return null;
		} else {
			return tasks.remove(task.getName());
		}
	}
	
	
	/**
	 * Returns a stored task.
	 * @param name
	 * @return
	 */
	public Task getTask(String name) {
		for (Task task : tasks.values()) {
			if (task.getName().equalsIgnoreCase(name)) {
				return task;
			} else {
				continue;
			}
		}
		return null;
	}
	
	
	/**
	 * Returns the executor based on the task's priority level.
	 * @param task
	 * @return
	 */
	public ExecutorService getService(Task task, Priority p) {
		switch (p) {
		case HIGH:
			if (task != null) {
				task.setExecutor(highExecutor);
			}
				return highExecutor;
		case MEDIUM:
			if (task != null) {
				task.setExecutor(mediumExecutor);
			}
				return mediumExecutor;
		case LOW:
			if (task != null) {
				task.setExecutor(lowExecutor);
			}
				return lowExecutor;
		case DEPENDANT:
			if (task != null) {
				task.setExecutor(Executors.newSingleThreadExecutor());
				return task.getExecutor();
			} else {
				return Executors.newSingleThreadExecutor();
			}
				
		}
		return null;
	}
	
	/**
	 * Creates the task manager.
	 */
	public TaskManager() {
		store(new FlushTask());
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 0, 30, TimeUnit.MILLISECONDS);
	}
	
	
	/**
	 * Begins any processes that have not begun.
	 */
	@Override
	public void run() {
		for (Task task : tasks.values()) {
			if (task.isReady() && task != null) {
				getService(task, task.getPriority()).execute(task);
			}
		}
	}
	
	
	/**
	 * Executes a temporary task.
	 * @param r
	 * @param p
	 */
	public static void execute(Runnable r, Priority p) {
		TaskManager.getManager().getService(null, p).execute(r);
	}

}
