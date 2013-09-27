package eu.opentxs.bridge.core.modules;

import org.opentransactions.otapi.Storable;
import org.opentransactions.otapi.otapi;
import org.opentransactions.otapi.otapiJNI;

public class OTAPI {

	public enum Box {
		NYMBOX(0), INBOX(1), OUTBOX(2);
		private int index;
		private Box(int index) {
			this.index = index;
		}
		public int getIndex() {
			return index;
		}
	}

	public static boolean init() {
		return otapiJNI.OTAPI_Basic_Init();
	}
	public static boolean appStartup() {
		return otapiJNI.OTAPI_Basic_AppStartup();
	}
	public static boolean appShutdown() {
		return otapiJNI.OTAPI_Basic_AppShutdown();
	}

	public static boolean walletExists() {
		return otapiJNI.OTAPI_Basic_WalletExists();
	}
	public static boolean loadWallet() {
		return otapiJNI.OTAPI_Basic_LoadWallet();
	}
	public static boolean setWallet(String fileName) {
		return otapiJNI.OTAPI_Basic_SetWallet(fileName);
	}
	public static boolean switchWallet() {
		return otapiJNI.OTAPI_Basic_SwitchWallet();
	}

	public static String getTime() {
		return otapiJNI.OTAPI_Basic_GetTime();
	}

	public static String encode(String s, boolean lineBreaks) {
		return otapiJNI.OTAPI_Basic_Encode(s, lineBreaks);
	}
	public static String decode(String encoded, boolean lineBreaks) {
		return otapiJNI.OTAPI_Basic_Decode(encoded, lineBreaks);
	}

	public static class Message {
		public static String getLedger(String message) {
			return otapiJNI.OTAPI_Basic_Message_GetLedger(message);
		}
		public static String getPayload(String message) {
			return otapiJNI.OTAPI_Basic_Message_GetPayload(message);
		}
	}

	public static class Wallet {
		public static String exportNym(String nymId) {
			return otapiJNI.OTAPI_Basic_Wallet_ExportNym(nymId);
		}
		public static String importNym(String content) {
			return otapiJNI.OTAPI_Basic_Wallet_ImportNym(content);
		}
	}

	public static void flushMessageBuffer() {
		otapiJNI.OTAPI_Basic_FlushMessageBuffer();
	}
	public static String popMessageBuffer(String requestId, String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_PopMessageBuffer(requestId, serverId, nymId);
	}
	public static int verifyMessage(String message) {
		return otapiJNI.OTAPI_Basic_Message_GetSuccess(message);
	}
	public static int verifyTransactionMessage(String serverId, String nymId, String accountId, String message) {
		return otapiJNI.OTAPI_Basic_Message_GetTransactionSuccess(serverId, nymId, accountId, message);
	}
	public static int verifyBalanceAgreement(String serverId, String nymId, String accountId, String message) {
		return otapiJNI.OTAPI_Basic_Message_GetBalanceAgreementSuccess(serverId, nymId, accountId, message);
	}
	public static int synchronizeRequestNumber(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_getRequest(serverId, nymId);
	}
	public static boolean verifyAccountReceipt(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_VerifyAccountReceipt(serverId, nymId, accountId);
	}

	public static String getServerIdFromPartial(String id) {
		return otapiJNI.OTAPI_Basic_Wallet_GetServerIDFromPartial(id);
	}
	public static String getNymIdFromPartial(String id) {
		return otapiJNI.OTAPI_Basic_Wallet_GetNymIDFromPartial(id);
	}
	public static String getAssetIdFromPartial(String id) {
		return otapiJNI.OTAPI_Basic_Wallet_GetAssetIDFromPartial(id);
	}
	public static String getAccountIdFromPartial(String id) {
		return otapiJNI.OTAPI_Basic_Wallet_GetAccountIDFromPartial(id);
	}

	public static int getNymCount() {
		return otapiJNI.OTAPI_Basic_GetNymCount();
	}
	public static int getServerCount() {
		return otapiJNI.OTAPI_Basic_GetServerCount();
	}
	public static int getAssetCount() {
		return otapiJNI.OTAPI_Basic_GetAssetTypeCount();
	}
	public static int getAccountCount() {
		return otapiJNI.OTAPI_Basic_GetAccountCount();
	}

	public static String getNymId(int index) {
		return otapiJNI.OTAPI_Basic_GetNym_ID(index);
	}
	public static String getServerId(int index) {
		return otapiJNI.OTAPI_Basic_GetServer_ID(index);
	}
	public static String getAssetId(int index) {
		return otapiJNI.OTAPI_Basic_GetAssetType_ID(index);
	}

	public static String getNymName(String nymId) {
		return otapiJNI.OTAPI_Basic_GetNym_Name(nymId);
	}
	public static String getServerName(String serverId) {
		return otapiJNI.OTAPI_Basic_GetServer_Name(serverId);
	}
	public static String getAssetName(String assetId) {
		return otapiJNI.OTAPI_Basic_GetAssetType_Name(assetId);
	}

	public static String getAccountId(int index) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_ID(index);
	}
	public static String getAccountName(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_Name(accountId);
	}
	public static String getAccountNymId(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_NymID(accountId);
	}
	public static String getAccountServerId(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_ServerID(accountId);
	}
	public static String getAccountAssetId(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_AssetTypeID(accountId);
	}
	public static String getAccountBalance(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_Balance(accountId);
	}

	/**
	 * SERVER
	 */
	public static String createServer(String nymId, String definition) {
		return otapiJNI.OTAPI_Basic_CreateServerContract(nymId, definition);
	}
	public static int addServer(String contract) {
		return otapiJNI.OTAPI_Basic_AddServerContract(contract);
	}
	public static String getServerContract(String serverId) {
		return otapiJNI.OTAPI_Basic_GetServer_Contract(serverId);
	}
	public static boolean setServerName(String serverId, String serverName) {
		return otapiJNI.OTAPI_Basic_SetServer_Name(serverId, serverName);
	}

	public static boolean canDeleteServer(String serverId) {
		return otapiJNI.OTAPI_Basic_Wallet_CanRemoveServer(serverId);
	}
	public static boolean deleteServer(String serverId) {
		return otapiJNI.OTAPI_Basic_Wallet_RemoveServer(serverId);
	}

	public static int pingServer(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_checkServerID(serverId, nymId);
	}

	/** For emergency/testing use only - this call forces you to trust the server */
	public static boolean resyncNymWithServer(String serverId, String nymId, String message) {
		return otapiJNI.OTAPI_Basic_ResyncNymWithServer(serverId, nymId, message);
	}

	/**
	 * NYM
	 */
	public static String createNym(int keySize, String nymIdSource, String altLocation) {
		return otapiJNI.OTAPI_Basic_CreateNym(keySize, nymIdSource, altLocation);
	}
	public static boolean setNymName(String nymId, String nymName) {
		return otapiJNI.OTAPI_Basic_SetNym_Name(nymId, nymId, nymName);
	}

	// not working as expected
	@Deprecated
	public static boolean canDeleteNym(String nymId) {
		return otapiJNI.OTAPI_Basic_Wallet_CanRemoveNym(nymId);
	}
	public static boolean deleteNym(String nymId) {
		return otapiJNI.OTAPI_Basic_Wallet_RemoveNym(nymId);
	}

	public static boolean isNymRegisteredAtServer(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_IsNym_RegisteredAtServer(nymId, serverId);
	}
	public static int registerNymAtServer(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_createUserAccount(serverId, nymId);
	}
	public static int removeNymFromServer(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_deleteUserAccount(serverId, nymId);
	}

	/**
	 * ASSET
	 */
	public static String createAsset(String nymId, String definition) {
		return otapiJNI.OTAPI_Basic_CreateAssetContract(nymId, definition);
	}

	public static String getAssetContract(String assetId) {
		return otapiJNI.OTAPI_Basic_GetAssetType_Contract(assetId);
	}

	public static int issueAsset(String serverId, String nymId, String contract) {
		return otapiJNI.OTAPI_Basic_issueAssetType(serverId, nymId, contract);
	}
	public static String getNewIssuerAccountId(String message) {
		return otapiJNI.OTAPI_Basic_Message_GetNewIssuerAcctID(message);
	}

	// why is it not returning assetId?
	public static int addAsset(String contract) {
		return otapiJNI.OTAPI_Basic_AddAssetContract(contract);
	}

	public static String getNewAssetId(String message) {
		return otapiJNI.OTAPI_Basic_Message_GetNewAssetTypeID(message);
	}

	// decoding does not work on server side
	public static int queryAssetTypes(String serverId, String nymId, String encodedMap) {
		return otapiJNI.OTAPI_Basic_queryAssetTypes(serverId, nymId, encodedMap);
	}

	public static boolean setAssetName(String assetId, String assetName) {
		return otapiJNI.OTAPI_Basic_SetAssetType_Name(assetId, assetName);
	}

	public static boolean canDeleteAsset(String assetId) {
		return otapiJNI.OTAPI_Basic_Wallet_CanRemoveAssetType(assetId);
	}
	public static boolean deleteAsset(String assetId) {
		return otapiJNI.OTAPI_Basic_Wallet_RemoveAssetType(assetId);
	}

	/**
	 * ACCOUNT
	 */
	// why is it not returning accountId?
	public static int createAccount(String serverId, String nymId, String assetId) {
		return otapiJNI.OTAPI_Basic_createAssetAccount(serverId, nymId, assetId);
	}
	public static String getNewAccountId(String message) {
		return otapiJNI.OTAPI_Basic_Message_GetNewAcctID(message);
	}
	// why is nymId needed?
	public static boolean setAccountName(String nymId, String accountId, String accountName) {
		return otapiJNI.OTAPI_Basic_SetAccountWallet_Name(accountId, nymId, accountName);
	}
	public static String getAccountType(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_Type(accountId);
	}
	public static boolean canDeleteAccount(String accountId) {
		return otapiJNI.OTAPI_Basic_Wallet_CanRemoveAccount(accountId);
	}
	// why is nymId needed?
	public static int deleteAccountFromServer(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_deleteAssetAccount(serverId, nymId, accountId);
	}

	/** Send a message to the server asking it to send you the latest copy of any of your asset account */
	public static int synchronizeAccount(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_getAccount(serverId, nymId, accountId);
	}

	public static String getInboxHashCached(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_InboxHash(accountId);
	}
	public static String getInboxHashLocal(String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_GetNym_InboxHash(accountId, nymId);
	}

	public static String getOutboxHashCached(String accountId) {
		return otapiJNI.OTAPI_Basic_GetAccountWallet_OutboxHash(accountId);
	}
	public static String getOutboxHashLocal(String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_GetNym_OutboxHash(accountId, nymId);
	}

	public static String getNymboxHashCached(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_GetNym_RecentHash(serverId, nymId);
	}
	public static String getNymboxHashLocal(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_GetNym_NymboxHash(serverId, nymId);
	}

	public static int getInbox(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_getInbox(serverId, nymId, accountId);
	}
	public static int getOutbox(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_getOutbox(serverId, nymId, accountId);
	}
	public static int getNymbox(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_getNymbox(serverId, nymId);
	}
	public static String loadRecordbox(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_LoadRecordBox(serverId, nymId, accountId);
	}
	public static String loadPayInbox(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_LoadPaymentInbox(serverId, nymId);
	}

	public static String loadInboxNoVerify(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_LoadInboxNoVerify(serverId, nymId, accountId);
	}
	public static String loadOutboxNoVerify(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_LoadOutboxNoVerify(serverId, nymId, accountId);
	}
	public static String loadNymboxNoVerify(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_LoadNymboxNoVerify(serverId, nymId);
	}
	public static String loadRecordboxNoVerify(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_LoadRecordBoxNoVerify(serverId, nymId, accountId);
	}
	public static String loadPayInboxNoVerify(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_LoadPaymentInboxNoVerify(serverId, nymId);
	}

	public static boolean verifySignature(String nymId, String contract) {
		return otapiJNI.OTAPI_Basic_VerifySignature(nymId, contract);
	}

	public static class Ledger {
		public static int getCount(String serverId, String nymId, String accountId, String ledger) {
			return otapiJNI.OTAPI_Basic_Ledger_GetCount(serverId, nymId, accountId, ledger);
		}
		public static String getInstrumentbyIndex(String serverId, String nymId, String accountId, String ledger, int index) {
			return otapiJNI.OTAPI_Basic_Ledger_GetInstrument(serverId, nymId, accountId, ledger, index);
		}
		public static String getTransactionIdByIndex(String serverId, String nymId, String accountId, String ledger, int index) {
			return otapiJNI.OTAPI_Basic_Ledger_GetTransactionIDByIndex(serverId, nymId, accountId, ledger, index);
		}
		public static String getTransactionById(String serverId, String nymId, String accountId, String ledger, String transactionId) {
			return otapiJNI.OTAPI_Basic_Ledger_GetTransactionByID(serverId, nymId, accountId, ledger, transactionId);
		}
		public static String getTransactionByIndex(String serverId, String nymId, String accountId, String ledger, int index) {
			return otapiJNI.OTAPI_Basic_Ledger_GetTransactionByIndex(serverId, nymId, accountId, ledger, index);
		}
		public static String createResponse(String serverId, String nymId, String accountId, String originalLedger) {
			return otapiJNI.OTAPI_Basic_Ledger_CreateResponse(serverId, nymId, accountId, originalLedger);
		}
		public static String finalizeResponse(String serverId, String nymId, String accountId, String ledger) {
			return otapiJNI.OTAPI_Basic_Ledger_FinalizeResponse(serverId, nymId, accountId, ledger);
		}
	}

	public static class Transaction {
		public static String createResponse(String serverId, String nymId, String accountId, String responseLedger, String originalTransaction, boolean accept) {
			return otapiJNI.OTAPI_Basic_Transaction_CreateResponse(serverId, nymId, accountId, responseLedger, originalTransaction, accept);
		}
		public static String getType(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetType(serverId, nymId, accountId, transaction);
		}
		public static String getDisplayReferenceToNum(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetDisplayReferenceToNum(serverId, nymId, accountId, transaction);
		}
		public static String getAmount(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetAmount(serverId, nymId, accountId, transaction);
		}
		public static String getSenderNymId(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetSenderUserID(serverId, nymId, accountId, transaction);
		}
		public static String getSenderAccountId(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetSenderAcctID(serverId, nymId, accountId, transaction);
		}
		public static String getRecipientNymId(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetRecipientUserID(serverId, nymId, accountId, transaction);
		}
		public static String getRecipientAccountId(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetRecipientAcctID(serverId, nymId, accountId, transaction);
		}
		public static String getDateSigned(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetDateSigned(serverId, nymId, accountId, transaction);
		}
		public static String getVoucher(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Transaction_GetVoucher(serverId, nymId, accountId, transaction);
		}
	}

	public static class Pending {
		public static String getNote(String serverId, String nymId, String accountId, String transaction) {
			return otapiJNI.OTAPI_Basic_Pending_GetNote(serverId, nymId, accountId, transaction);
		}
	}

	public static class Instrument {
		public static String getAmount(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetAmount(instrument);
		}
		public static String getTransactionNum(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetTransNum(instrument);
		}
		public static String getValidFrom(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetValidFrom(instrument);
		}
		public static String getValidTo(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetValidTo(instrument);
		}
		public static String getNote(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetMemo(instrument);
		}
		public static String getType(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetType(instrument);
		}
		public static String getServerId(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetServerID(instrument);
		}
		public static String getAssetId(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetAssetID(instrument);
		}
		public static String getSenderNymId(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetSenderUserID(instrument);
		}
		public static String getSenderAccountId(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetSenderAcctID(instrument);
		}
		public static String getRecipientNymId(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetRecipientUserID(instrument);
		}
		public static String getRecipientAccountId(String instrument) {
			return otapiJNI.OTAPI_Basic_Instrmnt_GetRecipientAcctID(instrument);
		}
	}

	public static class ReplyNotice {
		public static String getRequestId(String serverId, String nymId, String transaction) {
			return otapiJNI.OTAPI_Basic_ReplyNotice_GetRequestNum(serverId, nymId, transaction);
		}
	}

	public static boolean haveAlreadySeenReply(String serverId, String nymId, String requestId) {
		return otapiJNI.OTAPI_Basic_HaveAlreadySeenReply(serverId, nymId, requestId);
	}
	public static boolean doesBoxReceiptExist(String serverId, String nymId, String accountId, Box box, String transactionId) {
		return otapiJNI.OTAPI_Basic_DoesBoxReceiptExist(serverId, nymId, accountId, box.getIndex(), transactionId);
	}
	public static int getBoxReceipt(String serverId, String nymId, String accountId, Box box, String transactionId) {
		return otapiJNI.OTAPI_Basic_getBoxReceipt(serverId, nymId, accountId, box.getIndex(), transactionId);
	}

	public static int getTransactionNumbersCount(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_GetNym_TransactionNumCount(serverId, nymId);
	}
	public static int getTransactionNumbers(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_getTransactionNumber(serverId, nymId);
	}

	public static String loadInbox(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_LoadInbox(serverId, nymId, accountId);
	}
	public static String loadOutbox(String serverId, String nymId, String accountId) {
		return otapiJNI.OTAPI_Basic_LoadOutbox(serverId, nymId, accountId);
	}
	public static String loadNymbox(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_LoadNymbox(serverId, nymId);
	}

	public static int processInbox(String serverId, String nymId, String accountId, String ledger) {
		return otapiJNI.OTAPI_Basic_processInbox(serverId, nymId, accountId, ledger);
	}

	public static int processNymbox(String serverId, String nymId) {
		return otapiJNI.OTAPI_Basic_processNymbox(serverId, nymId);
	}

	public static boolean mintIsStillGood(String serverId, String assetId) {
		return otapiJNI.OTAPI_Basic_Mint_IsStillGood(serverId, assetId);
	}
	public static int getMint(String serverId, String nymId, String assetId) {
		return otapiJNI.OTAPI_Basic_getMint(serverId, nymId, assetId);
	}
	public static String loadMint(String serverId, String assetId) {
		return otapiJNI.OTAPI_Basic_LoadMint(serverId, assetId);
	}

	public static String loadPurse(String serverId, String nymId, String assetId) {
		return otapiJNI.OTAPI_Basic_LoadPurse(serverId, assetId, nymId);
	}
	public static String createPurse(String serverId, String nymId, String assetId, String ownerNymId) {
		return otapiJNI.OTAPI_Basic_CreatePurse(serverId, assetId, ownerNymId, nymId);
	}
	public static String createPurseWithPassphrase(String serverId, String nymId, String assetId) {
		return otapiJNI.OTAPI_Basic_CreatePurse_Passphrase(serverId, assetId, nymId);
	}
	public static boolean savePurse(String serverId, String nymId, String assetId, String purse) {
		return otapiJNI.OTAPI_Basic_SavePurse(serverId, assetId, nymId, purse);
	}
	public static boolean importPurse(String serverId, String nymId, String assetId, String purse) {
		return otapiJNI.OTAPI_Basic_Wallet_ImportPurse(serverId, assetId, nymId, purse);
	}

	public static class Purse {
		public static String getBalance(String serverId, String assetId, String purse) {
			return otapiJNI.OTAPI_Basic_Purse_GetTotalValue(serverId, assetId, purse);
		}
		public static int getSize(String serverId, String assetId, String purse) {
			return otapiJNI.OTAPI_Basic_Purse_Count(serverId, assetId, purse);
		}
		public static String peek(String serverId, String nymId, String assetId, String purse) {
			return otapiJNI.OTAPI_Basic_Purse_Peek(serverId, assetId, nymId, purse);
		}
		public static String pop(String serverId, String nymId, String assetId, String purse) {
			return otapiJNI.OTAPI_Basic_Purse_Pop(serverId, assetId, nymId, purse);
		}
		public static String push(String serverId, String nymId, String assetId, String purse, String token, String hisNymId) {
			return otapiJNI.OTAPI_Basic_Purse_Push(serverId, assetId, nymId, hisNymId, purse, token);
		}
		public static boolean hasPassword(String serverId, String purse) {
			return otapiJNI.OTAPI_Basic_Purse_HasPassword(serverId, purse);
		}
	}

	public static class Token {
		public static String getDenomination(String serverId, String assetId, String token) {
			return otapiJNI.OTAPI_Basic_Token_GetDenomination(serverId, assetId, token);
		}
		public static int getSeries(String serverId, String assetId, String token) {
			return otapiJNI.OTAPI_Basic_Token_GetSeries(serverId, assetId, token);
		}
		public static String getValidFrom(String serverId, String assetId, String token) {
			return otapiJNI.OTAPI_Basic_Token_GetValidFrom(serverId, assetId, token);
		}
		public static String getValidTo(String serverId, String assetId, String token) {
			return otapiJNI.OTAPI_Basic_Token_GetValidTo(serverId, assetId, token);
		}
		public static String changeOwner(String serverId, String nymId, String assetId, String token, String hisNymId) {
			return otapiJNI.OTAPI_Basic_Token_ChangeOwner(serverId, assetId, token, nymId, nymId, hisNymId);
		}
		public static String getId(String serverId, String assetId, String token) {
			return otapiJNI.OTAPI_Basic_Token_GetID(serverId, assetId, token);
		}
	}

	public static int notarizeTransfer(String serverId, String nymId, String accountId, Double volume, String hisAccountId, String note) {
		return otapiJNI.OTAPI_Basic_notarizeTransfer(serverId, nymId, accountId, hisAccountId, volume.toString(), note);
	}

	public static int notarizeWithdrawal(String serverId, String nymId, String accountId, Double volume) {
		return otapiJNI.OTAPI_Basic_notarizeWithdrawal(serverId, nymId, accountId, volume.toString());
	}
	public static int notarizeDeposit(String serverId, String nymId, String accountId, String purse) {
		return otapiJNI.OTAPI_Basic_notarizeDeposit(serverId, nymId, accountId, purse);
	}

	public static int writeVoucher(String serverId, String nymId, String accountId, Double volume, String hisNymId, String note) {
		return otapiJNI.OTAPI_Basic_withdrawVoucher(serverId, nymId, accountId, hisNymId, note, volume.toString());
	}

	public static String writeCheque(String serverId, String nymId, String accountId, Double volume, String hisNymId, String note, String validFrom, String validTo) {
		return otapiJNI.OTAPI_Basic_WriteCheque(serverId, volume.toString(), validFrom, validTo, accountId, nymId, note, hisNymId);
	}
	public static boolean cancelCheque(String serverId, String nymId, String accountId, String cheque) {
		return otapiJNI.OTAPI_Basic_DiscardCheque(serverId, nymId, accountId, cheque);
	}
	public static int executeCheque(String serverId, String nymId, String accountId, String cheque) {
		return otapiJNI.OTAPI_Basic_depositCheque(serverId, nymId, accountId, cheque);
	}

	public static int sendUserPayment(String serverId, String nymId, String hisNymId, String hisPublicKey, String payment) {
		return otapiJNI.OTAPI_Basic_sendUserInstrument(serverId, nymId, hisNymId, hisPublicKey, payment, "");
		// return otapiJNI.OTAPI_Basic_sendUserInstrument(serverId, nymId, hisNymId, hisPublicKey, payment);
	}

	public static boolean recordPayment(String serverId, String nymId, boolean isInbox, int index, boolean saveCopy) {
		// return otapiJNI.OTAPI_Basic_RecordPayment(serverId, nymId, isInbox, index);
		return otapiJNI.OTAPI_Basic_RecordPayment(serverId, nymId, isInbox, index, saveCopy);
	}

	public static boolean clearRecord(String serverId, String nymId, String accountId, int index, boolean clearAll) {
		return otapiJNI.OTAPI_Basic_ClearRecord(serverId, nymId, accountId, index, clearAll);
	}

	public static class Nym {
		public static boolean verifyOutpaymentsByIndex(String nymId, int index) {
			return otapiJNI.OTAPI_Basic_Nym_VerifyOutpaymentsByIndex(nymId, index);
		}
		public static boolean removeOutpaymentsByIndex(String nymId, int index) {
			return otapiJNI.OTAPI_Basic_Nym_RemoveOutpaymentsByIndex(nymId, index);
		}
	}

	public static class GetNym {
		public static int outpaymentsCount(String nymId) {
			return otapiJNI.OTAPI_Basic_GetNym_OutpaymentsCount(nymId);
		}
		public static String outpaymentsContentsByIndex(String nymId, int index) {
			return otapiJNI.OTAPI_Basic_GetNym_OutpaymentsContentsByIndex(nymId, index);
		}
		public static String outpaymentsRecipientNymIdByIndex(String nymId, int index) {
			return otapiJNI.OTAPI_Basic_GetNym_OutpaymentsRecipientIDByIndex(nymId, index);
		}
		public static String outpaymentsServerIdByIndex(String nymId, int index) {
			return otapiJNI.OTAPI_Basic_GetNym_OutpaymentsServerIDByIndex(nymId, index);
		}
	}

	public static int checkUser(String serverId, String nymId, String targetNymId) {
		return otapiJNI.OTAPI_Basic_checkUser(serverId, nymId, targetNymId);
	}
	public static String loadUserPublicKeyEncryption(String nymId) {
		return otapiJNI.OTAPI_Basic_LoadUserPubkey_Encryption(nymId);
	}
	public static String loadPublicKeyEncryption(String nymId) {
		return otapiJNI.OTAPI_Basic_LoadPubkey_Encryption(nymId);
	}

	public static Storable createObject(int objectType) {
		return otapi.CreateObject(objectType);
	}
	public static String encodeObject(Storable object) {
		return otapi.EncodeObject(object);
	}
	public static Storable decodeObject(int objectType, String s) {
		return otapi.DecodeObject(objectType, s);
	}
	public static boolean exists(String folder, String first, String second) {
		return otapiJNI.Exists__SWIG_1(folder, first, second);
	}

	//
	// public static void Output(int nLogLevel, String strOutput) {
	// otapiJNI.OTAPI_Basic_Output(nLogLevel, strOutput);
	// }
	//
	// public static String NumList_Add(String strNumList, String strNumbers) {
	// return otapiJNI.OTAPI_Basic_NumList_Add(strNumList, strNumbers);
	// }
	//
	// public static String NumList_Remove(String strNumList, String strNumbers) {
	// return otapiJNI.OTAPI_Basic_NumList_Remove(strNumList, strNumbers);
	// }
	//
	// public static boolean NumList_VerifyQuery(String strNumList, String strNumbers) {
	// return otapiJNI.OTAPI_Basic_NumList_VerifyQuery(strNumList, strNumbers);
	// }
	//
	// public static boolean NumList_VerifyAll(String strNumList, String strNumbers) {
	// return otapiJNI.OTAPI_Basic_NumList_VerifyAll(strNumList, strNumbers);
	// }
	//
	// public static int NumList_Count(String strNumList) {
	// return otapiJNI.OTAPI_Basic_NumList_Count(strNumList);
	// }
	//
	// public static String Encrypt(String RECIPIENT_NYM_ID, String strPlaintext) {
	// return otapiJNI.OTAPI_Basic_Encrypt(RECIPIENT_NYM_ID, strPlaintext);
	// }
	//
	// public static String Decrypt(String RECIPIENT_NYM_ID, String strCiphertext) {
	// return otapiJNI.OTAPI_Basic_Decrypt(RECIPIENT_NYM_ID, strCiphertext);
	// }
	//
	// public static String CreateSymmetricKey() {
	// return otapiJNI.OTAPI_Basic_CreateSymmetricKey();
	// }
	//
	// public static String SymmetricEncrypt(String SYMMETRIC_KEY, String PLAintEXT) {
	// return otapiJNI.OTAPI_Basic_SymmetricEncrypt(SYMMETRIC_KEY, PLAintEXT);
	// }
	//
	// public static String SymmetricDecrypt(String SYMMETRIC_KEY, String CIPHERTEXT_ENVELOPE) {
	// return otapiJNI.OTAPI_Basic_SymmetricDecrypt(SYMMETRIC_KEY, CIPHERTEXT_ENVELOPE);
	// }
	//
	// public static String SignContract(String SIGNER_NYM_ID, String THE_CONTRACT) {
	// return otapiJNI.OTAPI_Basic_SignContract(SIGNER_NYM_ID, THE_CONTRACT);
	// }
	//
	// public static String FlatSign(String SIGNER_NYM_ID, String THE_INPUT, String CONTRACT_TYPE) {
	// return otapiJNI.OTAPI_Basic_FlatSign(SIGNER_NYM_ID, THE_INPUT, CONTRACT_TYPE);
	// }
	//
	// public static String AddSignature(String SIGNER_NYM_ID, String THE_CONTRACT) {
	// return otapiJNI.OTAPI_Basic_AddSignature(SIGNER_NYM_ID, THE_CONTRACT);
	// }
	//
	// public static String VerifyAndRetrieveXMLContents(String THE_CONTRACT, String SIGNER_ID) {
	// return otapiJNI.OTAPI_Basic_VerifyAndRetrieveXMLContents(THE_CONTRACT, SIGNER_ID);
	// }
	//
	// public static int GetMemlogSize() {
	// return otapiJNI.OTAPI_Basic_GetMemlogSize();
	// }
	//
	// public static String GetMemlogAtIndex(int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetMemlogAtIndex(nIndex);
	// }
	//
	// public static String PeekMemlogFront() {
	// return otapiJNI.OTAPI_Basic_PeekMemlogFront();
	// }
	//
	// public static String PeekMemlogBack() {
	// return otapiJNI.OTAPI_Basic_PeekMemlogBack();
	// }
	//
	// public static boolean PopMemlogFront() {
	// return otapiJNI.OTAPI_Basic_PopMemlogFront();
	// }
	//
	// public static boolean PopMemlogBack() {
	// return otapiJNI.OTAPI_Basic_PopMemlogBack();
	// }
	//
	// public static String GetNym_SourceForID(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_SourceForID(NYM_ID);
	// }
	//
	// public static String GetNym_AltSourceLocation(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_AltSourceLocation(NYM_ID);
	// }
	//
	// public static int GetNym_CredentialCount(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_CredentialCount(NYM_ID);
	// }
	//
	// public static String GetNym_CredentialID(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_CredentialID(NYM_ID, nIndex);
	// }
	//
	// public static String GetNym_CredentialContents(String NYM_ID, String CREDENTIAL_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_CredentialContents(NYM_ID, CREDENTIAL_ID);
	// }
	//
	// public static int GetNym_RevokedCredCount(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_RevokedCredCount(NYM_ID);
	// }
	//
	// public static String GetNym_RevokedCredID(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_RevokedCredID(NYM_ID, nIndex);
	// }
	//
	// public static String GetNym_RevokedCredContents(String NYM_ID, String CREDENTIAL_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_RevokedCredContents(NYM_ID, CREDENTIAL_ID);
	// }
	//
	// public static int GetNym_SubcredentialCount(String NYM_ID, String MASTER_CRED_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_SubcredentialCount(NYM_ID, MASTER_CRED_ID);
	// }
	//
	// public static String GetNym_SubCredentialID(String NYM_ID, String MASTER_CRED_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_SubCredentialID(NYM_ID, MASTER_CRED_ID, nIndex);
	// }
	//
	// public static String GetNym_SubCredentialContents(String NYM_ID, String MASTER_CRED_ID, String SUB_CRED_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_SubCredentialContents(NYM_ID, MASTER_CRED_ID, SUB_CRED_ID);
	// }
	//
	// public static String AddSubcredential(String NYM_ID, String MASTER_CRED_ID, int nKeySize) {
	// return otapiJNI.OTAPI_Basic_AddSubcredential(NYM_ID, MASTER_CRED_ID, nKeySize);
	// }
	//
	// public static boolean RevokeSubcredential(String NYM_ID, String MASTER_CRED_ID, String SUB_CRED_ID) {
	// return otapiJNI.OTAPI_Basic_RevokeSubcredential(NYM_ID, MASTER_CRED_ID, SUB_CRED_ID);
	// }
	// public static String GetNym_Stats(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_Stats(NYM_ID);
	// }
	// public static int GetNym_MailCount(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_MailCount(NYM_ID);
	// }
	//
	// public static String GetNym_MailContentsByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_MailContentsByIndex(NYM_ID, nIndex);
	// }
	//
	// public static String GetNym_MailSenderIDByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_MailSenderIDByIndex(NYM_ID, nIndex);
	// }
	//
	// public static String GetNym_MailServerIDByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_MailServerIDByIndex(NYM_ID, nIndex);
	// }
	//
	// public static boolean Nym_RemoveMailByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Nym_RemoveMailByIndex(NYM_ID, nIndex);
	// }
	//
	// public static boolean Nym_VerifyMailByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Nym_VerifyMailByIndex(NYM_ID, nIndex);
	// }
	//
	// public static int GetNym_OutmailCount(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_GetNym_OutmailCount(NYM_ID);
	// }
	//
	// public static String GetNym_OutmailContentsByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_OutmailContentsByIndex(NYM_ID, nIndex);
	// }
	//
	// public static String GetNym_OutmailRecipientIDByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_OutmailRecipientIDByIndex(NYM_ID, nIndex);
	// }
	//
	// public static String GetNym_OutmailServerIDByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_GetNym_OutmailServerIDByIndex(NYM_ID, nIndex);
	// }
	//
	// public static boolean Nym_RemoveOutmailByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Nym_RemoveOutmailByIndex(NYM_ID, nIndex);
	// }
	//
	// public static boolean Nym_VerifyOutmailByIndex(String NYM_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Nym_VerifyOutmailByIndex(NYM_ID, nIndex);
	// }
	//
	//
	// public static boolean Wallet_ChangePassphrase() {
	// return otapiJNI.OTAPI_Basic_Wallet_ChangePassphrase();
	// }
	//
	//
	// public static String Wallet_ImportCert(String DISPLAY_NAME, String FILE_CONTENTS) {
	// return otapiJNI.OTAPI_Basic_Wallet_ImportCert(DISPLAY_NAME, FILE_CONTENTS);
	// }
	//
	// public static String Wallet_ExportCert(String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_Wallet_ExportCert(NYM_ID);
	// }
	//
	// public static String ProposePaymentPlan(String SERVER_ID, String VALID_FROM, String VALID_TO, String SENDER_ACCT_ID, String SENDER_USER_ID, String PLAN_CONSIDERATION, String RECIPIENT_ACCT_ID,
	// String RECIPIENT_USER_ID, String INITIAL_PAYMENT_AMOUNT, String INITIAL_PAYMENT_DELAY, String PAYMENT_PLAN_AMOUNT, String PAYMENT_PLAN_DELAY, String PAYMENT_PLAN_PERIOD, String
	// PAYMENT_PLAN_LENGTH, int PAYMENT_PLAN_MAX_PAYMENTS) {
	// return otapiJNI.OTAPI_Basic_ProposePaymentPlan(SERVER_ID, VALID_FROM, VALID_TO, SENDER_ACCT_ID, SENDER_USER_ID, PLAN_CONSIDERATION, RECIPIENT_ACCT_ID, RECIPIENT_USER_ID, INITIAL_PAYMENT_AMOUNT,
	// INITIAL_PAYMENT_DELAY, PAYMENT_PLAN_AMOUNT, PAYMENT_PLAN_DELAY, PAYMENT_PLAN_PERIOD, PAYMENT_PLAN_LENGTH, PAYMENT_PLAN_MAX_PAYMENTS);
	// }
	//
	// public static String EasyProposePlan(String SERVER_ID, String DATE_RANGE, String SENDER_ACCT_ID, String SENDER_USER_ID, String PLAN_CONSIDERATION, String RECIPIENT_ACCT_ID, String
	// RECIPIENT_USER_ID, String INITIAL_PAYMENT, String PAYMENT_PLAN, String PLAN_EXPIRY) {
	// return otapiJNI.OTAPI_Basic_EasyProposePlan(SERVER_ID, DATE_RANGE, SENDER_ACCT_ID, SENDER_USER_ID, PLAN_CONSIDERATION, RECIPIENT_ACCT_ID, RECIPIENT_USER_ID, INITIAL_PAYMENT, PAYMENT_PLAN,
	// PLAN_EXPIRY);
	// }
	//
	// public static String ConfirmPaymentPlan(String SERVER_ID, String SENDER_USER_ID, String SENDER_ACCT_ID, String RECIPIENT_USER_ID, String PAYMENT_PLAN) {
	// return otapiJNI.OTAPI_Basic_ConfirmPaymentPlan(SERVER_ID, SENDER_USER_ID, SENDER_ACCT_ID, RECIPIENT_USER_ID, PAYMENT_PLAN);
	// }
	//
	// public static String Create_SmartContract(String SIGNER_NYM_ID, String VALID_FROM, String VALID_TO) {
	// return otapiJNI.OTAPI_Basic_Create_SmartContract(SIGNER_NYM_ID, VALID_FROM, VALID_TO);
	// }
	//
	// public static String SmartContract_AddBylaw(String THE_CONTRACT, String SIGNER_NYM_ID, String BYLAW_NAME) {
	// return otapiJNI.OTAPI_Basic_SmartContract_AddBylaw(THE_CONTRACT, SIGNER_NYM_ID, BYLAW_NAME);
	// }
	//
	// public static String SmartContract_AddClause(String THE_CONTRACT, String SIGNER_NYM_ID, String BYLAW_NAME, String CLAUSE_NAME, String SOURCE_CODE) {
	// return otapiJNI.OTAPI_Basic_SmartContract_AddClause(THE_CONTRACT, SIGNER_NYM_ID, BYLAW_NAME, CLAUSE_NAME, SOURCE_CODE);
	// }
	//
	// public static String SmartContract_AddVariable(String THE_CONTRACT, String SIGNER_NYM_ID, String BYLAW_NAME, String VAR_NAME, String VAR_ACCESS, String VAR_TYPE, String VAR_VALUE) {
	// return otapiJNI.OTAPI_Basic_SmartContract_AddVariable(THE_CONTRACT, SIGNER_NYM_ID, BYLAW_NAME, VAR_NAME, VAR_ACCESS, VAR_TYPE, VAR_VALUE);
	// }
	//
	// public static String SmartContract_AddCallback(String THE_CONTRACT, String SIGNER_NYM_ID, String BYLAW_NAME, String CALLBACK_NAME, String CLAUSE_NAME) {
	// return otapiJNI.OTAPI_Basic_SmartContract_AddCallback(THE_CONTRACT, SIGNER_NYM_ID, BYLAW_NAME, CALLBACK_NAME, CLAUSE_NAME);
	// }
	//
	// public static String SmartContract_AddHook(String THE_CONTRACT, String SIGNER_NYM_ID, String BYLAW_NAME, String HOOK_NAME, String CLAUSE_NAME) {
	// return otapiJNI.OTAPI_Basic_SmartContract_AddHook(THE_CONTRACT, SIGNER_NYM_ID, BYLAW_NAME, HOOK_NAME, CLAUSE_NAME);
	// }
	//
	// public static String SmartContract_AddParty(String THE_CONTRACT, String SIGNER_NYM_ID, String PARTY_NAME, String AGENT_NAME) {
	// return otapiJNI.OTAPI_Basic_SmartContract_AddParty(THE_CONTRACT, SIGNER_NYM_ID, PARTY_NAME, AGENT_NAME);
	// }
	//
	// public static String SmartContract_AddAccount(String THE_CONTRACT, String SIGNER_NYM_ID, String PARTY_NAME, String ACCT_NAME, String ASSET_TYPE_ID) {
	// return otapiJNI.OTAPI_Basic_SmartContract_AddAccount(THE_CONTRACT, SIGNER_NYM_ID, PARTY_NAME, ACCT_NAME, ASSET_TYPE_ID);
	// }
	//
	// public static int SmartContract_CountNumsNeeded(String THE_CONTRACT, String AGENT_NAME) {
	// return otapiJNI.OTAPI_Basic_SmartContract_CountNumsNeeded(THE_CONTRACT, AGENT_NAME);
	// }
	//
	// public static String SmartContract_ConfirmAccount(String THE_CONTRACT, String SIGNER_NYM_ID, String PARTY_NAME, String ACCT_NAME, String AGENT_NAME, String ACCT_ID) {
	// return otapiJNI.OTAPI_Basic_SmartContract_ConfirmAccount(THE_CONTRACT, SIGNER_NYM_ID, PARTY_NAME, ACCT_NAME, AGENT_NAME, ACCT_ID);
	// }
	//
	// public static String SmartContract_ConfirmParty(String THE_CONTRACT, String PARTY_NAME, String NYM_ID) {
	// return otapiJNI.OTAPI_Basic_SmartContract_ConfirmParty(THE_CONTRACT, PARTY_NAME, NYM_ID);
	// }
	//
	// public static boolean Smart_AreAllPartiesConfirmed(String THE_CONTRACT) {
	// return otapiJNI.OTAPI_Basic_Smart_AreAllPartiesConfirmed(THE_CONTRACT);
	// }
	//
	// public static boolean Smart_IsPartyConfirmed(String THE_CONTRACT, String PARTY_NAME) {
	// return otapiJNI.OTAPI_Basic_Smart_IsPartyConfirmed(THE_CONTRACT, PARTY_NAME);
	// }
	//
	// public static int Smart_GetBylawCount(String THE_CONTRACT) {
	// return otapiJNI.OTAPI_Basic_Smart_GetBylawCount(THE_CONTRACT);
	// }
	//
	// public static String Smart_GetBylawByIndex(String THE_CONTRACT, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Smart_GetBylawByIndex(THE_CONTRACT, nIndex);
	// }
	//
	// public static String Bylaw_GetLanguage(String THE_CONTRACT, String BYLAW_NAME) {
	// return otapiJNI.OTAPI_Basic_Bylaw_GetLanguage(THE_CONTRACT, BYLAW_NAME);
	// }
	//
	// public static int Bylaw_GetClauseCount(String THE_CONTRACT, String BYLAW_NAME) {
	// return otapiJNI.OTAPI_Basic_Bylaw_GetClauseCount(THE_CONTRACT, BYLAW_NAME);
	// }
	//
	// public static String Clause_GetNameByIndex(String THE_CONTRACT, String BYLAW_NAME, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Clause_GetNameByIndex(THE_CONTRACT, BYLAW_NAME, nIndex);
	// }
	//
	// public static String Clause_GetContents(String THE_CONTRACT, String BYLAW_NAME, String CLAUSE_NAME) {
	// return otapiJNI.OTAPI_Basic_Clause_GetContents(THE_CONTRACT, BYLAW_NAME, CLAUSE_NAME);
	// }
	//
	// public static int Bylaw_GetVariableCount(String THE_CONTRACT, String BYLAW_NAME) {
	// return otapiJNI.OTAPI_Basic_Bylaw_GetVariableCount(THE_CONTRACT, BYLAW_NAME);
	// }
	//
	// public static String Variable_GetNameByIndex(String THE_CONTRACT, String BYLAW_NAME, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Variable_GetNameByIndex(THE_CONTRACT, BYLAW_NAME, nIndex);
	// }
	//
	// public static String Variable_GetType(String THE_CONTRACT, String BYLAW_NAME, String VARIABLE_NAME) {
	// return otapiJNI.OTAPI_Basic_Variable_GetType(THE_CONTRACT, BYLAW_NAME, VARIABLE_NAME);
	// }
	//
	// public static String Variable_GetAccess(String THE_CONTRACT, String BYLAW_NAME, String VARIABLE_NAME) {
	// return otapiJNI.OTAPI_Basic_Variable_GetAccess(THE_CONTRACT, BYLAW_NAME, VARIABLE_NAME);
	// }
	//
	// public static String Variable_GetContents(String THE_CONTRACT, String BYLAW_NAME, String VARIABLE_NAME) {
	// return otapiJNI.OTAPI_Basic_Variable_GetContents(THE_CONTRACT, BYLAW_NAME, VARIABLE_NAME);
	// }
	//
	// public static int Bylaw_GetHookCount(String THE_CONTRACT, String BYLAW_NAME) {
	// return otapiJNI.OTAPI_Basic_Bylaw_GetHookCount(THE_CONTRACT, BYLAW_NAME);
	// }
	//
	// public static String Hook_GetNameByIndex(String THE_CONTRACT, String BYLAW_NAME, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Hook_GetNameByIndex(THE_CONTRACT, BYLAW_NAME, nIndex);
	// }
	//
	// public static int Hook_GetClauseCount(String THE_CONTRACT, String BYLAW_NAME, String HOOK_NAME) {
	// return otapiJNI.OTAPI_Basic_Hook_GetClauseCount(THE_CONTRACT, BYLAW_NAME, HOOK_NAME);
	// }
	//
	// public static String Hook_GetClauseAtIndex(String THE_CONTRACT, String BYLAW_NAME, String HOOK_NAME, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Hook_GetClauseAtIndex(THE_CONTRACT, BYLAW_NAME, HOOK_NAME, nIndex);
	// }
	//
	// public static int Bylaw_GetCallbackCount(String THE_CONTRACT, String BYLAW_NAME) {
	// return otapiJNI.OTAPI_Basic_Bylaw_GetCallbackCount(THE_CONTRACT, BYLAW_NAME);
	// }
	//
	// public static String Callback_GetNameByIndex(String THE_CONTRACT, String BYLAW_NAME, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Callback_GetNameByIndex(THE_CONTRACT, BYLAW_NAME, nIndex);
	// }
	//
	// public static String Callback_GetClause(String THE_CONTRACT, String BYLAW_NAME, String CALLBACK_NAME) {
	// return otapiJNI.OTAPI_Basic_Callback_GetClause(THE_CONTRACT, BYLAW_NAME, CALLBACK_NAME);
	// }
	//
	// public static int Smart_GetPartyCount(String THE_CONTRACT) {
	// return otapiJNI.OTAPI_Basic_Smart_GetPartyCount(THE_CONTRACT);
	// }
	//
	// public static String Smart_GetPartyByIndex(String THE_CONTRACT, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Smart_GetPartyByIndex(THE_CONTRACT, nIndex);
	// }
	//
	// public static String Party_GetID(String THE_CONTRACT, String PARTY_NAME) {
	// return otapiJNI.OTAPI_Basic_Party_GetID(THE_CONTRACT, PARTY_NAME);
	// }
	//
	// public static int Party_GetAcctCount(String THE_CONTRACT, String PARTY_NAME) {
	// return otapiJNI.OTAPI_Basic_Party_GetAcctCount(THE_CONTRACT, PARTY_NAME);
	// }
	//
	// public static String Party_GetAcctNameByIndex(String THE_CONTRACT, String PARTY_NAME, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Party_GetAcctNameByIndex(THE_CONTRACT, PARTY_NAME, nIndex);
	// }
	//
	// public static String Party_GetAcctID(String THE_CONTRACT, String PARTY_NAME, String ACCT_NAME) {
	// return otapiJNI.OTAPI_Basic_Party_GetAcctID(THE_CONTRACT, PARTY_NAME, ACCT_NAME);
	// }
	//
	// public static String Party_GetAcctAssetID(String THE_CONTRACT, String PARTY_NAME, String ACCT_NAME) {
	// return otapiJNI.OTAPI_Basic_Party_GetAcctAssetID(THE_CONTRACT, PARTY_NAME, ACCT_NAME);
	// }
	//
	// public static String Party_GetAcctAgentName(String THE_CONTRACT, String PARTY_NAME, String ACCT_NAME) {
	// return otapiJNI.OTAPI_Basic_Party_GetAcctAgentName(THE_CONTRACT, PARTY_NAME, ACCT_NAME);
	// }
	//
	// public static int Party_GetAgentCount(String THE_CONTRACT, String PARTY_NAME) {
	// return otapiJNI.OTAPI_Basic_Party_GetAgentCount(THE_CONTRACT, PARTY_NAME);
	// }
	//
	// public static String Party_GetAgentNameByIndex(String THE_CONTRACT, String PARTY_NAME, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Party_GetAgentNameByIndex(THE_CONTRACT, PARTY_NAME, nIndex);
	// }
	//
	// public static String Party_GetAgentID(String THE_CONTRACT, String PARTY_NAME, String AGENT_NAME) {
	// return otapiJNI.OTAPI_Basic_Party_GetAgentID(THE_CONTRACT, PARTY_NAME, AGENT_NAME);
	// }
	//
	// public static int activateSmartContract(String SERVER_ID, String USER_ID, String THE_SMART_CONTRACT) {
	// return otapiJNI.OTAPI_Basic_activateSmartContract(SERVER_ID, USER_ID, THE_SMART_CONTRACT);
	// }
	//
	// public static int triggerClause(String SERVER_ID, String USER_ID, String TRANSACTION_NUMBER, String CLAUSE_NAME, String STR_PARAM) {
	// return otapiJNI.OTAPI_Basic_triggerClause(SERVER_ID, USER_ID, TRANSACTION_NUMBER, CLAUSE_NAME, STR_PARAM);
	// }
	//
	// public static boolean Msg_HarvestTransactionNumbers(String THE_MESSAGE, String USER_ID, boolean bHarvestingForRetry, boolean bReplyWasSuccess, boolean bReplyWasFailure, boolean
	// bTransactionWasSuccess, boolean bTransactionWasFailure) {
	// return otapiJNI.OTAPI_Basic_Msg_HarvestTransactionNumbers(THE_MESSAGE, USER_ID, bHarvestingForRetry, bReplyWasSuccess, bReplyWasFailure, bTransactionWasSuccess, bTransactionWasFailure);
	// }
	//
	//
	// public static String LoadUserPubkey_Signing(String USER_ID) {
	// return otapiJNI.OTAPI_Basic_LoadUserPubkey_Signing(USER_ID);
	// }
	//
	//
	// public static String LoadPubkey_Signing(String USER_ID) {
	// return otapiJNI.OTAPI_Basic_LoadPubkey_Signing(USER_ID);
	// }
	//
	// public static boolean VerifyUserPrivateKey(String USER_ID) {
	// return otapiJNI.OTAPI_Basic_VerifyUserPrivateKey(USER_ID);
	// }
	//
	// public static String LoadAssetContract(String ASSET_TYPE_ID) {
	// return otapiJNI.OTAPI_Basic_LoadAssetContract(ASSET_TYPE_ID);
	// }
	//
	// public static String LoadServerContract(String SERVER_ID) {
	// return otapiJNI.OTAPI_Basic_LoadServerContract(SERVER_ID);
	// }
	//
	// public static boolean IsBasketCurrency(String ASSET_TYPE_ID) {
	// return otapiJNI.OTAPI_Basic_IsBasketCurrency(ASSET_TYPE_ID);
	// }
	//
	// public static int Basket_GetMemberCount(String BASKET_ASSET_TYPE_ID) {
	// return otapiJNI.OTAPI_Basic_Basket_GetMemberCount(BASKET_ASSET_TYPE_ID);
	// }
	//
	// public static String Basket_GetMemberType(String BASKET_ASSET_TYPE_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Basket_GetMemberType(BASKET_ASSET_TYPE_ID, nIndex);
	// }
	//
	// public static String Basket_GetMinimumTransferAmount(String BASKET_ASSET_TYPE_ID) {
	// return otapiJNI.OTAPI_Basic_Basket_GetMinimumTransferAmount(BASKET_ASSET_TYPE_ID);
	// }
	//
	// public static String Basket_GetMemberMinimumTransferAmount(String BASKET_ASSET_TYPE_ID, int nIndex) {
	// return otapiJNI.OTAPI_Basic_Basket_GetMemberMinimumTransferAmount(BASKET_ASSET_TYPE_ID, nIndex);
	// }
	//
	// public static String LoadAssetAccount(String SERVER_ID, String USER_ID, String ACCOUNT_ID) {
	// return otapiJNI.OTAPI_Basic_LoadAssetAccount(SERVER_ID, USER_ID, ACCOUNT_ID);
	// }
	//
	// public static String Ledger_AddTransaction(String SERVER_ID, String USER_ID, String ACCOUNT_ID, String THE_LEDGER, String THE_TRANSACTION) {
	// return otapiJNI.OTAPI_Basic_Ledger_AddTransaction(SERVER_ID, USER_ID, ACCOUNT_ID, THE_LEDGER, THE_TRANSACTION);
	// }
	// public static int Transaction_GetSuccess(String SERVER_ID, String USER_ID, String ACCOUNT_ID, String THE_TRANSACTION) {
	// return otapiJNI.OTAPI_Basic_Transaction_GetSuccess(SERVER_ID, USER_ID, ACCOUNT_ID, THE_TRANSACTION);
	// }
	//
	// public static int Transaction_IsCanceled(String SERVER_ID, String USER_ID, String ACCOUNT_ID, String THE_TRANSACTION) {
	// return otapiJNI.OTAPI_Basic_Transaction_IsCanceled(SERVER_ID, USER_ID, ACCOUNT_ID, THE_TRANSACTION);
	// }
	//
	// public static int Transaction_GetBalanceAgreementSuccess(String SERVER_ID, String USER_ID, String ACCOUNT_ID, String THE_TRANSACTION) {
	// return otapiJNI.OTAPI_Basic_Transaction_GetBalanceAgreementSuccess(SERVER_ID, USER_ID, ACCOUNT_ID, THE_TRANSACTION);
	// }
	//
	//
	// public static String Purse_Empty(String SERVER_ID, String ASSET_TYPE_ID, String SIGNER_ID, String THE_PURSE) {
	// return otapiJNI.OTAPI_Basic_Purse_Empty(SERVER_ID, ASSET_TYPE_ID, SIGNER_ID, THE_PURSE);
	// }
	//
	// public static int exchangePurse(String SERVER_ID, String ASSET_TYPE_ID, String USER_ID, String THE_PURSE) {
	// return otapiJNI.OTAPI_Basic_exchangePurse(SERVER_ID, ASSET_TYPE_ID, USER_ID, THE_PURSE);
	// }
	//
	// public static String Token_GetAssetID(String THE_TOKEN) {
	// return otapiJNI.OTAPI_Basic_Token_GetAssetID(THE_TOKEN);
	// }
	//
	// public static String Token_GetServerID(String THE_TOKEN) {
	// return otapiJNI.OTAPI_Basic_Token_GetServerID(THE_TOKEN);
	// }
	//
	// public static int usageCredits(String SERVER_ID, String USER_ID, String USER_ID_CHECK, String ADJUSTMENT) {
	// return otapiJNI.OTAPI_Basic_usageCredits(SERVER_ID, USER_ID, USER_ID_CHECK, ADJUSTMENT);
	// }
	//
	// public static String Message_GetUsageCredits(String THE_MESSAGE) {
	// return otapiJNI.OTAPI_Basic_Message_GetUsageCredits(THE_MESSAGE);
	// }
	//
	// public static int sendUserMessage(String SERVER_ID, String USER_ID, String USER_ID_RECIPIENT, String RECIPIENT_PUBKEY, String THE_MESSAGE) {
	// return otapiJNI.OTAPI_Basic_sendUserMessage(SERVER_ID, USER_ID, USER_ID_RECIPIENT, RECIPIENT_PUBKEY, THE_MESSAGE);
	// }
	// public static int getContract(String SERVER_ID, String USER_ID, String ASSET_ID) {
	// return otapiJNI.OTAPI_Basic_getContract(SERVER_ID, USER_ID, ASSET_ID);
	// }
	//
	// public static String GenerateBasketCreation(String USER_ID, String MINIMUM_TRANSFER) {
	// return otapiJNI.OTAPI_Basic_GenerateBasketCreation(USER_ID, MINIMUM_TRANSFER);
	// }
	//
	// public static String AddBasketCreationItem(String USER_ID, String THE_BASKET, String ASSET_TYPE_ID, String MINIMUM_TRANSFER) {
	// return otapiJNI.OTAPI_Basic_AddBasketCreationItem(USER_ID, THE_BASKET, ASSET_TYPE_ID, MINIMUM_TRANSFER);
	// }
	//
	// public static int issueBasket(String SERVER_ID, String USER_ID, String THE_BASKET) {
	// return otapiJNI.OTAPI_Basic_issueBasket(SERVER_ID, USER_ID, THE_BASKET);
	// }
	//
	// public static String GenerateBasketExchange(String SERVER_ID, String USER_ID, String BASKET_ASSET_TYPE_ID, String BASKET_ASSET_ACCT_ID, int TRANSFER_MULTIPLE) {
	// return otapiJNI.OTAPI_Basic_GenerateBasketExchange(SERVER_ID, USER_ID, BASKET_ASSET_TYPE_ID, BASKET_ASSET_ACCT_ID, TRANSFER_MULTIPLE);
	// }
	//
	// public static String AddBasketExchangeItem(String SERVER_ID, String USER_ID, String THE_BASKET, String ASSET_TYPE_ID, String ASSET_ACCT_ID) {
	// return otapiJNI.OTAPI_Basic_AddBasketExchangeItem(SERVER_ID, USER_ID, THE_BASKET, ASSET_TYPE_ID, ASSET_ACCT_ID);
	// }
	//
	// public static int exchangeBasket(String SERVER_ID, String USER_ID, String BASKET_ASSET_ID, String THE_BASKET, boolean BOOL_EXCHANGE_IN_OR_OUT) {
	// return otapiJNI.OTAPI_Basic_exchangeBasket(SERVER_ID, USER_ID, BASKET_ASSET_ID, THE_BASKET, BOOL_EXCHANGE_IN_OR_OUT);
	// }
	//
	// public static String Nymbox_GetReplyNotice(String SERVER_ID, String USER_ID, String REQUEST_NUMBER) {
	// return otapiJNI.OTAPI_Basic_Nymbox_GetReplyNotice(SERVER_ID, USER_ID, REQUEST_NUMBER);
	// }
	//
	// public static int payDividend(String SERVER_ID, String ISSUER_USER_ID, String DIVIDEND_FROM_ACCT_ID, String SHARES_ASSET_TYPE_ID, String DIVIDEND_MEMO, String AMOUNT_PER_SHARE) {
	// return otapiJNI.OTAPI_Basic_payDividend(SERVER_ID, ISSUER_USER_ID, DIVIDEND_FROM_ACCT_ID, SHARES_ASSET_TYPE_ID, DIVIDEND_MEMO, AMOUNT_PER_SHARE);
	// }
	// public static int depositPaymentPlan(String SERVER_ID, String USER_ID, String THE_PAYMENT_PLAN) {
	// return otapiJNI.OTAPI_Basic_depositPaymentPlan(SERVER_ID, USER_ID, THE_PAYMENT_PLAN);
	// }
	//
	// public static int issueMarketOffer(String SERVER_ID, String USER_ID, String ASSET_ACCT_ID, String CURRENCY_ACCT_ID, String MARKET_SCALE, String MINIMUM_INCREMENT, String TOTAL_ASSETS_ON_OFFER,
	// String PRICE_LIMIT, boolean bBuyingOrSelling, String LIFESPAN_IN_SECONDS) {
	// return otapiJNI.OTAPI_Basic_issueMarketOffer(SERVER_ID, USER_ID, ASSET_ACCT_ID, CURRENCY_ACCT_ID, MARKET_SCALE, MINIMUM_INCREMENT, TOTAL_ASSETS_ON_OFFER, PRICE_LIMIT, bBuyingOrSelling,
	// LIFESPAN_IN_SECONDS);
	// }
	//
	// public static int getMarketList(String SERVER_ID, String USER_ID) {
	// return otapiJNI.OTAPI_Basic_getMarketList(SERVER_ID, USER_ID);
	// }
	//
	// public static int getMarketOffers(String SERVER_ID, String USER_ID, String MARKET_ID, String MAX_DEPTH) {
	// return otapiJNI.OTAPI_Basic_getMarketOffers(SERVER_ID, USER_ID, MARKET_ID, MAX_DEPTH);
	// }
	//
	// public static int getMarketRecentTrades(String SERVER_ID, String USER_ID, String MARKET_ID) {
	// return otapiJNI.OTAPI_Basic_getMarketRecentTrades(SERVER_ID, USER_ID, MARKET_ID);
	// }
	//
	// public static int getNym_MarketOffers(String SERVER_ID, String USER_ID) {
	// return otapiJNI.OTAPI_Basic_getNym_MarketOffers(SERVER_ID, USER_ID);
	// }
	//
	// public static int killMarketOffer(String SERVER_ID, String USER_ID, String ASSET_ACCT_ID, String TRANSACTION_NUMBER) {
	// return otapiJNI.OTAPI_Basic_killMarketOffer(SERVER_ID, USER_ID, ASSET_ACCT_ID, TRANSACTION_NUMBER);
	// }
	//
	// public static int killPaymentPlan(String SERVER_ID, String USER_ID, String FROM_ACCT_ID, String TRANSACTION_NUMBER) {
	// return otapiJNI.OTAPI_Basic_killPaymentPlan(SERVER_ID, USER_ID, FROM_ACCT_ID, TRANSACTION_NUMBER);
	// }
	//
	// public static String GetSentMessage(String REQUEST_NUMBER, String SERVER_ID, String USER_ID) {
	// return otapiJNI.OTAPI_Basic_GetSentMessage(REQUEST_NUMBER, SERVER_ID, USER_ID);
	// }
	//
	// public static boolean RemoveSentMessage(String REQUEST_NUMBER, String SERVER_ID, String USER_ID) {
	// return otapiJNI.OTAPI_Basic_RemoveSentMessage(REQUEST_NUMBER, SERVER_ID, USER_ID);
	// }
	//
	// public static void FlushSentMessages(boolean bHarvestingForRetry, String SERVER_ID, String USER_ID, String THE_NYMBOX) {
	// otapiJNI.OTAPI_Basic_FlushSentMessages(bHarvestingForRetry, SERVER_ID, USER_ID, THE_NYMBOX);
	// }
	//
	// public static void Sleep(String MILLISECONDS) {
	// otapiJNI.OTAPI_Basic_Sleep(MILLISECONDS);
	// }
	//
	// public static String Message_GetCommand(String THE_MESSAGE) {
	// return otapiJNI.OTAPI_Basic_Message_GetCommand(THE_MESSAGE);
	// }
	//
	// public static int Message_GetDepth(String THE_MESSAGE) {
	// return otapiJNI.OTAPI_Basic_Message_GetDepth(THE_MESSAGE);
	// }
	//
	// public static int Message_IsTransactionCanceled(String SERVER_ID, String USER_ID, String ACCOUNT_ID, String THE_MESSAGE) {
	// return otapiJNI.OTAPI_Basic_Message_IsTransactionCanceled(SERVER_ID, USER_ID, ACCOUNT_ID, THE_MESSAGE);
	// }
	//
	// public static String Message_GetNymboxHash(String THE_MESSAGE) {
	// return otapiJNI.OTAPI_Basic_Message_GetNymboxHash(THE_MESSAGE);
	// }
	//
	// public static boolean ConnectServer(String SERVER_ID, String USER_ID, String strCA_FILE, String strKEY_FILE, String strKEY_PASSWORD) {
	// return otapiJNI.OTAPI_Basic_ConnectServer(SERVER_ID, USER_ID, strCA_FILE, strKEY_FILE, strKEY_PASSWORD);
	// }
	//
	// public static boolean ProcessSockets() {
	// return otapiJNI.OTAPI_Basic_ProcessSockets();
	// }

}
