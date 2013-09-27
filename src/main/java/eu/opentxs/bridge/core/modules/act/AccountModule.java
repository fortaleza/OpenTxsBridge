package eu.opentxs.bridge.core.modules.act;

import java.util.ArrayList;
import java.util.List;

import eu.opentxs.bridge.Text;
import eu.opentxs.bridge.UTC;
import eu.opentxs.bridge.Util;
import eu.opentxs.bridge.core.dto.Transaction;
import eu.opentxs.bridge.core.dto.Transaction.InstrumentType;
import eu.opentxs.bridge.core.modules.OTAPI;

public class AccountModule extends NymModule {

	protected String accountId;

	public AccountModule(String serverId, String nymId, String accountId) throws Exception {
		super(serverId, nymId);
		this.accountId = parseAccountId(accountId);
	}

	public static String accountAlreadyExists(String serverId, String nymId, String assetId) {
		List<String> accountIds = getAccountIds();
		for (String accountId : accountIds) {
			AccountType accountType = AccountType.parse(getAccountType(accountId));
			if (accountType.equals(AccountType.ISSUER))
				continue;
			if (getAccountServerId(accountId).equals(serverId) && getAccountNymId(accountId).equals(nymId) && getAccountAssetId(accountId).equals(assetId))
				return accountId;
		}
		return null;
	}

	public static void renameAccount(String accountId, String accountName) throws Exception {
		attempt("Renaming account");
		accountId = parseAccountId(accountId);
		String accountNymId = getAccountNymId(accountId);
		if (!Util.isValidString(accountName)) {
			accountName = getAccountStandardName(accountId);
			print(accountName);
		}
		if (!OTAPI.setAccountName(accountNymId, accountId, accountName))
			error("Failed to rename");
		success("Account is renamed");
	}

	public static void deleteAccount(String accountId) throws Exception {
		attempt("Deleting account");
		accountId = parseAccountId(accountId);
		String serverId = getAccountServerId(accountId);
		String nymId = getAccountNymId(accountId);
		new AccountModule(serverId, nymId, accountId).deleteItself();
		success("Account is deleted");
	}

	private void deleteItself() throws Exception {
		refresh();
		sendRequest(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.deleteAccountFromServer(serverId, nymId, accountId);
			}
		});
	}

	public void showInbox() throws Exception {
		String ledger = OTAPI.loadInbox(serverId, nymId, accountId);
		if (!Util.isValidString(ledger)) {
			print("Inbox is empty");
			return;
		}
		int size = OTAPI.Ledger.getCount(serverId, nymId, accountId, ledger);
		if (size <= 0) {
			if (size < 0)
				warn("Inbox size is abnormal", size);
			else
				print("Inbox size is zero");
			return;
		}
		List<Transaction> list = Transaction.getListForAccount(serverId, nymId, accountId, ledger, size);
		Transaction.showV(list);
	}

	public void showOutbox() throws Exception {
		String ledger = OTAPI.loadOutbox(serverId, nymId, accountId);
		if (!Util.isValidString(ledger)) {
			print("Outbox is empty");
			return;
		}
		int size = OTAPI.Ledger.getCount(serverId, nymId, accountId, ledger);
		if (size <= 0) {
			if (size < 0)
				warn("Outbox size is abnormal", size);
			else
				print("Outbox size is zero");
			return;
		}
		List<Transaction> list = Transaction.getListForAccount(serverId, nymId, accountId, ledger, size);
		Transaction.showV(list);
	}

	public void showTransactions() throws Exception {
		print(Util.repeat("-", 70));
		{
			print(String.format("%12s:", "UNREALIZED"));
			print(Util.repeat("-", 13));
			List<Transaction> list = getTransactionsUnrealized();
			if (list.size() == 0)
				print("There are no unrealized transactions");
			else
				Transaction.showH(list);
		}
		print(Util.repeat("-", 70));
		{
			print(String.format("%12s:", "REALIZED"));
			print(Util.repeat("-", 13));
			List<Transaction> list = getTransactionsRealized();
			if (list.size() == 0)
				print("There are no realized transactions");
			else
				Transaction.showH(list);
		}
		print(Util.repeat("-", 70));
	}

	public List<Transaction> getTransactionsUnrealized() throws Exception {
		List<Transaction> list = new ArrayList<Transaction>();
		{
			String ledger = OTAPI.loadPayInbox(serverId, nymId);
			if (Util.isValidString(ledger)) {
				int size = OTAPI.Ledger.getCount(serverId, nymId, nymId, ledger);
				if (size > 0) {
					list.addAll(Transaction.getListForNym(serverId, nymId, getAccountAssetId(accountId), ledger, size));
				} else if (size < 0) {
					warn("Recordbox for nym size is abnormal", size);
				}
			}
		}
		{
			int size = OTAPI.GetNym.outpaymentsCount(nymId);
			if (size > 0) {
				list.addAll(Transaction.getListOfOutpayments(serverId, nymId, getAccountAssetId(accountId), size, new InstrumentType[]{InstrumentType.CHEQUE, InstrumentType.INVOICE}));
			} else if (size < 0) {
				warn("Outpayments size is abnormal", size);
			}
		}
		return list;
	}

	public List<Transaction> getTransactionsRealized() throws Exception {
		List<Transaction> list = new ArrayList<Transaction>();
		{
			String ledger = OTAPI.loadRecordbox(serverId, nymId, nymId);
			if (Util.isValidString(ledger)) {
				int size = OTAPI.Ledger.getCount(serverId, nymId, nymId, ledger);
				if (size > 0) {
					list.addAll(Transaction.getListForNym(serverId, nymId, getAccountAssetId(accountId), ledger, size));
				} else if (size < 0) {
					warn("Recordbox for nym size is abnormal", size);
				}
			}
		}
		{
			String ledger = OTAPI.loadRecordbox(serverId, nymId, accountId);
			if (Util.isValidString(ledger)) {
				int size = OTAPI.Ledger.getCount(serverId, nymId, accountId, ledger);
				if (size > 0) {
					list.addAll(Transaction.getListForAccount(serverId, nymId, accountId, ledger, size));
				} else if (size < 0) {
					warn("Recordbox for account size is abnormal", size);
				}
			}
		}
		{
			String ledger = OTAPI.loadOutbox(serverId, nymId, accountId);
			if (Util.isValidString(ledger)) {
				int size = OTAPI.Ledger.getCount(serverId, nymId, accountId, ledger);
				if (size > 0) {
					list.addAll(Transaction.getListForOutbox(serverId, nymId, accountId, ledger, size));
				} else if (size < 0) {
					warn("Outbox size is abnormal", size);
				}
			}
		}
		{
			int size = OTAPI.GetNym.outpaymentsCount(nymId);
			if (size > 0) {
				list.addAll(Transaction.getListOfOutpayments(serverId, nymId, getAccountAssetId(accountId), size, new InstrumentType[]{InstrumentType.CASH, InstrumentType.VOUCHER}));
			} else if (size < 0) {
				warn("Outpayments size is abnormal", size);
			}
		}
		return list;
	}

	public void refresh() throws Exception {
		downloadFiles();
		processInbox();
		processIncome();
		verifyLastReceipt();
	}

	public void downloadFiles() throws Exception {
		attempt("Downloading files");
		getIntermediaryFiles(true);
		success("Files are downloaded");
	}

	public void processInbox() throws Exception {
		attempt("Processing inbox");
		getIntermediaryFiles(false);

		String ledger = OTAPI.loadInbox(serverId, nymId, accountId);
		int size = OTAPI.Ledger.getCount(serverId, nymId, accountId, ledger);
		if (size == 0) {
			skip("Nothing to process, inbox is empty");
			return;
		}

		getTransactionNumbers();

		String responseLedger = OTAPI.Ledger.createResponse(serverId, nymId, accountId, ledger);
		if (!Util.isValidString(responseLedger))
			error("response ledger is not valid");

		int count = 0;
		for (int index = 0; index < size; index++) {
			String transaction = OTAPI.Ledger.getTransactionByIndex(serverId, nymId, accountId, ledger, index);
			if (!Util.isValidString(transaction)) {
				warn("skipping empty transaction");
				continue;
			}
			String temp = new String(responseLedger);
			responseLedger = OTAPI.Transaction.createResponse(serverId, nymId, accountId, temp, transaction, true);
			count++;
		}
		if (count == 0)
			error("Inbox has only empty transactions");

		if (!Util.isValidString(responseLedger))
			error("response ledger is not valid");
		final String finalResponseLedger = OTAPI.Ledger.finalizeResponse(serverId, nymId, accountId, responseLedger);
		if (!Util.isValidString(finalResponseLedger))
			error("final response ledger is not valid");

		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.processInbox(serverId, nymId, accountId, finalResponseLedger);
			}
		});
		success("Inbox is processed");
	}

	public void processIncome() throws Exception {
		attempt("Processing income");
		getAndProcessNymbox(true);
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
		int i = 0;
		for (int index = size - 1; index >= 0; index--) {// /going backwards to enable removing items
			String instrument = OTAPI.Ledger.getInstrumentbyIndex(serverId, nymId, nymId, ledger, index);
			if (!Util.isValidString(instrument))
				error("instrument is empty");
			String assetId = OTAPI.Instrument.getAssetId(instrument);
			if (!getAccountAssetId(accountId).equals(assetId))
				continue;
			InstrumentType instrumentType = getInstrumentType(instrument);
			if (instrumentType.equals(InstrumentType.INVOICE))
				continue;
			if (instrumentType.equals(InstrumentType.VOUCHER) || instrumentType.equals(InstrumentType.CHEQUE)) {
				UTC now = getTime();
				UTC validFrom = UTC.getDateUTC(OTAPI.Instrument.getValidFrom(instrument));
				if (validFrom != null && validFrom.isAfter(now)) {
					warn("Payment is not valid yet");
					continue;
				}
				UTC validTo = UTC.getDateUTC(OTAPI.Instrument.getValidTo(instrument));
				if (validTo != null && validTo.isBefore(now)) {
					warn("Payment is expired");
					continue;
				}
			}
			if (instrumentType.equals(InstrumentType.VOUCHER)) {
				executeVoucher(instrument);
				i++;
			} else if (instrumentType.equals(InstrumentType.CASH)) {
				importCashToAccount(instrument);
				OTAPI.recordPayment(serverId, nymId, true, index, true);
				i++;
			}
		}
		success("Income is processed", i);
	}

	public void verifyLastReceipt() throws Exception {
		if (!OTAPI.exists("receipts", serverId, String.format("%s.%s", nymId, "success")) && !OTAPI.exists("receipts", serverId, String.format("%s.%s", accountId, "success"))) {
			print("There is no receipt for this account");
			return;
		}
		if (!OTAPI.verifyAccountReceipt(serverId, nymId, accountId)) {
			String ledger = OTAPI.loadNymbox(serverId, nymId);
			if (!Util.isValidString(ledger)) {
				warn("Nymbox is empty");
				return;
			}
			int size = OTAPI.Ledger.getCount(serverId, nymId, accountId, ledger);
			if (size <= 0) {
				if (size < 0)
					warn("Nymbox size is abnormal", size);
				else
					print("Nymbox size is zero");
				return;
			}
			error("The intermediary files have FAILED to verify against the last signed receipt");
		}
		success("The intermediary files have all been VERIFIED against the last signed receipt");
	}

	public static void checkAvailableFunds(String accountId, Double requiredVolume) throws Exception {
		AccountType accountType = AccountType.parse(getAccountType(accountId));
		if (accountType.equals(AccountType.ISSUER))
			return;
		Double balance = getAccountBalanceValue(accountId);
		if (requiredVolume > balance)
			error(String.format("Not enough money in the account. You have only %.2f left.", balance));
		print("Enough money is available");
	}

	public void showTransfer(Double volume, String hisAccountId, String note) {
		print(Util.repeat("-", 13));
		print(String.format("%12s: %s (%s)", "Server", serverId, getServerName(serverId)));
		print(String.format("%12s: %s (%s)", "From", accountId, getAccountName(accountId)));
		print(String.format("%12s: %s (%s)", "To", hisAccountId, getContactAccountName(hisAccountId)));
		print(String.format("%12s: %.2f", "Volume", volume));
		print(String.format("%12s: %s", "Note", note));
		print(Util.repeat("-", 13));
	}

	public void transfer(final Double volume, final String hisAccountId, final String note) throws Exception {
		attempt("Moving account to account");
		if (!Util.isValidString(hisAccountId))
			error("hisAccountId is empty");
		getIntermediaryFiles(true);// /needed only to unblock a newly created account
		print(String.format("%.2f", volume));
		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.notarizeTransfer(serverId, nymId, accountId, volume, hisAccountId, note);
			}
		});
		success("Move account to account is made");
	}

	public void moveAccountToPurse(final Double volume) throws Exception {
		attempt("Moving account to purse");
		final String assetId = getAccountAssetId(accountId);
		if (!OTAPI.mintIsStillGood(serverId, assetId)) {
			sendRequest(new RequestGenerator() {
				@Override
				public int getRequest() {
					return OTAPI.getMint(serverId, nymId, assetId);
				}
			});
		}
		String mintContract = OTAPI.loadMint(serverId, assetId);
		if (!Util.isValidString(mintContract))
			error("mint contract failed to load");
		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.notarizeWithdrawal(serverId, nymId, accountId, volume);
			}
		});
		String purse = OTAPI.loadPurse(serverId, nymId, assetId);
		if (!Util.isValidString(purse))
			error("purse is empty");
		success("Move account to purse is made");
	}

	public void movePurseToAccount(String purse, List<Integer> indices) throws Exception {
		attempt("Moving purse to account");
		String assetId = getAccountAssetId(accountId);
		AssetModule assetModule = new AssetModule(serverId, nymId, assetId);
		final String newPurse = assetModule.processPurse(purse, indices, nymId, false);
		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.notarizeDeposit(serverId, nymId, accountId, newPurse);
			}
		});
		success("Move purse to account is made");
	}

	public void verifyCash(String cash) throws Exception {
		String hisNymId = OTAPI.Instrument.getRecipientNymId(cash);
		if (Util.isValidString(hisNymId) && !nymId.equals(hisNymId))
			error("Your nym and the recipient nym do not match");
		if (!getAccountAssetId(accountId).equals(OTAPI.Instrument.getAssetId(cash)))
			error("Your account's asset type is incompatible with the payment");
	}

	public void importCashToAccount(final String cash) throws Exception {
		attempt("Importing cash to account");
		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.notarizeDeposit(serverId, nymId, accountId, cash);
			}
		});
		success("Cash is successfully imported to account");
	}

	public String exportAccountToCash(Double volume, String hisNymId) throws Exception {
		attempt("Exporting account to cash");
		String assetId = getAccountAssetId(accountId);
		AssetModule assetModule = new AssetModule(serverId, nymId, assetId);

		/** first if there is any existing purse deposit it back to account */
		String oldPurse = OTAPI.loadPurse(serverId, nymId, assetId);
		Double temp = null;
		if (Util.isValidString(oldPurse)) {
			temp = getPurseBalanceValue(serverId, assetId, oldPurse);
			movePurseToAccount(oldPurse, null);
		}

		/** next withdraw from account to purse and export it to cash */
		moveAccountToPurse(volume);
		String newPurse = assetModule.exportPurseToCash(null, hisNymId);

		/** finally if there existed any purse reinstate it */
		if (temp != null)
			moveAccountToPurse(temp);

		success("Account is successfully exported to cash");
		return newPurse;
	}

	public String writeVoucher(final Double volume, final String hisNymId, final String note) throws Exception {
		attempt("Writing voucher");
		if (!Util.isValidString(hisNymId))
			error("hisNymId is empty");
		String message = sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.writeVoucher(serverId, nymId, accountId, volume, hisNymId, note);
			}
		});
		String ledger = OTAPI.Message.getLedger(message);
		if (!Util.isValidString(ledger))
			error("legder is empty");
		String transaction = OTAPI.Ledger.getTransactionByIndex(serverId, nymId, accountId, ledger, 0);
		if (!Util.isValidString(transaction))
			error("transaction is empty");
		String voucher = OTAPI.Transaction.getVoucher(serverId, nymId, accountId, transaction);
		if (!Util.isValidString(voucher))
			error("voucher is empty");
		publish(voucher);
		success("Voucher is written");
		return voucher;
	}

	public void sendVoucher(String voucher, String hisNymId) throws Exception {
		attempt("Sending voucher");
		if (!Util.isValidString(voucher))
			error("voucher is empty");
		if (!Util.isValidString(hisNymId))
			error("hisNymId is empty");
		sendPayment(voucher, hisNymId);
		success("Voucher is sent");
	}

	public Double verifyVoucher(String voucher) throws Exception {
		attempt("Verifying voucher");
		if (!Util.isValidString(voucher))
			error("voucher is empty");
		if (!serverId.equals(OTAPI.Instrument.getServerId(voucher)))
			error("This voucher is not present on this server");
		String hisNymId = OTAPI.Instrument.getRecipientNymId(voucher);
		if (Util.isValidString(hisNymId) && !nymId.equals(hisNymId))
			error("Your nym and the recipient nym do not match");
		if (!getAccountAssetId(accountId).equals(OTAPI.Instrument.getAssetId(voucher)))
			error("Your account's asset type is incompatible with the payment");
		UTC now = getTime();
		UTC validFrom = UTC.getDateUTC(OTAPI.Instrument.getValidTo(voucher));
		if (validFrom != null && validFrom.isAfter(now))
			error("This voucher is not valid yet");
		UTC validTo = UTC.getDateUTC(OTAPI.Instrument.getValidTo(voucher));
		if (validTo != null && validTo.isBefore(now))
			error("This voucher is expired");
		Double volume = getDouble(OTAPI.Instrument.getAmount(voucher));
		success("Voucher is verified");
		return volume;
	}

	public void executeVoucher(final String voucher) throws Exception {
		attempt("Executing voucher");
		if (!Util.isValidString(voucher))
			error("voucher is empty");
		getIntermediaryFiles(true);// /needed only to unblock a newly created account
		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.executeCheque(serverId, nymId, accountId, voucher);
			}
		});
		success("Voucher is executed");
	}

	public String writeCheque(Double volume, String hisNymId, String note, UTC expiry) throws Exception {
		attempt("Writing cheque");
		if (!Util.isValidString(hisNymId))
			hisNymId = "";// error("hisNymId is empty");
		if (!Util.isValidString(note))
			note = "";
		String validFrom = "";
		String validTo = (expiry != null ? new Long(expiry.getSeconds()).toString() : "");
		String cheque = OTAPI.writeCheque(serverId, nymId, accountId, volume, hisNymId, note, validFrom, validTo);
		if (!Util.isValidString(cheque))
			error("cheque is empty");
		publish(cheque);
		success("Cheque is written");
		return cheque;
	}

	public void sendCheque(String cheque, String hisNymId) throws Exception {
		attempt("Sending cheque");
		if (!Util.isValidString(cheque))
			error("cheque is empty");
		if (!Util.isValidString(hisNymId))
			error("hisNymId is empty");
		sendPayment(cheque, hisNymId);
		success("Cheque is sent");
	}

	public void cancelCheque(String cheque) throws Exception {
		attempt("Cancelling cheque");
		if (!Util.isValidString(cheque))
			error("cheque is empty");
		if (!serverId.equals(OTAPI.Instrument.getServerId(cheque)))
			error("This cheque is not present on this server");
		if (!getAccountAssetId(accountId).equals(OTAPI.Instrument.getAssetId(cheque)))
			error("Your account's asset type is incompatible with the payment");
		if (!OTAPI.cancelCheque(serverId, nymId, accountId, cheque))
			error("failed to cancel cheque");
		removeOutpayment(OTAPI.Instrument.getTransactionNum(cheque));
		success("Cheque is cancelled");
	}

	public Double verifyCheque(String cheque) throws Exception {
		attempt("Verifying cheque");
		if (!Util.isValidString(cheque))
			error("cheque is empty");
		if (!serverId.equals(OTAPI.Instrument.getServerId(cheque)))
			error("This cheque is not present on this server");
		String hisNymId = OTAPI.Instrument.getRecipientNymId(cheque);
		if (Util.isValidString(hisNymId) && !nymId.equals(hisNymId))
			error("Your nym and the recipient nym do not match");
		if (!getAccountAssetId(accountId).equals(OTAPI.Instrument.getAssetId(cheque)))
			error("Your account's asset type is incompatible with the payment");
		UTC now = getTime();
		UTC validFrom = UTC.getDateUTC(OTAPI.Instrument.getValidTo(cheque));
		if (validFrom != null && validFrom.isAfter(now))
			error("This cheque is not valid yet");
		UTC validTo = UTC.getDateUTC(OTAPI.Instrument.getValidTo(cheque));
		if (validTo != null && validTo.isBefore(now))
			error("This cheque is expired");
		Double volume = getDouble(OTAPI.Instrument.getAmount(cheque));
		success("Cheque is verified");
		return volume;
	}

	public void executeCheque(final String cheque) throws Exception {
		attempt("Executing cheque");
		if (!Util.isValidString(cheque))
			error("cheque is empty");
		getIntermediaryFiles(true);// /needed only to unblock a newly created account
		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.executeCheque(serverId, nymId, accountId, cheque);
			}
		});
		success("Cheque is executed");
	}

	public String writeInvoice(Double volume, String hisNymId, String note) throws Exception {
		attempt("Writing invoice");
		volume = new Double(0 - volume);
		String validFrom = "";
		String validTo = "";
		String invoice = OTAPI.writeCheque(serverId, nymId, accountId, volume, hisNymId, note, validFrom, validTo);
		if (!Util.isValidString(invoice))
			error("invoice is empty");
		publish(invoice);
		success("Invoice is written");
		return invoice;
	}

	public void sendInvoice(String invoice, String hisNymId) throws Exception {
		attempt("Sending invoice");
		if (!Util.isValidString(invoice))
			error("invoice is empty");
		if (!Util.isValidString(hisNymId))
			error("hisNymId is empty");
		sendPayment(invoice, hisNymId);
		success("Invoice is sent");
	}

	public void cancelInvoice(String invoice) throws Exception {
		attempt("Cancelling invoice");
		if (!Util.isValidString(invoice))
			error("invoice is empty");
		if (!serverId.equals(OTAPI.Instrument.getServerId(invoice)))
			error("This invoice is not present on this server");
		if (!getAccountAssetId(accountId).equals(OTAPI.Instrument.getAssetId(invoice)))
			error("Your account's asset type is incompatible with the payment");
		if (!OTAPI.cancelCheque(serverId, nymId, accountId, invoice))
			error("failed to cancel invoice");
		removeOutpayment(OTAPI.Instrument.getTransactionNum(invoice));
		success("Invoice is cancelled");
	}

	public Double verifyInvoice(String invoice) throws Exception {
		attempt("Verifying invoice");
		if (!Util.isValidString(invoice))
			error("invoice is empty");
		if (!serverId.equals(OTAPI.Instrument.getServerId(invoice)))
			error("This invoice is not present on this server");
		String hisNymId = OTAPI.Instrument.getRecipientNymId(invoice);
		if (Util.isValidString(hisNymId) && !nymId.equals(hisNymId))
			error("Your nym and the recipient nym do not match");
		if (!getAccountAssetId(accountId).equals(OTAPI.Instrument.getAssetId(invoice)))
			error("Your account's asset type is incompatible with the payment");
		UTC now = getTime();
		UTC validFrom = UTC.getDateUTC(OTAPI.Instrument.getValidTo(invoice));
		if (validFrom != null && validFrom.isAfter(now))
			error("This invoice is not valid yet");
		UTC validTo = UTC.getDateUTC(OTAPI.Instrument.getValidTo(invoice));
		if (validTo != null && validTo.isBefore(now))
			error("This invoice is expired");
		Double volume = 0 - getDouble(OTAPI.Instrument.getAmount(invoice));
		success("Invoice is verified");
		return volume;
	}

	public void executeInvoice(final String invoice) throws Exception {
		attempt("Execute invoice");
		if (!Util.isValidString(invoice))
			error("invoice is empty");
		getIntermediaryFiles(true);// /needed only to unblock a newly created account
		sendTransaction(new RequestGenerator() {
			@Override
			public int getRequest() {
				return OTAPI.executeCheque(serverId, nymId, accountId, invoice);
			}
		});
		success("Invoice is executed");
	}

	public void hack1() throws Exception {
		synchronizeRequestNumber();
		getAndProcessNymbox(true);
		getTransactionNumbers();
	}

	public void hack2() throws Exception {
		error(Text.FEATURE_UNSUPPORTED_YET);
	}

	/**********************************************************************
	 * internal
	 *********************************************************************/

	private String sendTransaction(RequestGenerator generator) throws Exception {
		return sendTransaction(generator, true);
	}

	private String sendTransaction(RequestGenerator generator, boolean canRetry) throws Exception {
		getIntermediaryFiles(false);
		getTransactionNumbers();
		OTAPI.flushMessageBuffer();
		int requestId = generator.getRequest();
		String message = processRequest(requestId, false);
		if (message == null)
			error(Text.TRANSACTION_MESSAGE_IS_INVALID);
		{
			attempt(Text.VERIFYING_BALANCE_AGREEMENT);
			int result = OTAPI.verifyBalanceAgreement(serverId, nymId, accountId, message);
			if (result != 1) {
				if (canRetry) {
					warn("Failed to verify balance agreement, proceeding to contingency plan");
					synchronizeRequestNumber();
					getIntermediaryFiles(true);
					getAndProcessNymbox(false);
					warn("Retrying sendTransaction() after contingency plan");
					sendTransaction(generator, false);
					return null;
				}
				error(Text.BALANCE_AGREEMENT_ERROR);
			}
		}
		{
			attempt(Text.VERIFYING_TRANSACTION_MESSAGE);
			int result = OTAPI.verifyTransactionMessage(serverId, nymId, accountId, message);
			if (result != 1)
				error(Text.TRANSACTION_MESSAGE_VERIFICATION_ERROR);
		}
		getIntermediaryFiles(true);
		return message;
	}

	private void getIntermediaryFiles(boolean forceDownload) throws Exception {
		{
			attempt("Synchronizing account");
			sendRequest(new RequestGenerator() {
				@Override
				public int getRequest() {
					return OTAPI.synchronizeAccount(serverId, nymId, accountId);
				}
			});
		}
		{
			attempt("Verifying inbox hash");
			String cachedHash = OTAPI.getInboxHashCached(accountId);
			if (!Util.isValidString(cachedHash))
				warn("Unable to retrieve cached copy of server-side inbox hash");
			String localHash = OTAPI.getInboxHashLocal(nymId, accountId);
			if (!Util.isValidString(localHash))
				warn("Unable to retrieve client-side inbox hash");
			if (!forceDownload && cachedHash.equals(localHash)) {
				skip("The inbox hashes already match, skipping inbox download");
			} else {
				attempt("Downloading inbox");
				sendRequest(new RequestGenerator() {
					@Override
					public int getRequest() {
						return OTAPI.getInbox(serverId, nymId, accountId);
					}
				});
				insureHaveAllBoxReceipts(OTAPI.Box.INBOX, accountId);
			}
		}
		{
			attempt("Verifying outbox hash");
			String cachedHash = OTAPI.getOutboxHashCached(accountId);
			if (!Util.isValidString(cachedHash))
				warn("Unable to retrieve cached copy of server-side outbox hash");
			String localHash = OTAPI.getOutboxHashLocal(nymId, accountId);
			if (!Util.isValidString(localHash))
				warn("Unable to retrieve client-side outbox hash");
			if (!forceDownload && cachedHash.equals(localHash)) {
				skip("The outbox hashes already match, skipping outbox download");
			} else {
				attempt("Downloading outbox");
				sendRequest(new RequestGenerator() {
					@Override
					public int getRequest() {
						return OTAPI.getOutbox(serverId, nymId, accountId);
					}
				});
				insureHaveAllBoxReceipts(OTAPI.Box.OUTBOX, accountId);
			}
		}
	}
}
