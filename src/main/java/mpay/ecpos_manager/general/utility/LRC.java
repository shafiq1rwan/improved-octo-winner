package mpay.ecpos_manager.general.utility;

public class LRC {
	public static String generateLRC(String data) {
		long lrc = data.toCharArray()[0];

		for (int i = 1; i < data.toCharArray().length; i++) {
			lrc = lrc ^ data.toCharArray()[i];
		}

		String ReturnHexCode = Long.toString(lrc, 16).toUpperCase();

		if (ReturnHexCode.length() == 1) {
			ReturnHexCode = "0" + ReturnHexCode;
		}
		return ReturnHexCode;
	}
}
