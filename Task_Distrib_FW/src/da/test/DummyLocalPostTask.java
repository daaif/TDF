package da.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import da.mas.task.AbstractLocalPostTask;

public class DummyLocalPostTask extends AbstractLocalPostTask{
	private DataObject dataObject;
	
	
	@Override
	public void doLocalPostTask() {
		dataObject = (DataObject) taskDataObject.getTaskDataObject();
		logger.fine("Yes it works... Post Task");
		saveImages();
	}

	public void saveImages() {

		//ClassificationTask taskResult = (ClassificationTask) result;

		int nombreClasses = dataObject.getNombreClasses();

		int[] data = dataObject.getDataResult();

		for (int i = 0; i < nombreClasses; i++) {
			int[] affichage = new int[data.length];
			for (int j = 0; j < data.length; j++) {
				if (data[j] == i)
					affichage[j] = 0x00000000;
				else
					affichage[j] = 0xFFFFFFFF;
			}

			BufferedImage bigs = new BufferedImage(dataObject.getWidth(),
					dataObject.getHeight(), BufferedImage.TYPE_INT_RGB);

			bigs.setRGB(0, 0, dataObject.getWidth(),
					dataObject.getHeight(), affichage, 0,
					dataObject.getWidth());
			try {
				String[] parts = dataObject.getFileName().split("\\\\");
				String[] parts1 = parts[parts.length-1].split("\\.");
				StringBuilder sb = new StringBuilder();
				for(int j=0; j<parts1.length-1; j++)
					sb.append(parts1[j]);
				sb.append("_" + i + "_out.jpg");
				ImageIO.write(bigs, "jpg", 
						new File("images/out/" + sb.toString() ));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
