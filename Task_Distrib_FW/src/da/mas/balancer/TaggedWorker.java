package da.mas.balancer;

import jade.core.AID;

public class TaggedWorker {
	
	private AID AgentId;
	private long taskDuration;
	public TaggedWorker(AID agentId, long taskDuration) {
		super();
		AgentId = agentId;
		this.taskDuration = taskDuration;
	}
	public AID getAgentId() {
		return AgentId;
	}
	public long getTaskDuration() {
		return taskDuration;
	}
	
}
