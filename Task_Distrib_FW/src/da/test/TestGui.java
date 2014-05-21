package da.test;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;

import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.MovedAgent;
import da.gui.JFrameGui;
import da.mas.management.AgentTaskDispatcher;
import da.mas.task.AbstractTaskDataObject;
import da.mas.task.TaskMetaData;
import da.mas.task.TaskTransportDataObject;
import da.mas.workers.AgentLocalWorker;

public class TestGui extends JFrameGui {
	private JList<AID> jListLocalWorkers;
	private DefaultListModel<AID> localWorkersModel = new DefaultListModel<AID>();

	public TestGui(AgentTaskDispatcher agent) {
		super(agent);
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(10, 10, 360, 220);

		JButton buttonStart = new JButton("Start");
		JButton buttonAddTask = new JButton("Add Task");
		JButton buttonAddRemoteAgent = new JButton("Add Remote");
		JButton buttonAddLocalAgnet = new JButton("Add Local");

		jListLocalWorkers = new JList<AID>(localWorkersModel);

		buttonStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startStopWorkflow(e.getSource());
				JButton button = (JButton) e.getSource();
				if(button.getText().equalsIgnoreCase("start"))
					button.setText("stop");
				else
					button.setText("start");
			}
		});
		buttonAddTask.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File("images");
				for (File f : file.listFiles()) {
					TaskTransportDataObject ttdo = new TaskTransportDataObject();
					ttdo.setTaskMetaData(new TaskMetaData("task",
							DummyLocalPreTask.class.getName(),
							DummyLocalPostTask.class.getName(),
							DummyRemoteTask.class.getName()));
					DataObject dataObject = new DataObject(f.getAbsolutePath(), 8);
					ttdo.setTaskDataObject(dataObject);
					addTask(ttdo);
				}
			}
		});
		buttonAddLocalAgnet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addLocalAgent("LOCAL_CONTAINER");
			}
		});
		buttonAddRemoteAgent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addRemoteAgent("REMOTE_CONTAINER");
			}
		});

		this.add(buttonStart);
		this.add(buttonAddTask);
		this.add(buttonAddLocalAgnet);
		this.add(buttonAddRemoteAgent);
		this.add(jListLocalWorkers);

		this.setVisible(true);
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
		if (bornAgent.getClassName().equals(AgentLocalWorker.class.getName()))
			localWorkersModel.addElement(bornAgent.getAgent());
	}

	@Override
	public void onAgentDead(DeadAgent deadAgent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAgentMove(MovedAgent movedAgent) {
		System.out.println("Agent moved from " + movedAgent.getFrom().getName()
				+ " to : " + movedAgent.getTo().getName());

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
	public void taskAdded(TaskTransportDataObject ttdo, Date createdAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void taskDone(TaskTransportDataObject ttdo) {
		// TODO Auto-generated method stub
		
	}

	
	
}
