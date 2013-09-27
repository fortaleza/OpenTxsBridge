package eu.opentxs.bridge.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eu.ApplProperties;
import eu.opentxs.bridge.core.Console.ConsoleApplication;

public class ConsoleFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// new ConsoleFrame(Interpreter.class);
		new ConsoleFrame(OpenTransactions.class);
	}

	private Console console;

	public ConsoleFrame(Class<? extends ConsoleApplication> application) {
		super();
		console = new Console();
		console.setForeground(new Color(ApplProperties.get().getInteger("console.color.in")));
		console.setBackground(new Color(ApplProperties.get().getInteger("console.background")));
		console.setFont(new Font(ApplProperties.get().getString("console.font.name"), (ApplProperties.get().getBoolean("console.font.bold") ? Font.BOLD : Font.PLAIN), ApplProperties.get().getInteger(
				"console.font.size")));
		console.setOutColor(new Color(ApplProperties.get().getInteger("console.color.out")));
		console.setErrColor(new Color(ApplProperties.get().getInteger("console.color.err")));

		JScrollPane scrollPane = new JScrollPane(console);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPane);
		add(scrollPane);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(ApplProperties.get().getInteger("console.inset.horizontal"), ApplProperties.get().getInteger("console.inset.vertical"),
				screenSize.width - ApplProperties.get().getInteger("console.inset.horizontal") * 2, screenSize.height - ApplProperties.get().getInteger("console.inset.vertical") * 2);
		setSize(ApplProperties.get().getInteger("console.width"), ApplProperties.get().getInteger("console.height"));

		setVisible(true);

		try {
			ConsoleApplication instance = application.newInstance();
			console.connectStdStreams();
			setTitle(instance.getTitle());
			instance.run(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Console getConsole() {
		return console;
	}
}
