package da.mas.task;

import jade.core.Agent;

import java.io.Serializable;

public class TaskTransportDataObject implements Serializable{
	
	private TaskMetaData taskMetaData;

	private AbstractTaskDataObject taskDataObject;
	
	public TaskTransportDataObject() {
		taskMetaData = new TaskMetaData();
	}

	public TaskMetaData getTaskMetaData() {
		return taskMetaData;
	}

	public void setTaskMetaData(TaskMetaData taskMetaData) {
		this.taskMetaData = taskMetaData;
	}

	public AbstractTaskDataObject getTaskDataObject() {
		return taskDataObject;
	}

	public void setTaskDataObject(AbstractTaskDataObject taskDataObject) {
		this.taskDataObject = taskDataObject;
	} 
	
}
