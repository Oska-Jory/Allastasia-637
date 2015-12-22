package org.oracle.task.execute;

import org.oracle.task.Task;

public class FlushTask extends Task {

	public FlushTask() {
		super("flush", Priority.LOW, 27000);
	}

	@Override
	public void run() {
		System.gc();
	}

	
}
