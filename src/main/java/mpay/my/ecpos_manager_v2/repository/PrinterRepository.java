package mpay.my.ecpos_manager_v2.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PrinterRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public PrinterRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final String MPOP_PRINTER_PATH = "C:\\Users\\nicholas.foo\\source\\repos\\ECPOS_Printer_MPOP\\ECPOS_Printer_MPOP\\bin\\Release\\ECPOS_Printer_MPOP.exe ";
	private static final String PORT_INFO_LIST = "PortInfoList";
	private static final String PAPER_SIZE_LIST = "PaperSizeList";

	private static final String SELECTED_PORT_MODEL_NAME = "selectedPortModelName";
	private static final String SELECTED_PAPER_SIZE = "selectedPaperSize";
	private static final String SELECTED_PORT_NAME = "selectedPortName";
}
