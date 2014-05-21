package da.mas.workers;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import da.mas.management.Ontology;
import da.mas.task.AbstractLocalPostTask;
import da.mas.task.AbstractLocalPreTask;
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

public class AgentLocalWorker extends Agent {

	private Logger logger = Logger.getMyLogger(this.getClass().getName());

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

		/** Waiting for task from Dispatcher */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.TASK_FROM_DISPATCHER_TO_LOCAL);
				ACLMessage message = receive(template);
				if (message != null) {
					try {
						Object[] data = (Object[]) message.getContentObject();
						TaskTransportDataObject ttdo = (TaskTransportDataObject) data[0];
						AID remoteAgent = (AID) data[1];
						String localPreTaskClass = ttdo.getTaskMetaData()
								.getLocalPreTaskClass();
						AbstractLocalPreTask alpt = (AbstractLocalPreTask) Class
								.forName(localPreTaskClass).newInstance();
						alpt.setTaskDataObject(ttdo);
						sendEventInfo(Workflow.BEFORE_PRE_LOCAL_TASK, ttdo
								.getTaskMetaData().getTaskId());
						alpt.doLocalPreTask();
						sendEventInfo(Workflow.AFTER_PRE_LOCAL_TASK, ttdo
								.getTaskMetaData().getTaskId());
						ttdo.getTaskMetaData().setBeforeLeavingTime(System.currentTimeMillis());
						ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
						msg.setOntology(Ontology.TASK_FROM_LOCAL_TO_REMOTE);
						msg.addReceiver(remoteAgent);
						msg.setContentObject(ttdo);
						send(msg);
						sendReadyMessageToDispatcher(Ontology.READY_LOCAL_AGENT);

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
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					block();

			}
		});

		/** Waiting for response from remote worker */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.TASK_FROM_REMOTE_TO_LOCAL);
				ACLMessage message = receive(template);
				if (message != null) {
					try {
						TaskTransportDataObject ttdo = (TaskTransportDataObject) message
								.getContentObject();
						String localPostTaskClass = ttdo.getTaskMetaData()
								.getLocalPostTaskClass();
						AbstractLocalPostTask alpt = (AbstractLocalPostTask) Class
								.forName(localPostTaskClass).newInstance();
						alpt.setTaskDataObject(ttdo);
						sendEventInfo(Workflow.BEFORE_POST_LOCAL_TASK, ttdo
								.getTaskMetaData().getTaskId());
						alpt.doLocalPostTask();
						ttdo.getTaskMetaData().setEndTime(System.currentTimeMillis());
						sendTaskDone(ttdo);
						sendEventInfo(Workflow.AFTER_POST_LOCAL_TASK, ttdo
								.getTaskMetaData().getTaskId());
						sendReadyMessageToDispatcher(Ontology.READY_LOCAL_AGENT);

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

		addBehaviour(pb);
	}

	protected void sendTaskDone(TaskTransportDataObject ttdo) {
		long millis = ttdo.getTaskMetaData().getEndTime() -
				ttdo.getTaskMetaData().getStartTime();
		try {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Workflow.TASK_DONE);
			msg.setContentObject(ttdo);
			msg.addReceiver(new AID("agent_dispatcher", AID.ISLOCALNAME));
			send(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sendReadyMessageToDispatcher(String ontology) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(ontology);
		msg.addReceiver(new AID("agent_dispatcher", AID.ISLOCALNAME));
		send(msg);
	}

	private void sendEventInfo(String content, String taskId) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(Ontology.TASK_WORKFLOW);
		msg.setContent(content + "," + taskId + "," + here().getID());
		msg.addReceiver(new AID("agent_dispatcher", AID.ISLOCALNAME));
		send(msg);

	}

	@Override
	protected void afterMove() {
		sendReadyMessageToDispatcher(Ontology.READY_LOCAL_AGENT_INIT);
		super.afterMove();
	}
}
