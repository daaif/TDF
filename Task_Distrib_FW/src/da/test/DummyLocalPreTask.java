package da.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import da.mas.task.AbstractLocalPreTask;
import da.mas.task.TaskTransportDataObject;

public class DummyLocalPreTask extends AbstractLocalPreTask {

	@Override
	public void doLocalPreTask() {
		DataObject dataObject = (DataObject) taskDataObject.getTaskDataObject();
		String fileName = dataObject.getFileName();
		try {
			dataObject.setData(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
