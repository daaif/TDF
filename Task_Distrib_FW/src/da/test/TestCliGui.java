package da.test;

import java.io.File;

import da.gui.CliGui;
import da.mas.management.AgentTaskDispatcher;
import da.mas.management.PlatformManager;
import da.mas.task.TaskMetaData;
import da.mas.task.TaskTransportDataObject;

public class TestCliGui extends CliGui {
	public TestCliGui(AgentTaskDispatcher agent) {
		super(agent);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 6; i++) {
			addLocalAgent("LOCAL_CONTAINER");
		}
		for (int i = 0; i < 4; i++) {
			addRemoteAgent("REMOTE_CONTAINER");
		}
		File file = new File("images");
		for (File f : file.listFiles()) {
			TaskTransportDataObject ttdo = new TaskTransportDataObject();
			ttdo.setTaskMetaData(new TaskMetaData("task",
					DummyLocalPreTask.class.getName(), DummyLocalPostTask.class
							.getName(), DummyRemoteTask.class.getName()));
			DataObject dataObject = new DataObject(f.getAbsolutePath(), 3);
			ttdo.setTaskDataObject(dataObject);
			addTask(ttdo);
		}
		startStopWorkflow(this);

	}

	public static void main(String[] args) {
		PlatformManager.initPlatform(TestCliGui.class.getName());
	}

}
