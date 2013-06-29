package com.vahe;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

public class Program {

	private static final Logger LOGGER = Logger.getLogger(Program.class);
	private static  InsForm INS_FORM;
	/**
	 * GUI main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.info("Exception in main 1 ", e);
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				LOGGER.info("Exception in main 2 ", e);
			}
		}

		INS_FORM = new InsForm();
		INS_FORM.setVisible(true);
	}

}
