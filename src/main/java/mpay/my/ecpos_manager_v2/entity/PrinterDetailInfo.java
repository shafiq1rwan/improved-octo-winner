package mpay.my.ecpos_manager_v2.entity;

public class PrinterDetailInfo {

	private String printer_model;
	private String port_name;
	private int paper_size;
	
	public PrinterDetailInfo() {}

	public PrinterDetailInfo(String printer_model, String port_name, int paper_size) {
		this.printer_model = printer_model;
		this.port_name = port_name;
		this.paper_size = paper_size;
	}

	public String getPrinterModel() {
		return printer_model;
	}

	public void setPrinterModel(String printerModel) {
		this.printer_model = printerModel;
	}

	public int getPaperSize() {
		return paper_size;
	}

	public void setPaperSize(int paperSize) {
		this.paper_size = paperSize;
	}

	public String getPortName() {
		return port_name;
	}

	public void setPortName(String portName) {
		this.port_name = portName;
	}
	
	
}
