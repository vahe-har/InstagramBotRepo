package com.vahe;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.vahe.utils.Sites;

public class InsForm extends JFrame {

	private static final String MAX_LIKE_PER_HOUR_DEFAULT = "330";
	private static final String LIKE_COUNT_DEFAULT = "10";
	private static final String DELAY_DEFAULT = "5";
	
	private static final Logger LOGGER = Logger.getLogger(InsForm.class);
	
	private JPanel contentPane;
	private JTextField username;
	private JTextField password;
	private JTextField tagName;
	private JTextField delay;
	private JTextField likePerPhoto;
	private JTextField maxLikes;
	private JLabel lblRequired;
	private JLabel label;
	private JLabel label_1;
	private JLabel lblNewLabel;
	private JLabel lblDefaultIs;
	private JLabel lblDefaultIs_1;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton startButton;
	private final Executor singleThreadExecutor = Executors.newSingleThreadExecutor();
	private Program program;
	private JCheckBox chckbxWebstagram;
	private JCheckBox chckbxStatigram;
	private JLabel lblLikeVia;

	public InsForm(Program program) {
		this();
		this.program = program;
	}

	/**
	 * Create the frame.
	 */
	public InsForm() {
		this.program = new Program();
		
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 554, 713);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
	
		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startButtonActionPerformed(e);
			}
		});
		startButton.setBounds(178, 412, 89, 23);
		contentPane.add(startButton);
	
		username = new JTextField();
		username.setBounds(178, 25, 130, 23);
		contentPane.add(username);
		username.setColumns(10);
	
		password = new JTextField();
		password.setBounds(178, 73, 130, 23);
		contentPane.add(password);
		password.setColumns(10);
	
		tagName = new JTextField();
		tagName.setBounds(178, 121, 130, 23);
		contentPane.add(tagName);
		tagName.setColumns(10);
	
		delay = new JTextField();
		delay.setBounds(178, 169, 130, 23);
		contentPane.add(delay);
		delay.setColumns(10);
	
		likePerPhoto = new JTextField();
		likePerPhoto.setBounds(178, 217, 130, 23);
		contentPane.add(likePerPhoto);
		likePerPhoto.setColumns(10);
	
		JLabel usernamet = new JLabel("Username");
		usernamet.setFont(new Font("Tahoma", Font.BOLD, 11));
		usernamet.setBounds(103, 29, 65, 14);
		contentPane.add(usernamet);
	
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPassword.setBounds(114, 77, 54, 14);
		contentPane.add(lblPassword);
	
		JLabel lblTag = new JLabel("Tag Name");
		lblTag.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTag.setBounds(103, 125, 65, 14);
		contentPane.add(lblTag);
	
		JLabel lblDelayBetweenLikes = new JLabel("Delay between likes (in sec)");
		lblDelayBetweenLikes.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDelayBetweenLikes.setBounds(10, 173, 158, 14);
		contentPane.add(lblDelayBetweenLikes);
	
		JLabel lblLikeCountPer = new JLabel("Like count per photo");
		lblLikeCountPer.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLikeCountPer.setBounds(48, 217, 120, 14);
		contentPane.add(lblLikeCountPer);
	
		maxLikes = new JTextField();
		maxLikes.setColumns(10);
		maxLikes.setBounds(178, 265, 130, 23);
		contentPane.add(maxLikes);
	
		JLabel lblMaxLikesPer = new JLabel("Max likes per hour");
		lblMaxLikesPer.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMaxLikesPer.setBounds(64, 269, 104, 14);
		contentPane.add(lblMaxLikesPer);
	
		lblRequired = new JLabel("required");
		lblRequired.setForeground(Color.RED);
		lblRequired.setBackground(Color.RED);
		// lblRequired.setForeground(UIManager.getColor("ToolBar.dockingForeground"));
		lblRequired.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblRequired.setBounds(318, 29, 65, 14);
		contentPane.add(lblRequired);
	
		label = new JLabel("required");
		label.setForeground(Color.RED);
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		label.setBounds(318, 77, 65, 14);
		contentPane.add(label);
	
		label_1 = new JLabel("required");
		label_1.setForeground(Color.RED);
		label_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_1.setBounds(318, 125, 65, 14);
		contentPane.add(label_1);
	
		lblNewLabel = new JLabel("default is 5");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(318, 173, 65, 14);
		contentPane.add(lblNewLabel);
	
		lblDefaultIs = new JLabel("default is 10");
		lblDefaultIs.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDefaultIs.setBounds(318, 221, 77, 14);
		contentPane.add(lblDefaultIs);
	
		lblDefaultIs_1 = new JLabel("default is 330");
		lblDefaultIs_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDefaultIs_1.setBounds(318, 269, 89, 14);
		contentPane.add(lblDefaultIs_1);
	
		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 459, 548, 228);
		contentPane.add(scrollPane);
	
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setRows(50);
		
		chckbxWebstagram = new JCheckBox(Sites.WEBSTAGRAM.getName());
		chckbxWebstagram.setSelected(true);
		chckbxWebstagram.setBounds(178, 308, 112, 24);
		contentPane.add(chckbxWebstagram);
		
		chckbxStatigram = new JCheckBox(Sites.STATIGRAM.getName());
		chckbxStatigram.setSelected(true);
		chckbxStatigram.setBounds(178, 342, 112, 24);
		contentPane.add(chckbxStatigram);
		
		JLabel lblRequiredAtLeast = new JLabel("requires at least one to be selected");
		lblRequiredAtLeast.setForeground(Color.RED);
		lblRequiredAtLeast.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblRequiredAtLeast.setBounds(304, 333, 213, 14);
		contentPane.add(lblRequiredAtLeast);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		layeredPane.setBounds(166, 300, 353, 72);
		contentPane.add(layeredPane);
		
		lblLikeVia = new JLabel("Like Via  ");
		lblLikeVia.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLikeVia.setBounds(103, 333, 54, 14);
		contentPane.add(lblLikeVia);
		
	}

	/**
	 * Launch the application.
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

			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				// not worth my time
			}
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InsForm frame = new InsForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void startButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_startButtonActionPerformed
		logMessage(username.getText() + "  " + password.getText() + "  " + tagName.getText() + "   " + delay.getText() + "  " + likePerPhoto.getText());

		if (StringUtils.isEmpty(username.getText()) || StringUtils.isEmpty(password.getText()) || StringUtils.isEmpty(tagName.getText())) {
			return;
		}
		if(!chckbxStatigram.isSelected() && !chckbxWebstagram.isSelected()){
			return;
		}
		startButton.setVisible(false);
		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				runGUIProgramm();
			}
		});
		//
	}

	public void logMessage(String message) {
		final String text = this.textArea.getText();
		if (text.length() > 5000) {
			this.textArea.setText("");
		}
		this.textArea.setText(text + message + "\n");

	}

	void runGUIProgramm() {
		// parameter are validated

		String username = this.username.getText();
		String password = this.password.getText();
		String likePerPhoto = this.likePerPhoto.getText();
		String tagname = this.tagName.getText();
		String delay = this.delay.getText();
		String maxLikes = this.maxLikes.getText();
		List<Sites> siteList = new ArrayList<>();
		
		if(chckbxStatigram.isSelected()){
			siteList.add(Sites.STATIGRAM);
		}
		if(chckbxWebstagram.isSelected()){
			siteList.add(Sites.WEBSTAGRAM);
		}
		

		if (StringUtils.isEmpty(delay)) {
			delay = DELAY_DEFAULT;
		}
		if (StringUtils.isEmpty(likePerPhoto)) {
			likePerPhoto = LIKE_COUNT_DEFAULT;
		}
		if (StringUtils.isEmpty(maxLikes)) {
			maxLikes = MAX_LIKE_PER_HOUR_DEFAULT;
		}
		
		LOGGER.info("username : " + username);
		LOGGER.info("password : " + password);
		LOGGER.info("likePerPhoto : " + likePerPhoto);
		LOGGER.info("tagName : " + tagname);
		LOGGER.info("delay : " + delay);
		LOGGER.info("maxLikes : " + maxLikes);
		LOGGER.info("siets list is " + siteList);
		LikeParmetes likeParmetes = new LikeParmetes(Integer.valueOf(likePerPhoto), Integer.valueOf(delay), Integer.valueOf(maxLikes), tagname,
				username, password,siteList);
		InstagramClient instagramClient = new InstagramClient(likeParmetes);
		instagramClient.likeImagesInTag(tagname);
	}
}
