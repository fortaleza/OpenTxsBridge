package net.otxs.bridge.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.otxs.ApplProperties;
import net.otxs.bridge.core.Console.ConsoleApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(ConsoleFrame.class);

	public static void main(String[] args) {
		// ConsoleFrame.run(Interpreter.class);
		ConsoleFrame.run(OTBridge.class);
	}

	private static Console console;
	
	public static Console getConsole() {
		return console;
	}
	
	private static void run(Class<? extends ConsoleApplication> application) {
		console = new Console();
		console.setBackground(new Color(ApplProperties.get().getInteger(
				"console.background")));
		console.setFont(new Font(ApplProperties.get().getString(
				"console.font.name"), (ApplProperties.get().getBoolean(
				"console.font.bold") ? Font.BOLD : Font.PLAIN), ApplProperties
				.get().getInteger("console.font.size")));
		console.setInColor(new Color(ApplProperties.get().getInteger(
				"console.color.in")));
		console.setOutColor(new Color(ApplProperties.get().getInteger(
				"console.color.out")));
		console.setErrColor(new Color(ApplProperties.get().getInteger(
				"console.color.err")));
		new ConsoleFrame().init(application);
	}

	private void init(Class<? extends ConsoleApplication> application) {
	
		JScrollPane scrollPane = new JScrollPane(console);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(
				ApplProperties.get().getInteger("console.inset.horizontal"),
				ApplProperties.get().getInteger("console.inset.vertical"),
				screenSize.width
						- ApplProperties.get().getInteger(
								"console.inset.horizontal") * 2,
				screenSize.height
						- ApplProperties.get().getInteger(
								"console.inset.vertical") * 2);
		setSize(ApplProperties.get().getInteger("console.width"),
				ApplProperties.get().getInteger("console.height"));

		setVisible(true);

		try {
			logger.info(String.format("Loading console application %s..",
					application.getSimpleName()));
			ConsoleApplication instance = application.newInstance();
			logger.info("Console application loaded");
			logger.info("Connecting streams..");
			console.connectStdStreams();
			console.setConsoleApplication(instance);
			setTitle(instance.getTitle());
			instance.run(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
