package eu.opentxs.bridge.core.modules.act;

import java.util.ArrayList;
import java.util.List;

import org.opentransactions.otapi.Storable;
import org.opentransactions.otapi.StoredObjectType;
import org.opentransactions.otapi.StringMap;

import eu.opentxs.bridge.UTC;
import eu.opentxs.bridge.Util;
import eu.opentxs.bridge.core.dto.Transaction;
import eu.opentxs.bridge.core.modules.OTAPI;

public class AssetModule extends NymModule {
	
	protected String assetId;
	
    public AssetModule(String serverId, String nymId, String assetId) throws Exception {
		super(serverId, nymId);
		this.assetId = parseAssetId(assetId);
	}
    
    public static String createAsset(String nymId, String definition) throws Exception {
    	attempt("Creating new asset");
    	nymId = parseNymId(nymId);
    	
        String assetId = OTAPI.createAsset(nymId, definition);
        if (!Util.isValidString(assetId))
        	error("Failed to create asset");
        print(assetId);
        String contract = getAssetContract(assetId);
        if (!Util.isValidString(contract))
        	error("Asset created but failed to retrieve its contract");
        publish(contract);
        success("Asset successfully created");
        return assetId;
    }

    public static String addAsset(String contract) throws Exception {
    	attempt("Adding asset");
    	///how to check if asset is already in the wallet??
    	List<String> before = getAssetIds();
    	///
    	int result = OTAPI.addAsset(contract);
    	if (result != 1)
    		error("Failed to add asset");
    	///
    	String assetId = null;
    	List<String> after = getAssetIds();
    	for (String s : after) {
    		if (!before.contains(s)) {
    			assetId = s;
    			break;
    		}
    	}
    	///
    	if (!Util.isValidString(assetId))
    		 error("This asset is already in your wallet");
    	showAsset(assetId);
    	success("Asset is added");
    	return assetId;
    }
    
    public static void renameAsset(String assetId, String assettName) throws Exception {
    	attempt("Renaming asset");
    	assetId = parseAssetId(assetId);
    	if (!OTAPI.setAssetName(assetId, assettName))
        	error("Failed to rename");
    	showAsset(assetId);
        success("Asset is renamed");
    }
    
    public void queryAsset() throws Exception {
    	attempt("Querying asset");
    	
		Storable storable1 = OTAPI.createObject(StoredObjectType.STORED_OBJ_STRING_MAP);
		if (storable1 == null)
			error("storable1 = null");
		
		StringMap map1 = StringMap.ot_dynamic_cast(storable1);
        if (map1 == null)
        	error("map == null");
		
        String[] assetIds = {assetId};
		for (String assetId : assetIds)
			map1.SetValue(assetId, "exists"); 

		final String encodedMap = OTAPI.encodeObject(map1);
		if (!Util.isValidString(encodedMap))
			error("encodedMap is empty");
		
		String message = sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.queryAssetTypes(serverId, nymId, encodedMap);
			}
		});
		if (message == null)
			error("message == null");
		
		String replyMap = OTAPI.Message.getPayload(message);
		if (!Util.isValidString(replyMap))
			error("replyMap is empty");
		
		Storable storable2 = OTAPI.decodeObject(StoredObjectType.STORED_OBJ_STRING_MAP, replyMap);
		if (storable2 == null)
			error("storable2 = null");
		
		StringMap map2 = StringMap.ot_dynamic_cast(storable2);
		if (map2 == null)
			error("map2 == null");

		List<String> confirmedAssetIds = new ArrayList<String>();
		for (String assetId : assetIds) {
			String value = map2.GetValue(assetId);
			if (value.equals("true"))
				confirmedAssetIds.add(assetId);
		}
		print(confirmedAssetIds);
	}
    
    public static String getAssetContract(String assetId) throws Exception {
    	return OTAPI.getAssetContract(assetId);
    }
    
    public static String showAssetContract(String assetId) throws Exception {
    	assetId = parseAssetId(assetId);
    	String contract = getAssetContract(assetId);
    	publish(contract);
    	return contract;
    }
    
    public static void showAssetAccounts(String assetId) throws Exception {
    	assetId = parseAssetId(assetId);
    	print(String.format("%12s:", "ACCOUNTS"));
        int accountCount = OTAPI.getAccountCount();
        for (int index = 0; index < accountCount; index++) {
        	String accountId = OTAPI.getAccountId(index);
        	if (getAccountAssetId(accountId).equals(assetId))
        		showLedger(accountId);
        }
    }
   
    public static void deleteAsset(String assetId) throws Exception {
    	attempt("Deleting asset");
    	assetId = parseAssetId(assetId);
    	if (!OTAPI.canDeleteAsset(assetId))
    		error("Asset cannot be deleted");
    	if (!OTAPI.deleteAsset(assetId))
    		error("Failed to delete asset");
    	success("Asset is deleted");
    }
    
    public String createAccount(String accountName) throws Exception {
    	attempt("Creating account");
    	if (!isNymRegisteredAtServer(serverId, nymId))
    		error("Nym is not registered at the server");
    	String message = sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.createAccount(serverId, nymId, assetId);
			}
		});
    	String accountId = OTAPI.getNewAccountId(message);
    	if (!Util.isValidString(accountId))
    		error("Account created but failed to retreive its id");
    	if (!Util.isValidString(accountName))
    		accountName = getAccountStandardName(accountId);
    	AccountModule.renameAccount(accountId, accountName);
    	success("Account created");
    	return accountId;
    }
    
    public String loadPurse() throws Exception {
    	String purse = OTAPI.loadPurse(serverId, nymId, assetId);
    	if (!Util.isValidString(purse))
    		return null;
    	if (getPurseBalanceValue(serverId, assetId, purse) > 0)
    		return purse;
    	return null;
    }
    
    public int getPurseSize(String purse) throws Exception {
    	attempt("Getting purse size");
    	int size = OTAPI.Purse.getSize(serverId, assetId, purse);
    	if (size < 0)
    		error("purse size is negative");
    	return size;
    }
    
    public void showPurse(String purse) throws Exception {
    	attempt("Showing purse");
    	String balance = OTAPI.Purse.getBalance(serverId, assetId, purse);
    	int size = getPurseSize(purse);
    	print(Util.repeat("-", 13));
    	print(getPurseStandardName(nymId, assetId));
    	int i = 0;
    	for (int index = 0; index < size; index++) {
    		String token = OTAPI.Purse.peek(serverId, nymId, assetId, purse);
    		if (!Util.isValidString(token))
    			error("purse peek failed, token is empty");
			purse = OTAPI.Purse.pop(serverId, nymId, assetId, purse);
    		if (!Util.isValidString(purse))
    			error("purse after pop is empty");
			String denomination = OTAPI.Token.getDenomination(serverId, assetId, token);
			int series = OTAPI.Token.getSeries(serverId, assetId, token);
			UTC validFrom = UTC.getDateUTC(OTAPI.Token.getValidFrom(serverId, assetId, token));
			UTC validTo = UTC.getDateUTC(OTAPI.Token.getValidTo(serverId, assetId, token));
			String status = (validTo.isAfter(getTime()) ? "valid" : "expired");
    		print(String.format("%5d: %4s | %2d | %s | %s | %s",
    				++i, denomination, series, UTC.dateToString(validFrom), UTC.dateToString(validTo), status));
    	}
    	print(String.format("%s: %4s", "Total", balance));
    	print(Util.repeat("-", 13));
    }
    
    public String exportPurseToCash(List<Integer> indices, String hisNymId) throws Exception {
    	attempt("Exporting purse to cash");
    	String purse = OTAPI.loadPurse(serverId, nymId, assetId);
    	if (!Util.isValidString(purse))
    		error("This purse does not exist");
    	String newPurse;
    	if (Util.isValidString(hisNymId))
    		newPurse = processPurse(purse, indices, hisNymId, true);
    	else
    		newPurse = processPurseWithPassphrase(purse, indices);
    	if (!Util.isValidString(newPurse))
    		error("failed to export cash");
		success("Purse successfully exported to cash");
		return newPurse;
    }
    
    public static String getCashAssetId(String cash) throws Exception {
    	if (!Util.isValidString(cash))
    		error("cash is empty");
    	String assetId = OTAPI.Instrument.getAssetId(cash);
    	if (!Util.isValidString(assetId))
    		error("assetId is empty");
    	return assetId;
    }
    
    public void verifyCash(String cash) throws Exception {
    	if (!Util.isValidString(cash))
    		error("cash is empty");
		String hisNymId = OTAPI.Instrument.getRecipientNymId(cash);
		if (Util.isValidString(hisNymId) && !nymId.equals(hisNymId))
			error("Your nym and the recipient nym do not match");
		if (!assetId.equals(OTAPI.Instrument.getAssetId(cash)))
			error("Your account's asset type is incompatible with the payment");
	}
    
    public void importCashToPurse(String cash) throws Exception {
    	attempt("Importing cash to purse");
    	if (!Util.isValidString(cash))
    		error("cash is empty");
    	if (!OTAPI.importPurse(serverId, nymId, assetId, cash))
    		error("failed to import cash");
    	Double volume = getPurseBalanceValue(serverId, assetId, cash);
    	print(String.format("Imported volume: %.2f", volume));
    	success("Cash successfully imported to purse");
    }
    
    public String processPurse(String purse, List<Integer> indices, String hisNymId, boolean publishResult) throws Exception {
    	attempt("Processing purse");
    	if (!Util.isValidString(hisNymId))
    		error("hisNymId is empty");
		String newPurse = OTAPI.createPurse(serverId, nymId, assetId, hisNymId);
		if (!Util.isValidString(newPurse))
			error("new purse failed to create");
		String oldPurse = OTAPI.createPurse(serverId, nymId, assetId, nymId);
		if (!Util.isValidString(oldPurse))
			error("old purse failed to create");
		int size = getPurseSize(purse);
		double volume = 0;
		for (int index = 0; index < size; index++) {
			String token = OTAPI.Purse.peek(serverId, nymId, assetId, purse);
			if (!Util.isValidString(token))
				error("purse peek failed, token is empty");
			purse = OTAPI.Purse.pop(serverId, nymId, assetId, purse);
			if (!Util.isValidString(purse))
				error("purse after pop is empty");
			if (indices == null || indices.size() == 0 || indices.contains(index)) {
				volume += getTokenDenomination(token);
				String exportedToken = OTAPI.Token.changeOwner(serverId, nymId, assetId, token, hisNymId);
				if (!Util.isValidString(exportedToken))
    				error("exported token is empty");
				newPurse = OTAPI.Purse.push(serverId, nymId, assetId, newPurse, exportedToken, hisNymId);
				if (!Util.isValidString(newPurse))
					error("purse after push is empty");
			} else {
				oldPurse = OTAPI.Purse.push(serverId, nymId, assetId, oldPurse, token, nymId);
				if (!Util.isValidString(oldPurse))
					error("purse after push is empty");
			}
		}
		if (!OTAPI.savePurse(serverId, nymId, assetId, oldPurse))
			error("failed to save purse");
		if (publishResult)
			publish(newPurse);
		print(String.format("Exported volume: %.2f", volume));
		success("Purse is processed");
		return newPurse;
    }
 
    
    public String processPurseWithPassphrase(String purse, List<Integer> indices) throws Exception {
    	attempt("Processing purse with passphrase");
    	///this causes crash!!!
		String newPurse = OTAPI.createPurseWithPassphrase(serverId, nymId, assetId);
		if (!Util.isValidString(newPurse))
			error("new purse failed to create");
		String oldPurse = OTAPI.createPurse(serverId, nymId, assetId, nymId);
		if (!Util.isValidString(oldPurse))
			error("old purse failed to create");
		int size = getPurseSize(purse);
		double volume = 0;
		for (int index = 0; index < size; index++) {
			String token = OTAPI.Purse.peek(serverId, nymId, assetId, purse);
			if (!Util.isValidString(token))
				error("purse peek failed, token is empty");
			purse = OTAPI.Purse.pop(serverId, nymId, assetId, purse);
			if (!Util.isValidString(purse))
				error("purse after pop is empty");
			if (indices == null || indices.size() == 0 || indices.contains(index)) {
				volume += getTokenDenomination(token);
				//String exportedToken = OTAPI.Token.changeOwner(serverId, nymId, assetId, token, hisNymId);
				//if (!Util.isValidString(exportedToken))
    			//	error("exported token is empty");
				newPurse = OTAPI.Purse.push(serverId, nymId, assetId, newPurse, token, null);
				if (!Util.isValidString(newPurse))
					error("purse after push is empty");
			} else {
				oldPurse = OTAPI.Purse.push(serverId, nymId, assetId, oldPurse, token, nymId);
				if (!Util.isValidString(oldPurse))
					error("purse after push is empty");
			}
		}
		if (!OTAPI.savePurse(serverId, nymId, assetId, oldPurse))
			error("failed to save purse");
		publish(newPurse);
		print(String.format("Exported volume: %.2f", volume));
		success("Purse is split");
		return newPurse;
    }
    
	public void showPayInbox() throws Exception {
		String ledger = OTAPI.loadPayInbox(serverId, nymId);
		if (!Util.isValidString(ledger)) {
			print("PayInbox is empty");
			return;
		}
		int size = OTAPI.Ledger.getCount(serverId, nymId, nymId, ledger);
		if (size <= 0) {
			if (size < 0)
				warn("PayInbox size is abnormal", size);
			else
				print("PayInbox size is zero");
			return;
		}
		List<Transaction> list = Transaction.getListForNym(serverId, nymId, assetId, ledger, size);
		Transaction.showH(list);
	}
    
    public void showPayOutbox() throws Exception {
		int size = OTAPI.GetNym.outpaymentsCount(nymId);
		if (size <= 0) {
			if (size < 0)
				warn("Outpayments size is abnormal", size);
			else
				print("Outpayments size is zero");
			return;
		}
		List<Transaction> list = Transaction.getListOfOutpayments(serverId, nymId, assetId, size, null);
		Transaction.showH(list);
	}
    
    
    /**********************************************************************
     * internal
     *********************************************************************/
    
    private Double getTokenDenomination(String token) throws Exception {
    	return getDouble(OTAPI.Token.getDenomination(serverId, assetId, token));
    }

}
