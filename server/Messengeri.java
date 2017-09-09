package messengeri;

import java.awt.*;
// import java.io.BufferedWriter;
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import networking.NetworkServer;

public class Messengeri extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
	public static final String TITLE = "Mark's Messenger - SERVER";
	private static Messengeri instance;
	
	private NetworkServer server;
	
	private JList<String> clients;
	private JTextArea textArea;
	String port;
	
	
	//constructor
	public Messengeri() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Messengeri.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
		makeUi();
	    port = JOptionPane.showInputDialog(null,"Port number: ", "Enter Server's port number", JOptionPane.INFORMATION_MESSAGE);
	    int portN = Integer.parseInt(port);
	    
		server = new NetworkServer(this, portN);

		showMessage("Not connected!");
		
		setTitle(TITLE);
		setSize(900,500);
		setMinimumSize(new Dimension(400,200));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(true);
		
	}
			
	public static Messengeri getInstance() {
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
		scrollPane.setBorder(BorderFactory.createTitledBorder("Message window"));
		panel.add(scrollPane, BorderLayout.CENTER);

		JTextArea statusArea = new JTextArea();
		statusArea.setPreferredSize(new Dimension(300, 450));
		panel.add(statusArea, BorderLayout.EAST);

		// Panel East
		JPanel panelEast = new JPanel(new BorderLayout());
		panel.add(panelEast, BorderLayout.EAST);
		clients = new JList<String>();
		JScrollPane clientSP = new JScrollPane(clients);
		clientSP.setPreferredSize(new Dimension(150, 0));
		clientSP.setBorder(BorderFactory.createTitledBorder("Client's list"));
		panelEast.add((clientSP), BorderLayout.CENTER);

		statusArea.setEditable(false);
		statusArea.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		
}

	public void updateView() {
		new Thread(new Runnable() {
			public void run() {
				DefaultListModel<String> model = new DefaultListModel<String>();
				for(String nickname : server.getConnectedClientMap().keySet()) {
					model.addElement(nickname);
				}
				clients.setModel(model);				
			}
		}).start();
	}		
		
		
		
		
		public void showMessage(String message) {
			textArea.append(DATE_FORMAT.format(new Date()) + " " + message + "\n");
//				txtLog(DATE_FORMAT.format(new Date()) + " " + message + "\r\n");
		}



		public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					instance = new Messengeri();
					instance.server.startServer();
					instance.setVisible(true);
				}
			});
		} // main

		
/*		
	// Logs in text file
	public static void txtLog(String text) throws IOException {

		BufferedWriter writer = null;
        try {
            File logFile = new File("serverlog.txt");
            
            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }

	}
*/		
		
} // class
