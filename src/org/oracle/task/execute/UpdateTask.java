package org.oracle.task.execute;

import org.oracle.Launcher;
import org.oracle.entity.user.User;
import org.oracle.task.Task;

public class UpdateTask extends Task {

	public UpdateTask(String name, Priority priority, int delay) {
		super("update-task", Priority.LOW, 600);
	}

	@Override
	public void run() {
		for (User user : Launcher.getWorld().getUsers()) {
			user.getGpi().sendUpdate();
		}
		
	}
	

}
