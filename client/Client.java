package clientsMessenger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import messengeri.Messengeri;
import networking.NetworkClient;

public class Client extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
	public static final String TITLE = "Mark's Messenger";
	private static Client instance;



	private static JList<String> clientL;
	public JTextArea textArea;
	@SuppressWarnings("unused")
	private JTextArea statusArea;
	private static JTextField textInput;
	private static JButton sendButton;
	private static NetworkClient client;
    String ip;
	String port;
	//constructor
	public Client() {
	    ip = JOptionPane.showInputDialog(null,"IP address: ", "Enter Server's IP address", JOptionPane.INFORMATION_MESSAGE);
	    port = JOptionPane.showInputDialog(null,"Port number: ", "Enter Server's port number", JOptionPane.INFORMATION_MESSAGE);
		int portN = Integer.parseInt(port);

		setIconImage(Toolkit.getDefaultToolkit().getImage(Messengeri.class.getResource("/com/sun/javafx/scene/control/skin/caspian/fxvk-enter-button.png")));
		makeUi();
		client = new NetworkClient(ip, portN);
		client.connectToServer();
		
		setTitle(TITLE);
		setSize(900,500);
		setMinimumSize(new Dimension(400,200));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(true);
		
	}
	


			
	public static Client getInstance() {
		return instance;
	}
		
		// Make GUI
		private void makeUi(){
		JFrame.setDefaultLookAndFeelDecorated(true);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel);
		panel.setBorder(new EmptyBorder(2, 2, 0, 1));
		panel.setLayout(new BorderLayout());
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Message area"));
		panel.add(scrollPane, BorderLayout.CENTER);

		

		JTextArea statusArea = new JTextArea();
		statusArea.setPreferredSize(new Dimension(300, 450));
		panel.add(statusArea, BorderLayout.EAST);

		// Panel East
		JPanel panelEast = new JPanel(new BorderLayout());
		panel.add(panelEast, BorderLayout.EAST);
		clientL = new JList<String>();
		JScrollPane clientSP = new JScrollPane(clientL);
		clientSP.setPreferredSize(new Dimension(150, 0));
		clientSP.setBorder(BorderFactory.createTitledBorder("Online users"));
		panelEast.add((clientSP), BorderLayout.CENTER);

		statusArea.setEditable(false);
		statusArea.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		
		// Panel South
		JPanel panelSouth = new JPanel(new BorderLayout());
		panel.add(panelSouth, BorderLayout.SOUTH);
		textInput = new JTextField();	
		textInput.setEditable(false);
		textInput.setBorder(BorderFactory.createTitledBorder("Type EXIT to disconnect and exit program"));
		panelSouth.add(textInput);

		textInput.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						String message = (textInput.getText());
						textInput.setText("");
						try {
							client.sendMessage(message);
						} catch (IOException e) {
							e.printStackTrace();
						}						
					}
				}
		);
		
		sendButton = new JButton("Send");
		panelSouth.add(sendButton, BorderLayout.EAST);
		sendButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						String message = (textInput.getText());
						textInput.setText("");
						try {
							client.sendMessage(message);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		);
}

		public static void updateView() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					DefaultListModel<String> model = new DefaultListModel<String>();
					Iterator<String> list = client.getConnectedClientMap().iterator();
					while (list.hasNext()) {
						model.addElement(list.next());
					}
					clientL.setModel(model);
					
				}
			}).start();
		}
		
		
		public void showMessage(String message) {
			textArea.append(DATE_FORMAT.format(new Date()) + " " + message + "\n");
		}
		

		public void ableToType(boolean tof) {
			textInput.setEditable(tof);
		}
		

		public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					instance = new Client();
					instance.setVisible(true);
				}
			});
		} // main
} // class
