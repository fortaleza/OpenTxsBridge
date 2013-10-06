package net.otxs.bridge.core;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.otxs.ApplProperties;
import net.otxs.bridge.Text;
import net.otxs.bridge.Util;
import net.otxs.bridge.core.Console.ConsoleApplication;
import net.otxs.bridge.core.commands.Commands;
import net.otxs.bridge.core.commands.Commands.Quit;
import net.otxs.bridge.core.commands.act.AccountCommands;
import net.otxs.bridge.core.commands.act.AssetCommands;
import net.otxs.bridge.core.commands.act.BusinessCommands;
import net.otxs.bridge.core.commands.act.ConfigCommands;
import net.otxs.bridge.core.commands.act.ContactCommands;
import net.otxs.bridge.core.commands.act.HackCommands;
import net.otxs.bridge.core.commands.act.MetaCommands;
import net.otxs.bridge.core.commands.act.NymCommands;
import net.otxs.bridge.core.commands.act.ServerCommands;
import net.otxs.bridge.core.commands.act.WalletCommands;
import net.otxs.bridge.core.modules.Module;
import net.otxs.bridge.core.modules.OTAPI;
import net.sf.image4j.codec.ico.ICODecoder;

import org.opentransactions.otapi.NativeLoader;
import org.opentransactions.otapi.OTCallback;
import org.opentransactions.otapi.OTCaller;
import org.opentransactions.otapi.OTPassword;
import org.opentransactions.otjavalib.Load.IPasswordImage;
import org.opentransactions.otjavalib.Load.LoadingOpenTransactionsFailure;

import com.southpark.HibFactory;

public class OTBridge extends Interpreter {

	private static Logger logger = Logger.getLogger(OTBridge.class
			.getName());

	private static FileOutputStream lock;
	private static HibFactory database;

	public static HibFactory getDatabase() {
		if (database == null)
			database = HibFactory.getByName(ApplProperties.get().getString(
					"main.database.folder"));
		return database;
	}

	public static void closeDatabase() {
		if (database != null)
			database.closeDatabase();
	}

	public static void main(String[] args) {
		new OTBridge().run(null);
		System.exit(0);
	}

	static {
		if (isLocked()) {
			System.err.println("ERROR: another instance is still active");
			System.exit(0);
		}

		Thread cleanupThread = new Thread() {
			@Override
			public void run() {
				OTAPI.appShutdown();
				System.out.println("SUCCESS: shutdown hook done");
			}
		};
		Runtime.getRuntime().addShutdownHook(cleanupThread);
		int loadingResult = load();
		if (loadingResult <= 0) {
			if (loadingResult == 0)
				Runtime.getRuntime().removeShutdownHook(cleanupThread);
			System.err.println("ERROR: failed to load libs");
			System.exit(0);
		}
		System.out.println("SUCCESS: native libs loaded");
	}

	private static boolean isLocked() {
		File lockFile = new File(String.format("%s/%s",
				ApplProperties.getUserDataPath(), 
				ApplProperties.get().getString("lock.file")));
		if (lockFile.exists() && !lockFile.delete())
			return true;
		try {
			lock = new FileOutputStream(lockFile);
		} catch (FileNotFoundException e) {
			return false;
		}
		return false;
	}

	private static int load() {
		try {
			if (!NativeLoader.getInstance().loadNative())
				return 0;
			if (NativeLoader.getInstance().initNative()) {
				/** copy 'password.image' into user data path */
				File file = new File(ApplProperties.getUserDataPath(),
						ApplProperties.get().getString(
								"password.image"));
				if (!file.exists()) {
					FileOutputStream out = new FileOutputStream(file);
					Util.copyStream(
							ClassLoader.getSystemResourceAsStream(
									ApplProperties.get().getString(
											"password.image")), out);
					out.close();
				}
				IPasswordImage passwordImageMgmt = new PasswordImageMgmt();
				if (NativeLoader.getInstance().setupPasswordImage(
						passwordImageMgmt)) {
					OTCaller javaPasswordCaller = new OTCaller();
					OTCallback javaPasswordCallback = new OTCallback() {
						@Override
						public void runOne(String prompt, OTPassword output) {
							String password = getPasswordFromUser(prompt);
							output.setPassword(password, password.length());
						}

						@Override
						public void runTwo(String prompt, OTPassword output) {
							String password = getPasswordFromUser(prompt);
							output.setPassword(password, password.length());
						}
					};
					if (NativeLoader.getInstance().setupPasswordCallback(
							javaPasswordCaller, javaPasswordCallback)) {
						return 1;
					}
				}
			}
			if (NativeLoader.getInstance().getInitialized())
				return -1;
			return 0;

		} catch (LoadingOpenTransactionsFailure e) {
			logger.log(Level.SEVERE, null, e);
			if (NativeLoader.getInstance().getInitialized())
				return -1;
			return 0;
		} catch (IOException e) {
			logger.log(Level.SEVERE, null, e);
			return -1;
		}
	}

	private static class PasswordImageMgmt implements IPasswordImage {
		@Override
		public String getPasswordImageFromUser(String value) {
			File file = new File(ApplProperties.getUserDataPath(),
					ApplProperties.get().getString("password.image"));
			if (file.exists())
				return file.getAbsolutePath();
			file = new File(ApplProperties.get().getApplBasePath(),
					ApplProperties
							.get().getString("password.image"));
			if (file.exists())
				return file.getAbsolutePath();
			return null;
		}

		@Override
		public boolean setPasswordImage(String value) {
			return false;
		}

		@Override
		public Boolean getIfUserCancelled() {
			return false;
		}
	}

	private static String getPasswordFromUser(String prompt) {
		System.out.println(String.format("%s", prompt));
		return ConsoleApplication.readLineFromConsole();
	}

	@Override
	public String getTitle() {
		return Text.CONSOLE_TITLE.toString();
	}

	@Override
	public void run(ConsoleFrame consoleFrame) {
		{
			String walletId = DataModel.getWalletId();
			File file = new File(Module.getWalletFileName(walletId));
			if (!file.exists())
				Module.createWallet(walletId);
			try {
				Module.loadWallet(walletId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		{/** expand 'contractsJar' into 'workingDir' */
			String contracts = String.format("%s/%s/%s",
					ApplProperties.get().getString("workingDir"),
					ApplProperties.get().getString("bridgeDir"),
					ApplProperties.get().getString("contractsDir"));
			JarInputStream is;
			try {
				is = new JarInputStream(
						ClassLoader.getSystemResourceAsStream(
								ApplProperties.get().getString("contractsJar")));
				JarEntry entry = is.getNextJarEntry();
				while (entry != null) {
					File file = new File(contracts, entry.getName());
					if (!file.exists()) {
						if (entry.getName().indexOf('/') == entry.getName()
								.length() - 1) {
							file.mkdirs();
						} else {
							FileOutputStream out = new FileOutputStream(file);
							Util.copyStream(is, out, entry.getSize());
							out.close();
						}
					}
					entry = is.getNextJarEntry();
				}
				is.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
		if (consoleFrame != null) {
			try {
				/** application icon */
				consoleFrame.setIconImage(ICODecoder.read(ClassLoader
						.getSystemResourceAsStream(ApplProperties.get().getString(
								"icon.image"))).get(0));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (ApplProperties.get().getBoolean("password.image.show")) {
			JLabel passwordImage = null;
			try {
				passwordImage = new JLabel(new ImageIcon(
						ImageIO.read(ClassLoader
								.getSystemResource(ApplProperties.get()
										.getString("password.image")))));
				passwordImage
						.setPreferredSize(new Dimension(
								ApplProperties.get().getInteger(
										"password.image.size"),
								ApplProperties.get().getInteger(
										"password.image.size")));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			JDialog passwordImageWindow = new JDialog();
			passwordImageWindow.add(passwordImage);
			passwordImageWindow.setModal(false);
			passwordImageWindow.setAlwaysOnTop(true);
			try {passwordImageWindow.setIconImage(ICODecoder.read(ClassLoader
						.getSystemResourceAsStream(ApplProperties.get().getString(
								"icon.image"))).get(0));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			passwordImageWindow.setBounds(
					ApplProperties.get().getInteger("password.image.insetW"),
					ApplProperties.get().getInteger("password.image.insetH"),
					screenSize.width
							- ApplProperties.get().getInteger(
									"password.image.insetW") * 2,
					screenSize.height
							- ApplProperties.get().getInteger(
									"password.image.insetH") * 2);
			passwordImageWindow.pack();
			passwordImageWindow.setVisible(true);
		}
		System.out.println(Text.WELCOME_HEADER);
		System.out.println();
		reset();
		super.run(consoleFrame);
	}

	public static void reset() {
		Commands.reset();
		MetaCommands.init();
		ConfigCommands.init();
		WalletCommands.init();
		ServerCommands.init();
		AssetCommands.init();
		NymCommands.init();
		AccountCommands.init();
		ContactCommands.init();
		BusinessCommands.init();
		HackCommands.init();
		Module.init();
	}

	@Override
	public void close() {
		try {
			if (lock != null)
				lock.close();
		} catch (IOException e) {
		} finally {
			Quit.execute();
		}
	}
}
