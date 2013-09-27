package eu.opentxs.bridge.core.modules.act;

import java.util.List;

import eu.opentxs.bridge.Util;
import eu.opentxs.bridge.core.modules.OTAPI;

public class ServerModule extends ContactModule {

	protected String serverId;

	public ServerModule(String serverId) throws Exception {
		super();
		this.serverId = parseServerId(serverId);
	}

	public static String createServer(String nymId, String definition) throws Exception {
		attempt("Creating new server");
		nymId = parseNymId(nymId);

		String serverId = OTAPI.createServer(nymId, definition);
		if (!Util.isValidString(serverId))
			error("Failed to create server");
		print(serverId);
		String contract = getServerContract(serverId);
		if (!Util.isValidString(contract))
			error("Server created but failed to retrieve its contract");
		publish(contract);
		success("Server successfully created");
		return serverId;
	}

	public static String addServer(String contract) throws Exception {
		attempt("Adding server");
		// /how to check if server is already in the wallet??
		List<String> before = getServerIds();
		// /
		int result = OTAPI.addServer(contract);
		if (result != 1)
			error("Failed to add server");
		// /
		String serverId = null;
		List<String> after = getServerIds();
		for (String s : after) {
			if (!before.contains(s)) {
				serverId = s;
				break;
			}
		}
		// /
		if (!Util.isValidString(serverId))
			error("This server is already in your wallet");
		showServer(serverId);
		success("Server is added");
		return serverId;
	}

	public static void renameServer(String serverId, String serverName) throws Exception {
		attempt("Renaming server");
		serverId = parseServerId(serverId);
		if (!OTAPI.setServerName(serverId, serverName))
			error("Failed to rename");
		showServer(serverId);
		success("Server is renamed");
	}

	public static String getServerContract(String serverId) throws Exception {
		return OTAPI.getServerContract(serverId);
	}

	public static String showServerContract(String serverId) throws Exception {
		serverId = parseServerId(serverId);
		String contract = getServerContract(serverId);
		publish(contract);
		return contract;
	}

	public static void showServerAccounts(String serverId) throws Exception {
		serverId = parseServerId(serverId);
		print(String.format("%12s:", "ACCOUNTS"));
		int accountCount = OTAPI.getAccountCount();
		for (int index = 0; index < accountCount; index++) {
			String accountId = OTAPI.getAccountId(index);
			if (getAccountServerId(accountId).equals(serverId))
				showLedger(accountId);
		}
	}

	public static void deleteServer(String serverId) throws Exception {
		attempt("Deleting server");
		serverId = parseServerId(serverId);
		if (!OTAPI.canDeleteServer(serverId))
			error("Server cannot be deleted");
		if (!OTAPI.deleteServer(serverId))
			error("Failed to delete server");
		success("Server is deleted");
	}
}
