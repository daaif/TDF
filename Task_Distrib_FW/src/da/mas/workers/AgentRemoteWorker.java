package da.mas.workers;

import java.io.IOException;
import java.util.UUID;

import da.mas.management.Ontology;
import da.mas.task.AbstractRemoteTask;
import da.mas.task.TaskTransportDataObject;
import da.mas.task.Workflow;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class AgentRemoteWorker extends Agent {

	private TaskTransportDataObject ttdo = null;
	
	private int attempt = 0;

	Logger logger = Logger.getMyLogger(this.getClass().getName());

	@Override
	protected void setup() {
		Object[] args = getArguments();
		final String containerDestinationId = (String) args[0];
		
		ParallelBehaviour pb = new ParallelBehaviour();

		pb.addSubBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				myAgent.doMove(new ContainerID(containerDestinationId, null));
			}
		});

		/** waiting for new task */
		pb.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.TASK_FROM_LOCAL_TO_REMOTE);
				ACLMessage message = receive(template);
				if (message != null) {
					try {
						TaskTransportDataObject ttdo = (TaskTransportDataObject) message
								.getContentObject();
						String remoteTaskClass = ttdo.getTaskMetaData()
								.getRemoteTaskClass();
						AbstractRemoteTask art = (AbstractRemoteTask) Class
								.forName(remoteTaskClass).newInstance();
						art.setTaskDataObject(ttdo);
						// art.setCurrentAgent(_this);
						sendEventInfo(Workflow.BEFORE_REMOTE_TASK, ttdo
								.getTaskMetaData().getTaskId());
						art.doRemoteTask();
						sendEventInfo(Workflow.AFTER_REMOTE_TASK, ttdo
								.getTaskMetaData().getTaskId());
						setTempTaskTransportObject(ttdo);
						//System.out.println("Remote task executed succefully.");
						requestForAvalaibleLocalAgent();

					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					block();
			}

		});
		
		/** waiting for local worker proposal */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {

				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.PROPOSAL_FOR_READY_LOCAL_AGENT);
				ACLMessage response = receive(template);
				if (response != null) {
					try {
						String localAgentName = (String) response.getContent();
						AID localAgent = new AID(localAgentName,
								AID.ISLOCALNAME);
						ACLMessage result = new ACLMessage(ACLMessage.CONFIRM);
						result.setOntology(Ontology.TASK_FROM_REMOTE_TO_LOCAL);
						result.setContentObject(getTempTaskTransportObject());
						result.addReceiver(localAgent);
						result.addReceiver(new AID("agent_dispatcher", AID.ISLOCALNAME));
						send(result);
						attempt = 0;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					block();

			}
		});
		
		/** local agent not available. wait and try again */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {

				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.NOT_AVAILABLE_WAIT);
				ACLMessage response = receive(template);
				if (response != null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(attempt++<4){
						requestForAvalaibleLocalAgent();
					}else{
						sendAbortMessage();
					}
				} else
					block();

			}
		});

		addBehaviour(pb);
	}

	protected void sendAbortMessage() {
		String taskId = getTempTaskTransportObject().getTaskMetaData().getTaskId();
		ACLMessage message = new ACLMessage(ACLMessage.CANCEL);
		message.setOntology(Ontology.TASK_ABORTED_IN_REMOTE);
		message.setContent(taskId.toString());
		message.addReceiver(new AID("agent_dispatcher",
				AID.ISLOCALNAME));
		send(message);
	}

	protected void requestForAvalaibleLocalAgent() {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setOntology(Ontology.REQUEST_FOR_READY_LOCAL_AGENT);
		request.addReceiver(new AID("agent_dispatcher",
				AID.ISLOCALNAME));
		send(request);
	}

	private void setTempTaskTransportObject(TaskTransportDataObject ttdo) {
		this.ttdo = ttdo;
	}

	private TaskTransportDataObject getTempTaskTransportObject() {
		return this.ttdo;
	}


	private void sendEventInfo(String content, String taskId) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(Ontology.TASK_WORKFLOW);
		msg.setContent(content + "," + taskId + "," + here().getID());
		msg.addReceiver(new AID("agent_dispatcher", AID.ISLOCALNAME));
		send(msg);

	}

	@Override
	protected void beforeMove() {
		//System.out.println("Agent " + getLocalName() + " will move");
		super.beforeMove();
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(Ontology.READY_REMOTE_AGENT_INIT);
		msg.addReceiver(new AID("agent_dispatcher", AID.ISLOCALNAME));
		send(msg);
	}
}
