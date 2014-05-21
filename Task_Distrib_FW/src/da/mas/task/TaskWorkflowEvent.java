package da.mas.task;

import java.util.Date;

import jade.core.AID;

public interface TaskWorkflowEvent {
	
	void taskAdded(TaskTransportDataObject ttdo, Date createdAt);
	void beforePreLocalTask(AID agentId, String taskId, String containerId);
	void afterPreLocalTask(AID agentId, String taskId, String containerId);
	void beforeRemoteTask(AID agentId, String taskId, String containerId);
	void afterRemoteTask(AID agentId, String taskId, String containerId);
	void beforePostLocalTask(AID agentId, String taskId, String containerId);
	void afterPostLocalTask(AID agentId, String taskId, String containerId);
	void taskDone(TaskTransportDataObject ttdo);

}
