package eu.opentxs.bridge.core.modules.act;

import java.util.List;

import eu.ApplProperties;
import eu.opentxs.bridge.Text;
import eu.opentxs.bridge.Util;
import eu.opentxs.bridge.core.dto.Transaction;
import eu.opentxs.bridge.core.dto.Transaction.TransactionType;
import eu.opentxs.bridge.core.modules.Module;
import eu.opentxs.bridge.core.modules.OTAPI;

public class NymModule extends ServerModule {
	
	protected String nymId;
	
    public NymModule(String serverId, String nymId) throws Exception {
		super(serverId);
		this.nymId = parseNymId(nymId);
	}
    
    public void pingServer() throws Exception {
    	attempt("Pinging server");
    	sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.pingServer(serverId, nymId);
			}
		});
    	success("Ping is done");
    }

	public static String createNym() throws Exception {
    	attempt("Creating new nym");
        String nymId = OTAPI.createNym(
        		ApplProperties.get().getInteger("encoding.keySize"), 
        		new String(""), new String(""));
        if (!Util.isValidString(nymId))
        	error("Failed to create nym");
        success("Nym successfully created");
        return nymId;
    }
    
    public static void renameNym(String nymId, String nymName) throws Exception {
    	attempt("Renaming nym");
    	nymId = parseNymId(nymId);
        if (!OTAPI.setNymName(nymId, nymName))
        	error("Failed to rename");
        success("Nym is renamed");
        showNym(nymId);
    }
    
    public static boolean isNymRegisteredAtServer(String serverId, String nymId) {
    	return OTAPI.isNymRegisteredAtServer(serverId, nymId);
    }
    
    public void registerNymAtServer() throws Exception {
    	attempt("Registering nym at server");
    	if (isNymRegisteredAtServer(serverId, nymId)) {
    		print("Already registered");
    		return;
    	}
    	sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.registerNymAtServer(serverId, nymId);
			}
		});
    	synchronizeRequestNumber();
		if (!isNymRegisteredAtServer(serverId, nymId))
			error("Managed to register but failed to confirm");
		success("Nym is registered");
    }
    
    public void resyncNymWithServer() throws Exception {
    	attempt("Resyncing nym with server");
    	String message = sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.registerNymAtServer(serverId, nymId);
			}
		});
    	if (!OTAPI.resyncNymWithServer(serverId, nymId, message))
    		error("reply is false");
    	success("Resyncing done");
    }
    
    public void removeNymFromServer() throws Exception {
    	attempt("Removing nym from server");
    	if (!isNymRegisteredAtServer(serverId, nymId)) {
    		print("Already removed. Nym is not registered at this server.");
    		return;
    	}
    	sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.removeNymFromServer(serverId, nymId);
			}
		});
    	success("Nym is removed");
    }
    
    public static String isNymRegisteredAtAnyServer(String nymId) throws Exception {
    	nymId = parseNymId(nymId);
    	List<String> serverIds = getServerIds();
    	for (String serverId : serverIds) {
    		if (isNymRegisteredAtServer(serverId, nymId))
    			return serverId;
    	}
    	return null;
    }
    
    public static void deleteNym(String nymId) throws Exception {
    	attempt("Deleting nym");
    	nymId = parseNymId(nymId);
    	String serverId = isNymRegisteredAtAnyServer(nymId);
		if (serverId != null)
			error(String.format("%s %s", 
					"This nym is still registered at server",
					getServerName(serverId)));
    	if (!OTAPI.deleteNym(nymId))
    		error("Failed to delete nym");
    	success("Nym is deleted");
    }
    
    public static String exportNym(String nymId) throws Exception {
    	attempt("Exporting nym");
    	nymId = parseNymId(nymId);
    	String contract = OTAPI.Wallet.exportNym(nymId);
    	if (!Util.isValidString(contract))
    		error("Failed to export nym");
    	success("Nym is exported");
    	publish(contract);
    	return contract;
    }
    
    public static String importNym(String contract) throws Exception {
    	attempt("Importing nym");
    	if (!Util.isValidString(contract))
    		error("content is empty");
    	String nymId = OTAPI.Wallet.importNym(contract);
    	if (!Util.isValidString(nymId))
    		error("Failed to import nym");
    	success("Nym is imported");
    	showNym(nymId);
    	return nymId;
    }
    
    public static void showNymAccounts(String nymId) throws Exception {
    	nymId = parseNymId(nymId);
    	print(String.format("%12s:", "ACCOUNTS"));
        int accountCount = OTAPI.getAccountCount();
        for (int index = 0; index < accountCount; index++) {
        	String accountId = OTAPI.getAccountId(index);
        	if (getAccountNymId(accountId).equals(nymId))
        		showLedger(accountId);
        }
    }
    
    public String issueAsset(final String contract, String accountName) throws Exception {
    	attempt("Issuing asset");
    	
    	/**
    	 * how to check if an asset is already issued on the server
		 * what is encodedMap?
    	 */
//    	String message = sendRequest(new RequestGenerator() {
//			@Override
//			public int getRequest() {
//				return OTAPI.queryAssetTypes(serverId, nymId, encodedMap);
//			}
//		});
    	
    	String message = sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.issueAsset(serverId, nymId, contract);
			}
		});
    	String accountId = OTAPI.getNewIssuerAccountId(message);
    	if (!Util.isValidString(accountId))
    		error("Account created but failed to retreive its id");
    	if (!Util.isValidString(accountName))
    		accountName = getAccountStandardName(accountId);
    	AccountModule.renameAccount(accountId, accountName);
    	success("Asset is issued");
    	return accountId;
    }
    
    public void showNymbox() throws Exception {
		String ledger = OTAPI.loadNymbox(serverId, nymId);
		if (!Util.isValidString(ledger)) {
			print("Nymbox is empty");
			return;
		}
		int size = OTAPI.Ledger.getCount(serverId, nymId, nymId, ledger);
		if (size <= 0) {
			if (size < 0)
				warn("Nymbox size is abnormal", size);
			else
				print("Nymbox size is zero");
			return;
		}
		List<Transaction> list = Transaction.getListForNym(serverId, nymId, null, ledger, size);
		Transaction.showV(list);
	}
    
    public void sendCash(String cash, String hisNymId) throws Exception {
		attempt("Sending cash");
		if (!Util.isValidString(cash))
			error("cash is empty");
		if (!Util.isValidString(hisNymId))
			error("hisNymId is empty");
		sendPayment(cash, hisNymId);
		success("Cash is sent");
	}
    
    public void discardCheque(String cheque) throws Exception {
		attempt("Discarding cheque");
		String ledger = OTAPI.loadPayInbox(serverId, nymId);
		if (!Util.isValidString(ledger))
			error("PayInbox is empty");
		int size = OTAPI.Ledger.getCount(serverId, nymId, nymId, ledger);
		if (size <= 0)
			error("PayInbox size is zero or negative");
		String trxnNum = OTAPI.Instrument.getTransactionNum(cheque);
		for (int index = 0; index < size; index++) {
			String instrument = OTAPI.Ledger.getInstrumentbyIndex(
					serverId, nymId, nymId, ledger, index);
			if (!Util.isValidString(instrument))
				error("instrument is empty");
			if (trxnNum.equals(OTAPI.Instrument.getTransactionNum(instrument))) {
				OTAPI.recordPayment(serverId, nymId, true, index, false);
				success("Cheque is discarded");
				return;
			}
		}
		error("Cheque not found");
	}
    
    public void discardInvoice(String invoice) throws Exception {
		attempt("Discarding invoice");
		String ledger = OTAPI.loadPayInbox(serverId, nymId);
		if (!Util.isValidString(ledger))
			error("PayInbox is empty");
		int size = OTAPI.Ledger.getCount(serverId, nymId, nymId, ledger);
		if (size <= 0)
			error("PayInbox size is zero or abnormal");
		String trxnNum = OTAPI.Instrument.getTransactionNum(invoice);
		for (int index = 0; index < size; index++) {
			String instrument = OTAPI.Ledger.getInstrumentbyIndex(
					serverId, nymId, nymId, ledger, index);
			if (!Util.isValidString(instrument))
				error("instrument is empty");
			if (trxnNum.equals(OTAPI.Instrument.getTransactionNum(instrument))) {
				OTAPI.recordPayment(serverId, nymId, true, index, false);
				success("Invoice is discarded");
				return;
			}
		}
		error("Invoice not found");
	}
    
    
    /**********************************************************************
     * internal
     *********************************************************************/
    
	protected void sendPayment(final String payment, final String hisNymId) throws Exception {
		attempt("Sending payment");
		final String hisPublicKey = loadPublicEncryptionKey(hisNymId);
		if (!Util.isValidString(hisPublicKey))
			error("unable to load public encryption key for the recipient");
		sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.sendUserPayment(serverId, nymId, hisNymId, hisPublicKey, payment);
			}
		});
	}
    
    protected void removeOutpayment(String trxnNum) throws Exception {
    	int size = OTAPI.GetNym.outpaymentsCount(nymId);
		if (size <= 0)
			error("Outpayments size is zero or abnormal");
		for (int index = 0; index < size; index++) {
			String instrument = OTAPI.GetNym.outpaymentsContentsByIndex(nymId, index);
			if (!Util.isValidString(instrument))
				Module.error("instrument is empty");
			if (trxnNum.equals(OTAPI.Instrument.getTransactionNum(instrument))) {
				OTAPI.Nym.removeOutpaymentsByIndex(nymId, index);
				success("Outpayment is removed");
				return;
			}
		}
		error("Outpayment not found");
	}
    
    protected String sendRequest(RequestGenerator generator) throws Exception {
    	return sendRequest(generator, false);
    }
	
    protected String sendRequest(RequestGenerator generator, boolean forceContinue) throws Exception {
    	OTAPI.flushMessageBuffer();
    	int requestId = generator.getRequest();
    	return processRequest(requestId, forceContinue);
    }
    
    protected String processRequest(int requestId, boolean forceContinue) throws Exception {
    	{
	    	attempt(Text.VERIFYING_REQUEST);
	    	if (requestId == 0) {
	    		warn(Text.REQUEST_ID_ZERO, requestId);
	    		return null;
	    	}
	    	if (requestId < 0)
	    		error(Text.REQUEST_ID_NEGATIVE, requestId);
    	}
    	delay();
    	String message;
    	{
    		attempt(Text.RECEIVING_MESSAGE);
	        message = OTAPI.popMessageBuffer(
	        		Integer.toString(requestId), serverId, nymId);
	        if (!Util.isValidString(message))
	        	error(Text.MESSAGE_IS_INVALID);
	        if (verboseServer)
	        	print(message);
    	}
    	{
    		attempt(Text.VERIFYING_MESSAGE);
            int result = OTAPI.verifyMessage(message);
            if (result == 0) {
            	if (forceContinue) {
	            	warn(Text.MESSAGE_VERIFICATION_FAILED, result);
	            	return null;
            	}
            	error(Text.MESSAGE_VERIFICATION_ERROR, result);
            }
            if (result != 1)
            	error(Text.MESSAGE_VERIFICATION_ERROR, result);
    	}
        return message;
    }
    
    protected void synchronizeRequestNumber() throws Exception {
    	attempt("Synchronizing request number");
    	sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.synchronizeRequestNumber(serverId, nymId);
			}
		});
    	success("Request number is synchronized");
    }
    
    protected void getAndProcessNymbox(boolean forceDownload) throws Exception {
		{
			attempt("Verifying nymbox hash");
			String cachedHash = OTAPI.getNymboxHashCached(serverId, nymId);
			if (!Util.isValidString(cachedHash))
				warn("Unable to retrieve cached copy of server-side nymbox hash");
			String localHash = OTAPI.getNymboxHashLocal(serverId, nymId);
			if (!Util.isValidString(localHash))
				warn("Unable to retrieve client-side nymbox hash");
			if (!forceDownload && cachedHash.equals(localHash)) {
				skip("The nymbox hashes already match, skipping nymbox download");
			} else {
				attempt("Downloading nymbox");
				sendRequest(new RequestGenerator() {
					@Override
					public int getRequest() {
						return OTAPI.getNymbox(serverId, nymId);
					}
				});
				insureHaveAllBoxReceipts(OTAPI.Box.NYMBOX, nymId);
			}
		}
		{
			attempt("Processing nymbox");
			sendRequest(new RequestGenerator() {
				@Override
				public int getRequest() {
					return OTAPI.processNymbox(serverId, nymId);
				}
			});
		}
	}
    
    protected void insureHaveAllBoxReceipts(final OTAPI.Box box, final String accountId) throws Exception {
		attempt(String.format("%s %s", "Insuring box receipts for", box.name()));
		String ledger = null;
		if (box.equals(OTAPI.Box.INBOX))
			ledger = OTAPI.loadInboxNoVerify(serverId, nymId, accountId);
		else if (box.equals(OTAPI.Box.OUTBOX))
			ledger = OTAPI.loadOutboxNoVerify(serverId, nymId, accountId);
		else if (box.equals(OTAPI.Box.NYMBOX))
			ledger = OTAPI.loadNymboxNoVerify(serverId, nymId);

		if (!Util.isValidString(ledger))
			error("ledger is not valid");
		if (!OTAPI.verifySignature(nymId, ledger))
			error("failed to verify signature on ledger contract");

		int receiptCount = OTAPI.Ledger.getCount(serverId, nymId, accountId, ledger);
		for (int index = 0; index < receiptCount; index++) {
			final String transactionId = OTAPI.Ledger.getTransactionIdByIndex(
					serverId, nymId, accountId, ledger, index);
			if (!Util.isValidString(transactionId))
				continue;
			String transaction = OTAPI.Ledger.getTransactionById(serverId,
					nymId, accountId, ledger, transactionId);
			if (!Util.isValidString(transaction))
				error("transaction is not valid");
			String transactionType = OTAPI.Transaction.getType(serverId, nymId,
					accountId, transaction);
			if (!Util.isValidString(transactionType))
				error("transaction type is not valid");
			boolean isReplyNotice = transactionType
					.equals(TransactionType.REPLY_NOTICE.getValue());
			boolean downloadNeeded = true;
			if (isReplyNotice) {
				String requestId = OTAPI.ReplyNotice.getRequestId(serverId,
						nymId, transaction);
				if (!Util.isValidString(requestId))
					error("reply notice request id is not valid");
				downloadNeeded = !OTAPI.haveAlreadySeenReply(serverId, nymId,
						requestId);
			}
			if (downloadNeeded) {
				boolean haveBoxReceipt = OTAPI.doesBoxReceiptExist(serverId,
						nymId, accountId, box, transactionId);
				if (!haveBoxReceipt) {
					attempt("Downloading box receipt");
					sendRequest(new RequestGenerator() {
						@Override
						public int getRequest() {
							return OTAPI.getBoxReceipt(serverId, nymId,
									accountId, box, transactionId);
						}
					});
				}
			}
		}
	}
    
    protected void getTransactionNumbers() throws Exception {
		getTransactionNumbers(true);
	}

	private void getTransactionNumbers(boolean canRetry) throws Exception {
		int count = getTransactionNumbersCount();
		if (isEnoughTransactionNumbers(count)) {
			skip(String.format("The nym still has %d transaction numbers left",
					count));
			return;
		}
		attempt("Fetching transaction numbers");
		synchronizeRequestNumber();
		getAndProcessNymbox(true);
		sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.getTransactionNumbers(serverId, nymId);
			}
		}, true);
		count = OTAPI.getTransactionNumbersCount(serverId, nymId);
		if (!isEnoughTransactionNumbers(count)) {
			if (canRetry) {
				warn("Contingency plan");
				synchronizeRequestNumber();
				getAndProcessNymbox(true);
				warn("Retrying getTransactionNumbers() after contingency plan");
				getTransactionNumbers(false);
				return;
			} else {
				warn("One more time");
				synchronizeRequestNumber();
				getAndProcessNymbox(true);
				sendRequest(new RequestGenerator() {
					@Override
					public int getRequest() {
						return OTAPI.getTransactionNumbers(serverId, nymId);
					}
				}, true);
				count = getTransactionNumbersCount();
				if (!isEnoughTransactionNumbers(count))
					error("I give up");
			}
		}
		log(String.format("Now the nym has %d transaction numbers", count));
	}

	private int getTransactionNumbersCount() {
		return OTAPI.getTransactionNumbersCount(serverId, nymId);
	}

	private static boolean isEnoughTransactionNumbers(int count) {
		int limit = ApplProperties.get().getInteger("transactionNumber.limit");
		return (count >= limit);
	}
	
	private String loadPublicEncryptionKey(final String hisNymId) throws Exception {
		attempt("Loading public encryption key");
		String key = getPublicEncryptionKey(hisNymId);
		if (!Util.isValidString(key)) {
			sendRequest(new RequestGenerator() {
				@Override
				public int getRequest() {
					return OTAPI.checkUser(serverId, nymId, hisNymId);
				}
			});
			key = getPublicEncryptionKey(hisNymId);
		}
		return key;//might be null
	}
	
	private static String getPublicEncryptionKey(String hisNymId) throws Exception {
		String key = OTAPI.loadPublicKeyEncryption(hisNymId);
		if (!Util.isValidString(key)) {
			key = OTAPI.loadUserPublicKeyEncryption(hisNymId);
			/**
			 * patch to fix bug in OTAPI_Basic_LoadUserPubkey_Encryption
			 */
			///
			if (Util.isValidString(key))
				key = key.replaceAll("- -----", "-----");
			///
		}
		return key;//might be null
	}
    
    private static void delay() {
    	long millis = ApplProperties.get().getLong("processRequest.delay");
        try {
            if (millis > 0)
                Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
