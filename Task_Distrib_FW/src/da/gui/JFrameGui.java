package da.gui;

import java.util.Date;

import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.MovedAgent;
import jade.gui.GuiEvent;

import javax.swing.JFrame;

import da.mas.management.AgentTaskDispatcher;
import da.mas.management.PlatformEventListener;
import da.mas.task.TaskTransportDataObject;
import da.mas.task.TaskWorkflowEvent;

public  class JFrameGui extends JFrame implements 
		PlatformEventListener, 
		TaskWorkflowEvent,
		PlatformGuiInterface
		{
	
	protected AgentTaskDispatcher guiAgent;
	
	public JFrameGui(AgentTaskDispatcher agent) {

		this.guiAgent = agent;
		agent.addPlatformEventListener(this);
		agent.addTaskWorkflowListener(this);
		
	}

	protected void  addTask(TaskTransportDataObject ttdo){
		GuiEvent event = new GuiEvent(this, 1);
		event.addParameter(ttdo);
		guiAgent.postGuiEvent(event);
	}
	
	protected void addLocalAgent(String containerId){
		GuiEvent event = new GuiEvent(this, 3);
		event.addParameter(containerId);
		guiAgent.postGuiEvent(event);
	}
	protected void addRemoteAgent(String containerId){
		GuiEvent event = new GuiEvent(this, 4);
		event.addParameter(containerId);
		guiAgent.postGuiEvent(event);
	}
	
	protected void startStopWorkflow(Object obj){
		GuiEvent event = new GuiEvent(obj, 2);
		guiAgent.postGuiEvent(event);
	}
	
	protected void setLoggingMode(String mode){
		GuiEvent event = new GuiEvent(this, 5);
		event.addParameter(mode);
		guiAgent.postGuiEvent(event);
	}

	@Override
	public void taskAdded(TaskTransportDataObject ttdo, Date createdAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforePreLocalTask(AID agentId, String taskId,
			String containerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPreLocalTask(AID agentId, String taskId, String containerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeRemoteTask(AID agentId, String taskId, String containerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterRemoteTask(AID agentId, String taskId, String containerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforePostLocalTask(AID agentId, String taskId,
			String containerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPostLocalTask(AID agentId, String taskId,
			String containerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskDone(TaskTransportDataObject ttdo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onContainerAdded(ContainerID container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onContainerRemoved(ContainerID container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAgentBorn(BornAgent bornAgent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAgentMove(MovedAgent movedAgent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAgentDead(DeadAgent deadAgent) {
		// TODO Auto-generated method stub
		
	}
	

}
