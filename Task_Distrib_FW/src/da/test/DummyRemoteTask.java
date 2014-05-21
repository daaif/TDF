package da.test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import da.mas.task.AbstractRemoteTask;

public class DummyRemoteTask extends AbstractRemoteTask{
	private int[] imageData;
	private DataObject dataObject;
	private int nombreClasses;
	
	
	@Override
	public void doRemoteTask() {
		dataObject = (DataObject) taskDataObject.getTaskDataObject();
		byte[] data = dataObject.getData();
		InputStream is = new ByteArrayInputStream(data);
		BufferedImage bi;
		try {
			bi = ImageIO.read(is);
			dataObject.setWidth(bi.getWidth());
			dataObject.setHeight(bi.getHeight());
			imageData = getGrayScaleImageData(bi);
			nombreClasses = dataObject.getNombreClasses();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.fine("Yes it works... Remote Task");
		dataObject.setDataResult(doCMeanClassification());
	}
	
	public int[] doCMeanClassification(){ //int nombreDeClasses, int[] imageData) {

		int[] centresClasses = new int[nombreClasses];

		int[] imageDataSource = imageData;

		int[] resultatClassification = new int[imageData.length];

		centresClasses = getCentresClasses(nombreClasses);

		int coutPrecedent = 0;

		int coutCourant = 0;

		int nombreIterations = 0;

		double epsillon = 1;

		while (true) {

			int[] distances = new int[nombreClasses];

			double[] sigmaDistances = new double[nombreClasses];

			double[] sigmaNiveaux = new double[nombreClasses];

			int[] cardinalClasses = new int[nombreClasses];

			nombreIterations++;

			for (int i = 0; i < imageDataSource.length; i++) {

				int indexMin = 0;

				int distanceMin = Math.abs(imageDataSource[i]
						- centresClasses[0]);

				distances[0] = distanceMin;

				for (int j = 1; j < nombreClasses; j++) {
					distances[j] = Math.abs(imageDataSource[i]
							- centresClasses[j]);
					if (distances[j] < distanceMin) {
						distanceMin = distances[j];
						indexMin = j;
					}
				}

				cardinalClasses[indexMin]++;

				sigmaNiveaux[indexMin] += imageDataSource[i];

				sigmaDistances[indexMin] += distances[indexMin];

				resultatClassification[i] = indexMin;

			}

			coutPrecedent = coutCourant;
			coutCourant = 0;
			for (int i = 0; i < nombreClasses; i++)
				coutCourant += sigmaDistances[i];

			int erreur = Math.abs(coutCourant - coutPrecedent);

			if ((erreur) < epsillon)
				break;

			nombreIterations++;

			for (int i = 0; i < nombreClasses; i++) {
				centresClasses[i] = (int) (sigmaNiveaux[i] / cardinalClasses[i]);
			}

			print(centresClasses);
		}
		return resultatClassification;
	}

	private void print(int[] centresClasses) {
		System.out.print("[ ");
		for (int i = 0; i < centresClasses.length; i++)
			System.out.print(centresClasses[i] + " ");
		System.out.println("]");

	}

	private int[] getCentresClasses(int nombreDeClasses) {
		int result[] = new int[nombreDeClasses];
		int part = 255 / (nombreDeClasses - 1);
		for (int i = 0; i < nombreDeClasses; i++)
			result[i] = i * part;
		return result;
	}

	private  int[] getGrayScaleImageData(BufferedImage bi) {
		byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer())
				.getData();
		boolean hasAlphaChanel = bi.getAlphaRaster() != null;
		int chanelsCount = 3;
		if (hasAlphaChanel)
			chanelsCount = 4;
		int index = 0;
		int result[] = new int[bi.getWidth() * bi.getHeight()];
		for (int pixel = 0; pixel < pixels.length; pixel += chanelsCount) {
			int i = 0;
			if (hasAlphaChanel)
				i++;
			int blue = pixels[pixel + i++] & 0xFF;
			int green = pixels[pixel + i++] & 0xFF;
			int red = pixels[pixel + i] & 0xFF;
			int temp = (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
			// int temp = (int) (0.299*red + 0.587*green + 0.114*blue);
			result[index++] = temp & 0xFF; // (temp << 16) + (temp << 8) + temp
											// ;
		}
		return result;
	}
}
