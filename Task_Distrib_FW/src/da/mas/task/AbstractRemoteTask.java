package da.mas.task;

import jade.core.Agent;
import jade.util.Logger;

import java.io.Serializable;

import da.mas.workers.AgentRemoteWorker;

public abstract class AbstractRemoteTask implements Serializable{
	
	//private transient Agent currentAgent = null;
	
	protected final Logger logger = Logger.getMyLogger(this.getClass().getName());
	
	protected TaskTransportDataObject  taskDataObject;
	
	public abstract void doRemoteTask();

	public TaskTransportDataObject getTaskDataObject() {
		return taskDataObject;
	}

	public void setTaskDataObject(TaskTransportDataObject taskDataObject) {
		this.taskDataObject = taskDataObject;
	}
	

}
