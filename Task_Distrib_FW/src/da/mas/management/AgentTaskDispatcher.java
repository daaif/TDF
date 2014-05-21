package da.mas.management;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.FileHandler;

import javax.swing.JButton;

import da.gui.JFrameGui;
import da.gui.PlatformGuiInterface;
import da.mas.balancer.TaggedWorker;
import da.mas.task.TaskTransportDataObject;
import da.mas.task.TaskWorkflowEvent;
import da.mas.workers.AgentLocalWorker;
import da.mas.workers.AgentRemoteWorker;
import da.mas.task.Workflow;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class AgentTaskDispatcher extends AgentTaskDispatcherGui {

	public static Logger logger = Logger.getMyLogger(AgentTaskDispatcher.class.getName());

	private Queue<TaskTransportDataObject> taskQueue = new LinkedList<TaskTransportDataObject>();

	private Map<String, TaskTransportDataObject> activeTasks = new HashMap<String, TaskTransportDataObject>();

	private PriorityQueue<TaggedWorker> readyRemoteWorkers = new PriorityQueue<>(
			10, new Comparator<TaggedWorker>() {

				@Override
				public int compare(TaggedWorker o1, TaggedWorker o2) {
					if (o1.getTaskDuration() > o2.getTaskDuration())
						return 1;
					else if (o1.getTaskDuration() < o2.getTaskDuration())
						return -1;
					return 0;
				}
			});

	private List<AID> readyLocalWorkers = new ArrayList<AID>();

	private List<TaskWorkflowEvent> taskListeners = new ArrayList<TaskWorkflowEvent>();

	private int localAgentsCount = 0;
	private int remoteAgentsCount = 0;
	private boolean start = false;
	private transient PlatformGuiInterface gui;
	private Agent _this = this;
	private long maxTime = 0;

	@Override
	protected void onGuiEvent(GuiEvent event) {
		/*
		 * logger.log(Logger.INFO, (new LoggingAgentInfo("platform",
		 * "Event from GUI type:" + event.getType(), _this)).toString());
		 */
		switch (event.getType()) {
		/* Task management */
		case 1: // add Task
			TaskTransportDataObject ttdo = (TaskTransportDataObject) event
					.getParameter(0);
			taskQueue.add(ttdo);
			//System.out.println("Task Added ---> "
			//		+ ttdo.getTaskMetaData().getTaskName() + " : "
			//		+ ttdo.getTaskMetaData().getTaskId());
			for (TaskWorkflowEvent twe : taskListeners) {
				twe.taskAdded(ttdo,
						new Date());
			}
			// logger.info("Task Queue size : " + taskQueue.size());
			break;
		case 2: // start/stop workflow
			// JButton startButton = (JButton) event.getSource();
			start = !start;
			/*
			 * if (start) startButton.setText("stop"); else
			 * startButton.setText("start");
			 */
			break;
		/* Platform management */
		case 3: // add local agent worker
			String containerId1 = (String) event.getParameter(0);
			addLocalAgent(containerId1);
			break;
		case 4: // add remote agent worker
			String containerId2 = (String) event.getParameter(0);
			addRemoteAgent(containerId2);
			break;
		case 5: // set mode dev/config/prod
			String mode = (String) event.getParameter(0);
			if (mode.equalsIgnoreCase("dev"))
				logger.setLevel(Logger.FINE);
			else if (mode.equalsIgnoreCase("config"))
				logger.setLevel(Logger.CONFIG);
			else
				logger.setLevel(Logger.INFO);
			break;
		}

	}

	private void addRemoteAgent(String containerId) {
		try {
			AgentController ac = getContainerController().createNewAgent(
					"RW_" + ++remoteAgentsCount,
					AgentRemoteWorker.class.getName(),
					new Object[] { containerId });
			ac.start();
		} catch (StaleProxyException e) {
			--remoteAgentsCount;
			e.printStackTrace();
		}
	}

	private void addLocalAgent(String containerId) {
		try {
			AgentController ac = getContainerController().createNewAgent(
					"LW_" + ++localAgentsCount,
					AgentLocalWorker.class.getName(),
					new Object[] { containerId });
			ac.start();
		} catch (StaleProxyException e) {
			--localAgentsCount;
			e.printStackTrace();
		}
	}

	@Override
	protected void setup() {
		Object[] args = getArguments();
		String guiClass = (String) args[0];
		try {
			logger.setLevel(Logger.FINE);
			FileHandler fileHandler = new FileHandler("logs/dispatcher.log");
			logger.addHandler(fileHandler);
			logger.setLevel(Logger.CONFIG);
			FileHandler fileHandler1 = new FileHandler("logs/app-config.log");
			logger.addHandler(fileHandler1);
		} catch (SecurityException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Constructor<?> c = Class.forName(guiClass).getConstructor(
					AgentTaskDispatcher.class);
			gui = (PlatformGuiInterface) c.newInstance(this);
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ParallelBehaviour pb = new ParallelBehaviour();

		/** Manage task events */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.TASK_WORKFLOW);
				ACLMessage message = receive(template);
				if (message != null) {
					String[] contents = message.getContent().split(",");
					String content = contents[0];
					String taskId = contents[1];
					String containerId = contents[2];
					if (content.equals(Workflow.BEFORE_PRE_LOCAL_TASK)) {
						beforePreLocalTask(message.getSender(), taskId,
								containerId);
					} else if (content.equals(Workflow.AFTER_PRE_LOCAL_TASK)) {
						afterPreLocalTask(message.getSender(), taskId,
								containerId);
					} else if (content.equals(Workflow.BEFORE_REMOTE_TASK)) {
						beforeRemoteTask(message.getSender(), taskId,
								containerId);
					} else if (content.equals(Workflow.AFTER_REMOTE_TASK)) {
						afterRemoteTask(message.getSender(), taskId,
								containerId);
					} else if (content.equals(Workflow.BEFORE_POST_LOCAL_TASK)) {
						beforePostLocalTask(message.getSender(), taskId,
								containerId);
					} else if (content.equals(Workflow.AFTER_POST_LOCAL_TASK)) {
						afterPostLocalTask(message.getSender(), taskId,
								containerId);
					}

				} else
					block();
			}
		});

		/** add ready new created remote worker */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.READY_REMOTE_AGENT_INIT);
				ACLMessage message = receive(template);
				if (message != null) {
					readyRemoteWorkers.add(new TaggedWorker(
							message.getSender(), 0));
					//System.out.println("Remote workers : "
					//		+ readyRemoteWorkers.size());
				} else
					block();
			}
		});

		/** Task aborted in remote due to timeout */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.TASK_ABORTED_IN_REMOTE);
				ACLMessage message = receive(template);
				if (message != null) {
					String taskIdString = message.getContent();
					String taskId = getUUIDFromString(taskIdString);
					taskQueue.add(activeTasks.get(taskId));
					activeTasks.remove(taskId);
					readyRemoteWorkers.add(new TaggedWorker(
							message.getSender(), maxTime + 100));
				} else
					block();
			}
		});

		/** add ready local worker */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.READY_LOCAL_AGENT);
				ACLMessage message = receive(template);
				if (message != null) {
					readyLocalWorkers.add(message.getSender());
					//System.out.println("Localworkers : "
					//		+ readyLocalWorkers.size());
				} else
					block();
			}
		});

		/** add ready new created local worker */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.READY_LOCAL_AGENT_INIT);
				ACLMessage message = receive(template);
				if (message != null) {
					readyLocalWorkers.add(message.getSender());
					//System.out.println("Localworkers : "
					//		+ readyLocalWorkers.size());
				} else
					block();
			}
		});

		/** task duration message */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Workflow.TASK_DONE);
				ACLMessage message = receive(template);
				if (message != null) {
					TaskTransportDataObject ttdo;
					try {
						ttdo = (TaskTransportDataObject) message.getContentObject();
						taskDone(ttdo);
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					block();
			}
		});

		/** add ready remote worker */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.TASK_FROM_REMOTE_TO_LOCAL);
				ACLMessage message = receive(template);
				if (message != null) {
					try {
						TaskTransportDataObject objectMetaData = (TaskTransportDataObject) message
								.getContentObject();
						AID agentId;
						long interval = 0;
						agentId = message.getSender();
						interval = System.currentTimeMillis()
								- objectMetaData.getTaskMetaData()
										.getBeforeLeavingTime();
						if (interval > maxTime)
							maxTime = interval;
						readyRemoteWorkers.add(new TaggedWorker(agentId,
								interval));
						//System.out.println("Remote workers : "
						//		+ readyRemoteWorkers.size());
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					block();
			}
		});

		/** execute task */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				while (start && readyRemoteWorkers.size() > 0
						&& taskQueue.size() > 0 && readyLocalWorkers.size() > 0) {
					
					AID localAgent = getNextLocalWorker();
					TaggedWorker taggedWorker = getNextRemoteWorker();
					if(localAgent==null || taggedWorker==null)
						continue;
					
					TaskTransportDataObject task = taskQueue.poll();
					task.getTaskMetaData().setStartTime(
							System.currentTimeMillis());
					activeTasks.put(task.getTaskMetaData().getTaskId(), task);
					
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.setOntology(Ontology.TASK_FROM_DISPATCHER_TO_LOCAL);
					message.addReceiver(localAgent);
					try {
						message.setContentObject(new Object[] { task,
								taggedWorker.getAgentId() });
						send(message);
						// System.out.println("Begin Task : " +
						// task.getTaskMetaData().getTaskName());
					} catch (IOException e) {
						activeTasks.remove(task.getTaskMetaData().getTaskId());
						taskQueue.add(task);
						readyLocalWorkers.add(localAgent);
						readyRemoteWorkers.add(taggedWorker );
						e.printStackTrace();
					}
				}
				block(10);
			}
		});

		/** request for proposal from remote worker */
		pb.addSubBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				MessageTemplate template = MessageTemplate
						.MatchOntology(Ontology.REQUEST_FOR_READY_LOCAL_AGENT);
				ACLMessage message = receive(template);
				if (message != null) {
					//System.out
					//		.println("Message received : request for local agent.");
					AID availableLocalAgent = getNextLocalWorker();
					if (availableLocalAgent != null) {
						ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
						response.setOntology(Ontology.PROPOSAL_FOR_READY_LOCAL_AGENT);
						response.addReceiver(message.getSender());
						response.setContent(availableLocalAgent.getLocalName());
						send(response);
					} else {
						ACLMessage response = new ACLMessage(ACLMessage.INFORM);
						response.setOntology(Ontology.NOT_AVAILABLE_WAIT);
						response.addReceiver(message.getSender());
						send(response);
					}

				} else
					block();

			}
		});

		addBehaviour(pb);

		super.setup();
	}

	private void taskDone(TaskTransportDataObject ttdo) {
		for (TaskWorkflowEvent twe : taskListeners)
			twe.taskDone(ttdo);
	}

	protected String getUUIDFromString(String taskIdString) {
		for (String id : activeTasks.keySet()) {
			if (id.toString().equals(taskIdString))
				return id;
		}
		return null;
	}

	public void addTaskWorkflowListener(TaskWorkflowEvent twl) {
		taskListeners.add(twl);
	}

	public void removeTaskWorkflowListener(TaskWorkflowEvent twl) {
		if (taskListeners.contains(twl))
			taskListeners.remove(twl);
	}

	private void beforePreLocalTask(AID agentId, String taskId,
			String containerId) {
		for (TaskWorkflowEvent twe : taskListeners)
			twe.beforePreLocalTask(agentId, taskId, containerId);
		if (logger.isLoggable(Logger.FINE))
			logger.fine(containerId + "," + agentId.getName() + "," + taskId);
		if (logger.isLoggable(Logger.CONFIG))
			logger.config(containerId + "," + agentId.getName() + "," + taskId);
	}

	private void afterPreLocalTask(AID agentId, String taskId,
			String containerId) {
		for (TaskWorkflowEvent twe : taskListeners)
			twe.afterPreLocalTask(agentId, taskId, containerId);
		if (logger.isLoggable(Logger.FINE))
			logger.fine(containerId + "," + agentId.getName() + "," + taskId);
	}

	private void beforeRemoteTask(AID agentId, String taskId, String containerId) {
		for (TaskWorkflowEvent twe : taskListeners)
			twe.beforeRemoteTask(agentId, taskId, containerId);
		if (logger.isLoggable(Logger.FINE))
			logger.fine(containerId + "," + agentId.getName() + "," + taskId);
	}

	private void afterRemoteTask(AID agentId, String taskId, String containerId) {
		for (TaskWorkflowEvent twe : taskListeners)
			twe.afterRemoteTask(agentId, taskId, containerId);
		if (logger.isLoggable(Logger.FINE))
			logger.fine(containerId + "," + agentId.getName() + "," + taskId);
	}

	private void beforePostLocalTask(AID agentId, String taskId,
			String containerId) {
		for (TaskWorkflowEvent twe : taskListeners)
			twe.beforePostLocalTask(agentId, taskId, containerId);
		if (logger.isLoggable(Logger.FINE))
			logger.fine(containerId + "," + agentId.getName() + "," + taskId);
	}

	private void afterPostLocalTask(AID agentId, String taskId,
			String containerId) {
		for (TaskWorkflowEvent twe : taskListeners)
			twe.afterPostLocalTask(agentId, taskId, containerId);
		if (logger.isLoggable(Logger.FINE))
			logger.fine(containerId + "," + agentId.getName() + "," + taskId);
		if (logger.isLoggable(Logger.CONFIG))
			logger.config(containerId + "," + agentId.getName() + "," + taskId);
	}

	private AID getNextLocalWorker() {
		while(true){
			if(readyLocalWorkers.size() == 0) 
				return null;
			AID agentId = readyLocalWorkers.remove(0);
			if(availableAgents.contains(agentId))
				return agentId;		
		}
	}

	private TaggedWorker getNextRemoteWorker() {
		while(true){
			if(readyRemoteWorkers.size() == 0) 
				return null;
			TaggedWorker tw = readyRemoteWorkers.poll();
			if(availableAgents.contains(tw.getAgentId()))
				return tw;		
		}
	}
	
}
