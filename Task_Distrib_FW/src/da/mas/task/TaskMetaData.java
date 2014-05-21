package da.mas.task;

import java.io.Serializable;
import java.util.UUID;

public class TaskMetaData implements Serializable{
	
	private String taskId = UUID.randomUUID().toString();
	private String taskName;
	private String localPreTaskClass;
	private String localPostTaskClass;
	private String remoteTaskClass;
	
	private long startTime;
	private long endTime;
	
	private long beforeLeavingTime;
	
	
	public TaskMetaData() {
		// TODO Auto-generated constructor stub
	}
	
	public TaskMetaData(String taskName, String localPreTaskClass,
			String localPostTaskClass, String remoteTaskClass) {
		super();
		this.taskName = taskName;
		this.localPreTaskClass = localPreTaskClass;
		this.localPostTaskClass = localPostTaskClass;
		this.remoteTaskClass = remoteTaskClass;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getLocalPreTaskClass() {
		return localPreTaskClass;
	}
	public void setLocalPreTaskClass(String localPreTaskClass) {
		this.localPreTaskClass = localPreTaskClass;
	}
	public String getLocalPostTaskClass() {
		return localPostTaskClass;
	}
	public void setLocalPostTaskClass(String localPostTaskClass) {
		this.localPostTaskClass = localPostTaskClass;
	}
	public String getRemoteTaskClass() {
		return remoteTaskClass;
	}
	public void setRemoteTaskClass(String remoteTaskClass) {
		this.remoteTaskClass = remoteTaskClass;
	}
	public String getTaskId() {
		return taskId;
	}

	public long getBeforeLeavingTime() {
		return beforeLeavingTime;
	}

	public void setBeforeLeavingTime(long beforeLeavingTime) {
		this.beforeLeavingTime = beforeLeavingTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	

}
