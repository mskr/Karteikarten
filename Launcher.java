import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class Launcher {
	public static void main(String[] args) {
		startGUI();
		/*
		try {
			// Datenbank erstellen
			Process p2 = new ProcessBuilder(""+System.getProperty("user.dir")+"/mysql-5.6/bin/mysql.exe",
				"-uroot",
				"-e",
				"\"CREATE DATABASE sopra\"").start();
			// Datenbank fuellen
			ProcessBuilder b = new ProcessBuilder(""+System.getProperty("user.dir")+"/mysql-5.6/bin/mysql.exe",
				"-uroot",
				"-v",
				"sopra",
				"<..\\..\\prog\\install\\karteikarten_db_full.sql"); // klappt wenn man es direkt in der Kommandozeile ausuehrt
			Process p3 = b.start();
			StreamGobbler errorGobbler = new StreamGobbler(p3.getErrorStream());
			StreamGobbler outputGobbler = new StreamGobbler(p3.getInputStream());
			errorGobbler.start();
			outputGobbler.start();
			p3.waitFor();
		} catch (Exception err) {
			err.printStackTrace();
		}
		*/
	}

	public static boolean isMysqlRunning = false;
	public static boolean isNeo4jRunning = false;
	public static boolean isTomcatRunning = false;

	public static void startMySQL() {
		try {
			ProcessBuilder mysqlPB = new ProcessBuilder(""+System.getProperty("user.dir")+"/mysql-5.6/bin/mysqld.exe");
			/*
			mysqlPB.redirectInput(new TextAreaOutputStream());
			mysqlPB.redirectOutput(new TextAreaOutputStream());
			mysqlPB.redirectError(new TextAreaOutputStream());
			*/
			Process mysql = mysqlPB.start();
			StreamGobbler mysqlErrorGobbler = new StreamGobbler("MySQL", mysql.getErrorStream());
			StreamGobbler mysqlOutputGobbler = new StreamGobbler("MySQL", mysql.getInputStream());
			mysqlErrorGobbler.start();
			mysqlOutputGobbler.start();

			isMysqlRunning = true;
			if(isNeo4jRunning && isTomcatRunning) enableOpenBt();
		} catch(Exception err) {
			err.printStackTrace();
		}
	}

	public static void stopMySQL() {
		try {
			Process p = new ProcessBuilder(""+System.getProperty("user.dir")+"/mysql-5.6/bin/mysqladmin.exe",
				"-u",
				"root",
				"shutdown").start();
			p.waitFor();

			disableOpenBt();
		} catch(Exception err) {
			err.printStackTrace();
		}
	}

	public static Process neo4j;

	public static void startNeo4j() {
		try {
			//TODO leider oeffnet sich immer ein zusaetzliches Neo4j Konsolenfenster
			ProcessBuilder neo4jPB = new ProcessBuilder(""+System.getProperty("user.dir")+"/neo4j-community-2.3.0-M01/bin/Neo4j.bat");
			/*
			neo4j.redirectInput(new TextAreaOutputStream());
			neo4j.redirectOutput(new TextAreaOutputStream());
			neo4j.redirectError(new TextAreaOutputStream());
			*/
			neo4j = neo4jPB.start();
			StreamGobbler neo4jErrorGobbler = new StreamGobbler("Neo4j", neo4j.getErrorStream());
			StreamGobbler neo4jOutputGobbler = new StreamGobbler("Neo4j", neo4j.getInputStream());
			neo4jErrorGobbler.start();
			neo4jOutputGobbler.start();

			isNeo4jRunning = true;
			if(isMysqlRunning && isTomcatRunning) enableOpenBt();
		} catch(Exception err) {
			err.printStackTrace();
		}
	}

	public static void stopNeo4j() {
		//TODO dies stoppt leider nicht den Neo4j Service
		neo4j.destroy();

		disableOpenBt();
	}

	public static void startTomcat() {
		try {
			ProcessBuilder tomcatPB = new ProcessBuilder(""+System.getProperty("user.dir")+"/apache-tomcat-7.0.63/bin/catalina.bat",
				"run");
			tomcatPB = tomcatPB.directory(new File(""+System.getProperty("user.dir")+"/apache-tomcat-7.0.63/bin"));
			/*
			tomcatPB.redirectInput(new TextAreaOutputStream());
			tomcatPB.redirectOutput(new TextAreaOutputStream());
			tomcatPB.redirectError(new TextAreaOutputStream());
			*/
			Process tomcat = tomcatPB.start();
			StreamGobbler tomcatErrorGobbler = new StreamGobbler("Tomcat", tomcat.getErrorStream());
			StreamGobbler tomcatOutputGobbler = new StreamGobbler("Tomcat", tomcat.getInputStream());
			tomcatErrorGobbler.start();
			tomcatOutputGobbler.start();

			isTomcatRunning = true;
			if(isMysqlRunning && isNeo4jRunning) enableOpenBt();
		} catch(Exception err) {
			err.printStackTrace();
		}
	}

	public static void stopTomcat() {
		try {
			ProcessBuilder pb = new ProcessBuilder(""+System.getProperty("user.dir")+"/apache-tomcat-7.0.63/bin/shutdown.bat");
			pb = pb.directory(new File(""+System.getProperty("user.dir")+"/apache-tomcat-7.0.63/bin"));
			Process p = pb.start();
			p.waitFor();

			disableOpenBt();
		} catch(Exception err) {
			err.printStackTrace();
		}
	}

	public static JTextArea logArea = new JTextArea();
	public static JButton openBrowserBt;

	public static void startGUI() {
		JFrame frame = new JFrame("Launcher");
		frame.setLayout(new GridLayout(5,1));
		JPanel mysqlPanel = new JPanel(new FlowLayout());
		JLabel mysqlLabel = new JLabel("MySQL");
		mysqlLabel.setPreferredSize(new Dimension(100,30));
		mysqlPanel.add(mysqlLabel);
		JButton mysqlStart = new JButton("Start");
		JButton mysqlStop = new JButton("Stop");
		mysqlStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startMySQL();
			}
		});
		mysqlStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopMySQL();
			}
		});
		mysqlPanel.add(mysqlStop);
		mysqlPanel.add(mysqlStart);
		frame.add(mysqlPanel);
		JPanel neo4jPanel = new JPanel(new FlowLayout());
		JLabel neo4jLabel = new JLabel("Neo4j");
		neo4jLabel.setPreferredSize(new Dimension(100,30));
		neo4jPanel.add(neo4jLabel);
		JButton neo4jStart = new JButton("Start");
		JButton neo4jStop = new JButton("Stop");
		neo4jStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startNeo4j();
			}
		});
		neo4jStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopNeo4j();
			}
		});
		neo4jPanel.add(neo4jStop);
		neo4jPanel.add(neo4jStart);
		frame.add(neo4jPanel);
		JPanel tomcatPanel = new JPanel(new FlowLayout());
		JLabel tomcatLabel = new JLabel("Tomcat");
		tomcatLabel.setPreferredSize(new Dimension(100,30));
		tomcatPanel.add(tomcatLabel);
		JButton tomcatStart = new JButton("Start");
		JButton tomcatStop = new JButton("Stop");
		tomcatStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startTomcat();
			}
		});
		tomcatStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopTomcat();
			}
		});
		tomcatPanel.add(tomcatStop);
		tomcatPanel.add(tomcatStart);
		frame.add(tomcatPanel);

		openBrowserBt = new JButton("Open Web App in Browser");
		openBrowserBt.setEnabled(false);
		openBrowserBt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://localhost:8080/SopraProject/sopra.html"));	
				} catch(Exception err) {
					err.printStackTrace();
				}
			}
		});
		frame.add(openBrowserBt);
		frame.add(logArea);

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void enableOpenBt() {
		openBrowserBt.setEnabled(true);
	}

	public static void disableOpenBt() {
		openBrowserBt.setEnabled(false);
	}

	static class StreamGobbler extends Thread {
	    InputStream is;
	    String name;

	    // reads everything from is until empty. 
	    StreamGobbler(String name, InputStream is) {
	    	this.name = name;
	        this.is = is;
	    }

	    public void run() {
	        try {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	                System.out.println("["+name+"] "+line);    
	        } catch (IOException ioe) {
	            ioe.printStackTrace();  
	        }
	    }
	}

	//TODO Versuch alle Konsolenausgaben auf die Textarea umzuleiten
	public class TextAreaOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			// redirects data to the text area
			logArea.append(String.valueOf((char)b));
			// scrolls the text area to the end of data
			logArea.setCaretPosition(logArea.getDocument().getLength());
		}
	}
}