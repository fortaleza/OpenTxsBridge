package eu.opentxs.bridge.core.modules;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import eu.ApplProperties;
import eu.opentxs.bridge.Text;
import eu.opentxs.bridge.UTC;
import eu.opentxs.bridge.Util;
import eu.opentxs.bridge.core.DataModel;
import eu.opentxs.bridge.core.Settings;
import eu.opentxs.bridge.core.commands.Commands.Extension;
import eu.opentxs.bridge.core.commands.Commands.Sophistication;
import eu.opentxs.bridge.core.dto.Account;
import eu.opentxs.bridge.core.dto.Transaction.InstrumentType;
import eu.opentxs.bridge.core.modules.act.AssetModule;

public abstract class Module {

	protected interface RequestGenerator {
		public int getRequest();
	}
	
	protected static boolean verbose;
	protected static boolean verboseServer;
	protected static boolean verboseClientLog;
	protected static boolean verboseClientSkip;
	protected static boolean verboseClientWarn;
	protected static boolean verboseClientSuccess;
	
	protected enum AccountType {
    	SIMPLE("simple"), ISSUER("issuer");
    	private String value;
    	private AccountType(String value) {
    		this.value = value;
    	}
    	public String getValue() {
    		return value;
    	}
    	public static AccountType parse(String value) {
    		if (value.equalsIgnoreCase(SIMPLE.getValue()))
    			return SIMPLE;
    		if (value.equalsIgnoreCase(ISSUER.getValue()))
    			return ISSUER;
    		return null;
    	}
    }

	public static void init() {
		
		String walletId = DataModel.getWalletId();
		File file = new File(getWalletFileName(walletId));
		try {
			if (file.exists())
				loadWallet(walletId);
			else
				createWallet(walletId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String myServerId = DataModel.getMyServerId();
		if (myServerId != null)
			myServerId = OTAPI.getServerIdFromPartial(myServerId);
		if (!Util.isValidString(myServerId))
			Settings.getInstance().setMyServerId(DataModel.EMPTY);
		
		String myNymId = DataModel.getMyNymId();
		if (myNymId != null)
			myNymId = OTAPI.getNymIdFromPartial(myNymId);
		if (!Util.isValidString(myNymId))
			Settings.getInstance().setMyNymId(DataModel.EMPTY);
		
		String myAssetId = DataModel.getMyAssetId();
		if (myAssetId != null)
			myAssetId = OTAPI.getAssetIdFromPartial(myAssetId);
		if (!Util.isValidString(myAssetId))
			Settings.getInstance().setMyAssetId(DataModel.EMPTY);
		
		String myAccountId = DataModel.getMyAccountId();
		if (myAccountId != null)
			myAccountId = OTAPI.getAccountIdFromPartial(myAccountId);
		if (!Util.isValidString(myAccountId))
			Settings.getInstance().setMyAccountId(DataModel.EMPTY);
		
		Settings.getInstance().save();
		showConfig();
		verbose = false;
		applyVerbose();
	}

	public static void toggleVerbose() {
		verbose = !verbose;
		applyVerbose();
		print(String.format("Verbose is now %s", verbose ? "on" : "off"));
	}

	public static void setSophistication(Sophistication sophistication) {
		Settings.getInstance().setSophistication(sophistication.getValue());
		Settings.getInstance().save();
	}

	public static void setMyServerId(String serverId) throws Exception {
		String myServerId;
		if (!Util.isValidString(serverId)) {
			myServerId = DataModel.EMPTY;
		} else {
			myServerId = parseServerId(serverId);
			if (myServerId.equals(DataModel.getMyServerId())) {
				print(myServerId);
				print("Already using this server");
				return;
			}
		}
		Settings.getInstance().setMyServerId(myServerId);
		if (!myServerId.equals(getAccountServerId(DataModel.getMyAccountId())))
			Settings.getInstance().setMyAccountId(DataModel.EMPTY);
		Settings.getInstance().save();
		showConfig();
	}
	
	public static void setMyNymId(String nymId) throws Exception {
		String myNymId;
		if (!Util.isValidString(nymId)) {
			myNymId = DataModel.EMPTY;
		} else {
			myNymId = parseNymId(nymId);
			if (myNymId.equals(DataModel.getMyNymId())) {
				showNym(myNymId);
				print("Already using this nym");
				return;
			}
		}
		Settings.getInstance().setMyNymId(myNymId);
		if (!myNymId.equals(getAccountNymId(DataModel.getMyAccountId())))
			Settings.getInstance().setMyAccountId(DataModel.EMPTY);
		Settings.getInstance().save();
		showConfig();
	}
	
	public static void setMyAssetId(String assetId) throws Exception {
		String myAssetId;
		if (!Util.isValidString(assetId)) {
			myAssetId = DataModel.EMPTY;
		} else {
			myAssetId = parseAssetId(assetId);
			if (myAssetId.equals(DataModel.getMyAssetId())) {
				showAsset(myAssetId);
				print("Already using this asset");
				return;
			}
		}
		Settings.getInstance().setMyAssetId(myAssetId);
		if (!myAssetId.equals(getAccountAssetId(DataModel.getMyAccountId())))
			Settings.getInstance().setMyAccountId(DataModel.EMPTY);
		Settings.getInstance().save();
		showConfig();
	}

	public static void setMyAccountId(String accountId) throws Exception {
		String myAccountId;
		if (!Util.isValidString(accountId)) {
			myAccountId = DataModel.EMPTY;
		} else {
			myAccountId = parseAccountId(accountId);
			if (myAccountId.equals(DataModel.getMyAccountId())) {
				print(myAccountId);
				print("Already using this account");
				return;
			}
			String myAssetId = getAccountAssetId(myAccountId);
			String myNymId = getAccountNymId(myAccountId);
			String myServerId = getAccountServerId(myAccountId);
			Settings.getInstance().setMyServerId(myServerId);
			Settings.getInstance().setMyNymId(myNymId);
			Settings.getInstance().setMyAssetId(myAssetId);
		}
		Settings.getInstance().setMyAccountId(myAccountId);
		Settings.getInstance().save();
	}
	
	public static boolean hasAccess(Sophistication sophistication) {
		return DataModel.getSophistication().hasAccess(sophistication);
	}
	
	public static UTC getTime() {
		return UTC.getDateUTC(OTAPI.getTime());
	}

	public static void showTime() {
		print(UTC.timeToString(getTime()));
	}
	
	public static void showConfig() {
		Sophistication sophistication = DataModel.getSophistication();
		String walletId = DataModel.getWalletId();
		String myServerId = DataModel.getMyServerId();
		String myNymId = DataModel.getMyNymId();
		String myAssetId = DataModel.getMyAssetId();
		String myAccountId = DataModel.getMyAccountId();
		print(Util.repeat("-", 13));
		print(String.format("%12s: %s", "Mode", sophistication));
		print(String.format("%12s: %s", "Wallet", walletId));
		print(String.format("%12s: %s (%s)", "Server", myServerId, getServerName(myServerId)));
		print(String.format("%12s: %s (%s)", "Nym", myNymId, getNymName(myNymId)));
		print(String.format("%12s: %s (%s)", "Asset", myAssetId, getAssetName(myAssetId)));
		print(String.format("%12s: %s (%s)", "Account", myAccountId, getAccountName(myAccountId)));
		print(Util.repeat("-", 13));
	}
	
	public static void showMe() throws Exception {
		showLedger(DataModel.getMyAccountId());
	}
	
	public static void createWallet(String walletId) throws Exception {
		InputStream is = ClassLoader.getSystemResource(
				ApplProperties.get().getString("wallet.xml")).openStream();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String content = s.hasNext() ? s.next() : "";
		s.close();
		is.close();
		content = content.replaceAll("\\r", "");
		FileWriter fw = new FileWriter(getWalletFileName(walletId));
		fw.write(content);
		fw.close();
	}
	
	public static void loadAndShowWallet(String walletId) throws Exception {
		loadWallet(walletId);
		Settings.getInstance().setWalletId(walletId);
		Settings.getInstance().save();
		showWallet();
	}

	public static void showWallet() {
		if (hasAccess(Sophistication.SIMPLE)) {
			print(Util.repeat("-", 70));
			showServers();
		}
		{
			print(Util.repeat("-", 70));
			showNyms();
		}
		{
			print(Util.repeat("-", 70));
			showAssets();
		}
		{
			print(Util.repeat("-", 70));
			Account.show();
		}
		print(Util.repeat("-", 70));
	}
	
	public static List<String> getServerIds() {
		List<String> servers = new ArrayList<String>();
		int count = OTAPI.getServerCount();
		for (int index = 0; index < count; index++)
			servers.add(OTAPI.getServerId(index));
		return servers;
	}
	
	public static List<String> getNymIds() {
		List<String> nyms = new ArrayList<String>();
		int count = OTAPI.getNymCount();
		for (int index = 0; index < count; index++)
			nyms.add(OTAPI.getNymId(index));
		return nyms;
	}

	public static List<String> getAssetIds() {
		List<String> assets = new ArrayList<String>();
		int count = OTAPI.getAssetCount();
		for (int index = 0; index < count; index++)
			assets.add(OTAPI.getAssetId(index));
		return assets;
	}

	public static List<String> getAccountIds() {
		List<String> accounts = new ArrayList<String>();
		int count = OTAPI.getAccountCount();
		for (int index = 0; index < count; index++)
			accounts.add(OTAPI.getAccountId(index));
		return accounts;
	}

	public static String getAccountServerId(String accountId) {
		if (!Util.isValidString(accountId))
			return null;
		return OTAPI.getAccountServerId(accountId);
	}

	public static String getAccountNymId(String accountId) {
		if (!Util.isValidString(accountId))
			return null;
		return OTAPI.getAccountNymId(accountId);
	}

	public static String getAccountAssetId(String accountId) {
		if (!Util.isValidString(accountId))
			return null;
		return OTAPI.getAccountAssetId(accountId);
	}
	
	public static Double getDouble(String s) throws Exception {
		Double value = null;
		try {
			value = new Double(s);
		} catch (Exception e) {
			error(Text.STRING_TO_DOUBLE_CONVERSION_ERROR);
		}
		return value;
	}

	public static String getAccountBalance(String accountId) {
		if (!Util.isValidString(accountId))
			return null;
		return OTAPI.getAccountBalance(accountId);
	}

	public static String getServerName(String serverId) {
		if (!Util.isValidString(serverId))
			return Text.NAME_UNKNOWN.toString();
		String serverName = OTAPI.getServerName(serverId);
		if (Util.isValidString(serverName))
			return serverName;
		return Text.NAME_UNKNOWN.toString();
	}

	public static String getNymName(String nymId) {
		if (!Util.isValidString(nymId))
			return Text.NAME_UNKNOWN.toString();
		String nymName = OTAPI.getNymName(nymId);
		if (Util.isValidString(nymName))
			return nymName;
		return Text.NAME_UNKNOWN.toString();
	}

	public static String getAssetName(String assetId) {
		if (!Util.isValidString(assetId))
			return Text.NAME_UNKNOWN.toString();
		String assetName = OTAPI.getAssetName(assetId);
		if (Util.isValidString(assetName))
			return assetName;
		return Text.NAME_UNKNOWN.toString();
	}

	public static String getAccountName(String accountId) {
		if (!Util.isValidString(accountId))
			return Text.NAME_UNKNOWN.toString();
		if (ApplProperties.get().getBoolean("account.standardNaming"))
			return getAccountStandardName(accountId);
		return OTAPI.getAccountName(accountId);
	}
	
	public static String getAccountType(String accountId) {
		if (!Util.isValidString(accountId))
			return null;
		return OTAPI.getAccountType(accountId);
	}

	public static String getAccountStandardName(String accountId) {
		String accountType = getAccountType(accountId);
		if (accountType.equals(AccountType.ISSUER.getValue())) {
			return String.format("%s%s's %s",
					Text.ISSUER_SIGN,
					getNymName(getAccountNymId(accountId)),
					getAssetName(getAccountAssetId(accountId)));
		}
		return String.format("%s's %s", getNymName(getAccountNymId(accountId)),
				getAssetName(getAccountAssetId(accountId)));
	}

	public static String getPurseStandardName(String nymId, String assetId) {
		return String.format("%s's %s", getNymName(nymId),
				getAssetName(assetId));
	}
	
	public static InstrumentType getInstrumentType(String instrument) {
		return InstrumentType.parse(OTAPI.Instrument.getType(instrument));
	}
	
	public static void showServer(String serverId) {
		print(Util.repeat("-", 13));
		print(String.format("%12s: %s", "Name", getServerName(serverId)));
		print(String.format("%12s: %s", "Server", serverId));
		print(Util.repeat("-", 13));
	}
	
	public static void showNym(String nymId) {
		print(Util.repeat("-", 13));
		print(String.format("%12s: %s", "Name", getNymName(nymId)));
		print(String.format("%12s: %s", "Nym", nymId));
		print(Util.repeat("-", 13));
	}

	public static void showAsset(String assetId) {
		print(Util.repeat("-", 13));
		print(String.format("%12s: %s", "Name", getAssetName(assetId)));
		print(String.format("%12s: %s", "Asset", assetId));
		print(Util.repeat("-", 13));
	}
	
	public static void showAccount(String accountId) {
		print(Util.repeat("-", 13));
		print(String.format("%12s: %s", "Name", getAccountName(accountId)));
		print(String.format("%12s: %s", "Account", accountId));
		print(Util.repeat("-", 13));
	}

	public static void showLedger(String accountId) throws Exception {
		String serverId = getAccountServerId(accountId);
		String nymId = getAccountNymId(accountId);
		String assetId = getAccountAssetId(accountId);
		
		String purse = OTAPI.loadPurse(serverId, nymId, assetId);
		Double purseBalanceValue = new Double(0);
		if (Util.isValidString(purse))
			purseBalanceValue = AssetModule.getPurseBalanceValue(serverId, assetId, purse);
		
		print(Util.repeat("-", 13));
		print(String.format("%12s: %s", "Name", getAccountName(accountId)));
		if (purseBalanceValue > 0)
			print(String.format("%12s: %.2f (+ %.2f)", "Balance", 
					getAccountBalanceValue(accountId),
					purseBalanceValue));
		else
			print(String.format("%12s: %.2f", "Balance", getAccountBalanceValue(accountId)));
		print(String.format("%12s: %s", "Account", accountId));
		print(String.format("%12s: %s (%s)", "Asset", assetId, getAssetName(assetId)));
		print(String.format("%12s: %s (%s)", "Nym", nymId, getNymName(nymId)));
		print(String.format("%12s: %s (%s)", "Server", serverId, getServerName(serverId)));
		
		print(Util.repeat("-", 13));
	}

	
	/**********************************************************************
     * internal
     *********************************************************************/
	
	private static String getWalletFileName(String walletId) {
		return String.format("%s/%s.%s",
				Util.getUserDataPath(), walletId, Extension.DEFINITION.getValue());
	}
	
	private static void loadWallet(String walletId) throws Exception {
		String fileName = String.format("%s.%s", walletId, Extension.DEFINITION.getValue());
		if (!OTAPI.setWallet(fileName))
			error("Failed to set wallet");
		if (!OTAPI.loadWallet())
			error("Failed to load wallet");
	}
	
	private static void applyVerbose() {
		if (verbose) {
			verboseServer = false;
			verboseClientLog = true;
			verboseClientSkip = true;
			verboseClientWarn = true;
			verboseClientSuccess = true;
		} else {
			verboseServer = ApplProperties.get().getBoolean("verbose.server");
			verboseClientLog = ApplProperties.get().getBoolean(
					"verbose.client.log");
			verboseClientSkip = ApplProperties.get().getBoolean(
					"verbose.client.skip");
			verboseClientWarn = ApplProperties.get().getBoolean(
					"verbose.client.warn");
			verboseClientSuccess = ApplProperties.get().getBoolean(
					"verbose.client.success");
		}
	}
	
	private static void showServers() {
		print(String.format("%12s:", "SERVERS"));
		int count = OTAPI.getServerCount();
		int i = 0;
		for (int index = 0; index < count; index++) {
			String serverId = OTAPI.getServerId(index);
			String serverName = getServerName(serverId);
			if (i == 0)
				print(Util.repeat("-", 13));
			print(String.format("%12d: %s (%s)", ++i, serverId, serverName));
		}
		if (i > 0)
			print(Util.repeat("-", 13));
	}
	
	private static void showNyms() {
		print(String.format("%12s:", "NYMS"));
		int count = OTAPI.getNymCount();
		int i = 0;
		for (int index = 0; index < count; index++) {
			String nymId = OTAPI.getNymId(index);
			String nymName = getNymName(nymId);
			if (i == 0)
				print(Util.repeat("-", 13));
			print(String.format("%12d: %s (%s)", ++i, nymId, nymName));
		}
		if (i > 0)
			print(Util.repeat("-", 13));
	}
	
	private static void showAssets() {
		print(String.format("%12s:", "ASSETS"));
		int count = OTAPI.getAssetCount();
		int i = 0;
		for (int index = 0; index < count; index++) {
			String assetId = OTAPI.getAssetId(index);
			String assetName = getAssetName(assetId);
			if (i == 0)
				print(Util.repeat("-", 13));
			print(String.format("%12d: %s (%s)", ++i, assetId, assetName));
		}
		if (i > 0)
			print(Util.repeat("-", 13));
	}
	
	private static Boolean isMeantForParsing(String id) {
		if (id == null)
			return null;
		int min = ApplProperties.get().getInteger("parsing.size.min");
		int len = id.length();
		if (len < min)
			return null;
		int max = ApplProperties.get().getInteger("parsing.size.max");
		return (len >= min && len <= max);
	}
	
	protected static String parseServerId(String serverId) throws Exception {
		Boolean parsing = isMeantForParsing(serverId);
		if (parsing == null)
			error(Text.PARSE_SERVER_ID_ERROR);
		if (parsing) {
			String id = OTAPI.getServerIdFromPartial(serverId);
			if (!Util.isValidString(id))
				error(Text.PARSE_SERVER_ID_ERROR);
			serverId = id;
		}
		logServerId(serverId);
		return serverId;
	}

	protected static String parseNymId(String nymId) throws Exception {
		Boolean parsing = isMeantForParsing(nymId);
		if (parsing == null)
			error(Text.PARSE_NYM_ID_ERROR);
		if (parsing) {
			String id = OTAPI.getNymIdFromPartial(nymId);
			if (!Util.isValidString(id))
				error(Text.PARSE_NYM_ID_ERROR);
			nymId = id;
		}
		logNymId(nymId);
		return nymId;
	}

	protected static String parseAssetId(String assetId) throws Exception {
		Boolean parsing = isMeantForParsing(assetId);
		if (parsing == null)
			error(Text.PARSE_ASSET_ID_ERROR);
		if (parsing) {
			String id = OTAPI.getAssetIdFromPartial(assetId);
			if (!Util.isValidString(id))
				error(Text.PARSE_ASSET_ID_ERROR);
			assetId = id;
		}
		logAssetId(assetId);
		return assetId;
	}

	protected static String parseAccountId(String accountId) throws Exception {
		Boolean parsing = isMeantForParsing(accountId);
		if (parsing == null)
			error(Text.PARSE_ACCOUNT_ID_ERROR);
		if (parsing) {
			String id = OTAPI.getAccountIdFromPartial(accountId);
			if (!Util.isValidString(id))
				error(Text.PARSE_ACCOUNT_ID_ERROR);
			accountId = id;
		}
		logAccountId(accountId);
		return accountId;
	}

	protected static void logServerId(String serverId) {
		log(String.format("%12s: %s", Text.SERVER_ID, serverId));
	}

	protected static void logNymId(String nymId) {
		log(String.format("%12s: %s", Text.NYM_ID, nymId));
	}

	protected static void logAssetId(String assetId) {
		log(String.format("%12s: %s", Text.ASSET_ID, assetId));
	}

	protected static void logAccountId(String accountId) {
		log(String.format("%12s: %s", Text.ACCOUNT_ID, accountId));
	}

	public static void print(Object s) {
		System.out.println(s);
	}

	protected static void publish(Object s) {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(s);
		System.out.println();
		System.out.println();
		System.out.println();
	}

	protected static void log(Object s) {
		if (verboseClientLog)
			print(s);
	}

	protected static void skip(String message) {
		if (verboseClientSkip)
			print(String.format("%s: %s", Text.SKIP, message));
	}

	protected static void skip(Text text) {
		skip(text.toString());
	}

	protected static void warn(String message) {
		if (verboseClientWarn)
			print(String.format("%s: %s", Text.WARN, message));
	}

	protected static void warn(String message, int result) {
		if (verboseClientWarn)
			print(String.format("%s: %s (%d)", Text.WARN, message, result));
	}

	protected static void warn(Text text) {
		warn(text.toString());
	}

	protected static void warn(Text text, int result) {
		warn(text.toString(), result);
	}

	protected static void success(String message) {
		if (verboseClientSuccess)
			print(String.format("%s: %s", Text.SUCCESS, message));
	}

	protected static void success(String message, int result) {
		if (verboseClientSuccess)
			print(String.format("%s: %s (%d)", Text.SUCCESS, message, result));
	}

	protected static void success(Text text) {
		success(text.toString());
	}

	protected static void success(Text text, int result) {
		success(text.toString(), result);
	}

	protected static void attempt(String message) {
		log(String.format("%s..", message));
	}

	protected static void attempt(Text text) {
		attempt(text.toString());
	}

	public static void error(String message) throws Exception {
		throw new Exception(message);
	}

	public static void error(String message, int result) throws Exception {
		throw new Exception(String.format("%s (%d)", message, result));
	}

	public static void error(Text text) throws Exception {
		error(text.toString());
	}

	public static void error(Text text, int result) throws Exception {
		error(text.toString(), result);
	}
	
	protected static Double getAccountBalanceValue(String accountId)
			throws Exception {
		return getDouble(getAccountBalance(accountId));
	}
	
	protected static Double getPurseBalanceValue(String serverId, String assetId, String purse) 
			throws Exception {
    	return getDouble(OTAPI.Purse.getBalance(serverId, assetId, purse));
    }
	
}
