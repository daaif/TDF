package da.test;

import da.mas.task.AbstractTaskDataObject;

public class DataObject extends AbstractTaskDataObject{
	
	private String fileName;
	private String path;
	private byte[] data;
	private int[] dataResult;
	private int width;
	private int height;
	
	private int nombreClasses;

	public DataObject() {
		// TODO Auto-generated constructor stub
	}
	
	public DataObject(String fileName, int nombreClasses) {
		this.fileName = fileName;
		this.nombreClasses = nombreClasses;
	}
	
	public int[] getDataResult() {
		return dataResult;
	}

	public void setDataResult(int[] dataResult) {
		this.dataResult = dataResult;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] bs) {
		this.data = bs;
	}

	public int getNombreClasses() {
		return nombreClasses;
	}

	public void setNombreClasses(int nombreClasses) {
		this.nombreClasses = nombreClasses;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	
	
}
