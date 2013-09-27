package eu.opentxs.bridge.core.commands.act;

import java.util.List;

import eu.opentxs.bridge.Text;
import eu.opentxs.bridge.UTC;
import eu.opentxs.bridge.Util;
import eu.opentxs.bridge.core.DataModel;
import eu.opentxs.bridge.core.Interpreter;
import eu.opentxs.bridge.core.commands.Command;
import eu.opentxs.bridge.core.commands.Commands;
import eu.opentxs.bridge.core.commands.act.ContactCommands.CreateContact;
import eu.opentxs.bridge.core.commands.act.ContactCommands.CreateContactAccount;
import eu.opentxs.bridge.core.dto.Contact;
import eu.opentxs.bridge.core.dto.ContactAccount;
import eu.opentxs.bridge.core.modules.Module;
import eu.opentxs.bridge.core.modules.act.AccountModule;
import eu.opentxs.bridge.core.modules.act.AssetModule;
import eu.opentxs.bridge.core.modules.act.ContactModule;
import eu.opentxs.bridge.core.modules.act.NymModule;

public class BusinessCommands extends Commands {

	public static void init() {
		addToCommands(new Transfer(), Category.BUSINESS, Sophistication.MINI);

		addToCommands(new MoveAccountToPurse(), Category.BUSINESS, Sophistication.ADVANCED);
		addToCommands(new MovePurseToAccount(), Category.BUSINESS, Sophistication.ADVANCED);

		addToCommands(new ExportPurseToCash(), Category.BUSINESS, Sophistication.ADVANCED);
		addToCommands(new ImportCashToPurse(), Category.BUSINESS_EXTRA, Sophistication.ADVANCED);

		addToCommands(new ExportAccountToCash(), Category.BUSINESS, Sophistication.MINI);
		addToCommands(new ImportCashToAccount(), Category.BUSINESS_EXTRA, Sophistication.MINI);

		addToCommands(new WriteVoucher(), Category.BUSINESS, Sophistication.SIMPLE);
		addToCommands(new ExecuteVoucher(), Category.BUSINESS_EXTRA, Sophistication.SIMPLE);

		addToCommands(new WriteCheque(), Category.BUSINESS, Sophistication.ADVANCED);
		addToCommands(new CancelCheque(), Category.BUSINESS_EXTRA, Sophistication.ADVANCED);
		addToCommands(new ExcecuteCheque(), Category.BUSINESS_EXTRA, Sophistication.ADVANCED);
		addToCommands(new DiscardCheque(), Category.BUSINESS_EXTRA, Sophistication.ADVANCED);

		addToCommands(new WriteInvoice(), Category.BUSINESS, Sophistication.ADVANCED);
		addToCommands(new CancelInvoice(), Category.BUSINESS_EXTRA, Sophistication.ADVANCED);
		addToCommands(new ExecuteInvoice(), Category.BUSINESS_EXTRA, Sophistication.ADVANCED);
		addToCommands(new DiscardInvoice(), Category.BUSINESS_EXTRA, Sophistication.ADVANCED);
	}

	public static class Transfer extends Command {
		private List<ContactAccount> contactAccounts;
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			if (index == 1) {
				contactAccounts = ContactAccount.getList(DataModel.getMyServerId(), DataModel.getMyAssetId());
				return (new Presenter<ContactAccount>() {
					@Override
					protected String id(ContactAccount t) {
						return t.getAccountId();
					}
					@Override
					protected String name(ContactAccount t) {
						return ContactModule.getContactAccountName(t);
					}
				}).show(contactAccounts, true);
			}
			return true;
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsDouble();
			} else if (index == 1) {
				return getListValidator(contactAccounts);
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			Double volume = getDouble(0);
			String hisAccountId = (new Extractor<ContactAccount>() {
				@Override
				public String get(ContactAccount contactAccount) {
					return contactAccount.getAccountId();
				}
			}).eval(1, contactAccounts);
			String note = getString(2, true);
			execute(DataModel.getMyAccountId(), volume, hisAccountId, note);
		}
		/**
		 * Transfer money from one account to another account
		 * 
		 * @param accountId
		 * @param volume
		 * @param hisAccountId
		 * @param note
		 * @throws Exception
		 */
		public static void execute(String accountId, Double volume, String hisAccountId, String note) throws Exception {
			if (hisAccountId.equals(accountId))
				error("Your account and his account are the same");
			if (!ContactModule.verifyContactAccount(hisAccountId)) {
				if (readBooleanFromInput("Would you like to add this account to your contacts?")) {
					String hisNymId = readStringFromInput("Enter nym id");
					String name = readStringFromInput("Enter nym name");
					CreateContact.execute(hisNymId, name);
					CreateContactAccount.execute(hisAccountId, DataModel.getMyAssetId(), hisNymId, DataModel.getMyServerId());
				}
			}
			AccountModule.checkAvailableFunds(accountId, volume);
			AccountModule accountModule = AccountModule.getInstance(accountId);
			accountModule.showTransfer(volume, hisAccountId, note);
			if (readBooleanFromInput("Are you sure you want to make this transfer?")) {
				accountModule.transfer(volume, hisAccountId, note);
				AccountCommands.ShowAccount.execute(accountId);
			}
		}
	}

	public static class MoveAccountToPurse extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsDouble();
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			Double volume = getDouble(0);
			execute(DataModel.getMyAccountId(), volume);
		}
		/**
		 * Move money from an account into a purse
		 * 
		 * @param accountId
		 * @param volume
		 * @throws Exception
		 */
		public static void execute(String accountId, Double volume) throws Exception {
			AccountModule.checkAvailableFunds(accountId, volume);
			AccountModule accountModule = AccountModule.getInstance(accountId);
			accountModule.moveAccountToPurse(volume);
			AccountCommands.ShowAccount.execute(accountId);
			AccountCommands.ShowPurse.execute(accountId);
		}
	}

	public static class MovePurseToAccount extends Command {
		private int purseSize;
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
			AssetModule assetModule = new AssetModule(DataModel.getMyServerId(), DataModel.getMyNymId(), DataModel.getMyAssetId());
			String purse = assetModule.loadPurse();
			if (!Util.isValidString(purse))
				error("Your purse seems to be empty");
			purseSize = assetModule.getPurseSize(purse);
		}
		@Override
		public boolean introduceArgument(int index) {
			if (index == 0) {
				try {
					AccountCommands.ShowPurse.execute(DataModel.getMyAccountId());
				} catch (Exception e) {
				}
			}
			return true;
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsIntegerList() {
					{
						setCanBeEmpty(true);
						setMinMax(1, purseSize);
					}
				};
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			List<Integer> indices = getIntegerList(0, true);
			execute(DataModel.getMyAccountId(), indices);
		}
		/**
		 * Move money from a purse into an account
		 * 
		 * @param accountId
		 * @param indices
		 * @throws Exception
		 */
		public static void execute(String accountId, List<Integer> indices) throws Exception {
			String serverId = Module.getAccountServerId(accountId);
			String nymId = Module.getAccountNymId(accountId);
			String assetId = Module.getAccountAssetId(accountId);
			AssetModule assetModule = new AssetModule(serverId, nymId, assetId);
			String purse = assetModule.loadPurse();
			AccountModule accountModule = AccountModule.getInstance(accountId);
			accountModule.movePurseToAccount(purse, indices);
			AccountCommands.ShowPurse.execute(accountId);
			AccountCommands.ShowAccount.execute(accountId);
		}
	}

	public static class ImportCashToAccount extends Command {
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		protected void action(String[] args) throws Exception {
			String cash = readStringFromInput("Paste cash here");
			if (Util.isValidString(cash) && isValidCashContract(cash)) {
				cash = Interpreter.restoreNewLines(cash);
			} else {
				print("This does not look like cash");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild cash was supplied");
				cash = readStringFromFile(Text.FOLDER_CASH, Extension.CONTRACT);
				if (!Util.isValidString(cash))
					error("No vaild cash was supplied");
			}
			execute(DataModel.getMyAccountId(), cash);
		}
		/**
		 * Import money as cash and deposit it into an account
		 * 
		 * @param accountId
		 * @param cash
		 * @throws Exception
		 */
		public static void execute(String accountId, String cash) throws Exception {
			String serverId = Module.getAccountServerId(accountId);
			String nymId = Module.getAccountNymId(accountId);
			String assetId = Module.getAccountAssetId(accountId);
			AssetModule assetModule = new AssetModule(serverId, nymId, assetId);
			assetModule.showPurse(cash);
			if (readBooleanFromInput("Are you sure you want to import this cash?")) {
				AccountModule accountModule = AccountModule.getInstance(accountId);
				accountModule.verifyCash(cash);
				accountModule.importCashToAccount(cash);
			}
			AccountCommands.ShowAccount.execute(accountId);
		}
	}

	public static class ExportAccountToCash extends Command {
		private List<Contact> contacts;
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			if (index == 1) {
				contacts = Contact.getList();
				return (new Presenter<Contact>() {
					@Override
					protected String id(Contact t) {
						return t.getNymId();
					}
					@Override
					protected String name(Contact t) {
						return t.getName();
					}
				}).show(contacts, true);
			}
			return true;
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsDouble();
			} else if (index == 1) {
				return getListValidator(contacts, true);
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			Double volume = getDouble(0);
			String hisNymId = (new Extractor<Contact>() {
				@Override
				public String get(Contact contact) {
					return contact.getNymId();
				}
			}).eval(1, contacts);
			execute(DataModel.getMyAccountId(), volume, hisNymId);
		}
		/**
		 * Withdraw money from an account and export it as cash
		 * 
		 * @param accountId
		 * @param volume
		 * @param hisNymId
		 * @throws Exception
		 */
		public static void execute(String accountId, Double volume, String hisNymId) throws Exception {
			// /
			if (!Util.isValidString(hisNymId))
				error(String.format("%s: %s", Text.FEATURE_DISABLED_SERVER_BUG, "hisNymId cannot be empty"));
			// /
			if (!ContactModule.verifyContact(hisNymId)) {
				if (readBooleanFromInput("Would you like to add this nym to your contacts?")) {
					String name = readStringFromInput("Enter nym name");
					CreateContact.execute(hisNymId, name);
				}
			}
			AccountModule.checkAvailableFunds(accountId, volume);
			AccountModule accountModule = AccountModule.getInstance(accountId);
			String cash = accountModule.exportAccountToCash(volume, hisNymId);
			if (readBooleanFromInput("Would you like to send the exported cash to the recipient?"))
				accountModule.sendCash(cash, hisNymId);
			if (readBooleanFromInput("Would you like to save the exported cash to a file?"))
				writeStringToFile(Text.FOLDER_CASH, Extension.CONTRACT, cash);
			AccountCommands.ShowAccount.execute(accountId);
		}
	}

	public static class ExportPurseToCash extends Command {
		private int purseSize;
		private List<Contact> contacts;
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
			AssetModule assetModule = new AssetModule(DataModel.getMyServerId(), DataModel.getMyNymId(), DataModel.getMyAssetId());
			String purse = assetModule.loadPurse();
			if (!Util.isValidString(purse))
				error("Your purse seems to be empty");
			purseSize = assetModule.getPurseSize(purse);
		}
		@Override
		public boolean introduceArgument(int index) {
			if (index == 0) {
				try {
					AccountCommands.ShowPurse.execute(DataModel.getMyAccountId());
				} catch (Exception e) {
				}
			} else if (index == 1) {
				contacts = Contact.getList();
				return (new Presenter<Contact>() {
					@Override
					protected String id(Contact t) {
						return t.getNymId();
					}
					@Override
					protected String name(Contact t) {
						return t.getName();
					}
				}).show(contacts, true);
			}
			return true;
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsIntegerList() {
					{
						setCanBeEmpty(true);
						setMinMax(1, purseSize);
					}
				};
			}
			if (index == 1) {
				return getListValidator(contacts, true);
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			List<Integer> indices = getIntegerList(0, true);
			String hisNymId = (new Extractor<Contact>() {
				@Override
				public String get(Contact contact) {
					return contact.getNymId();
				}
			}).eval(1, contacts);
			execute(DataModel.getMyAccountId(), indices, hisNymId);
		}
		/**
		 * Withdraw money from a purse and export it as cash
		 * 
		 * @param accountId
		 * @param indices
		 * @param hisNymId
		 * @throws Exception
		 */
		public static void execute(String accountId, List<Integer> indices, String hisNymId) throws Exception {
			// /
			if (!Util.isValidString(hisNymId))
				error(String.format("%s: %s", Text.FEATURE_DISABLED_SERVER_BUG, "hisNymId cannot be empty"));
			// /
			if (!ContactModule.verifyContact(hisNymId)) {
				if (readBooleanFromInput("Would you like to add this nym to your contacts?")) {
					String name = readStringFromInput("Enter nym name");
					CreateContact.execute(hisNymId, name);
				}
			}
			String serverId = Module.getAccountServerId(accountId);
			String nymId = Module.getAccountNymId(accountId);
			String assetId = Module.getAccountAssetId(accountId);
			AssetModule assetModule = new AssetModule(serverId, nymId, assetId);
			String cash = assetModule.exportPurseToCash(indices, hisNymId);
			if (readBooleanFromInput("Would you like to send the exported cash to the recipient?"))
				assetModule.sendCash(cash, hisNymId);
			if (readBooleanFromInput("Would you like to save the exported cash to a file?"))
				writeStringToFile(Text.FOLDER_CASH, Extension.CONTRACT, cash);
			AccountCommands.ShowPurse.execute(accountId);
		}
	}

	public static class ImportCashToPurse extends Command {
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		protected void action(String[] args) throws Exception {
			String cash = readStringFromInput("Paste cash here");
			if (Util.isValidString(cash) && isValidCashContract(cash)) {
				cash = Interpreter.restoreNewLines(cash);
			} else {
				print("This does not look like cash");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild cash was supplied");
				cash = readStringFromFile(Text.FOLDER_CASH, Extension.CONTRACT);
				if (!Util.isValidString(cash))
					error("No vaild cash was supplied");
			}
			execute(DataModel.getMyAccountId(), cash);
		}
		/**
		 * Import money as cash and deposit it into a purse
		 * 
		 * @param accountId
		 * @param cash
		 * @throws Exception
		 */
		public static void execute(String accountId, String cash) throws Exception {
			String serverId = Module.getAccountServerId(accountId);
			String nymId = Module.getAccountNymId(accountId);
			String assetId = AssetModule.getCashAssetId(cash);
			AssetModule assetModule = new AssetModule(serverId, nymId, assetId);
			assetModule.showPurse(cash);
			if (readBooleanFromInput("Are you sure you want to import this cash?")) {
				assetModule.verifyCash(cash);
				assetModule.importCashToPurse(cash);
				AccountCommands.ShowPurse.execute(accountId);
			}
		}
	}

	public static class WriteVoucher extends Command {
		private List<Contact> contacts;
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			if (index == 1) {
				contacts = Contact.getList();
				return (new Presenter<Contact>() {
					@Override
					protected String id(Contact t) {
						return t.getNymId();
					}
					@Override
					protected String name(Contact t) {
						return t.getName();
					}
				}).show(contacts, true);
			}
			return true;
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsDouble();
			} else if (index == 1) {
				return getListValidator(contacts);
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			Double volume = getDouble(0);
			String hisNymId = (new Extractor<Contact>() {
				@Override
				public String get(Contact contact) {
					return contact.getNymId();
				}
			}).eval(1, contacts);
			String note = getString(2, true);
			execute(DataModel.getMyAccountId(), volume, hisNymId, note);
		}
		/**
		 * Write (withdraw) a voucher
		 * 
		 * @param accountId
		 * @param volume
		 * @param hisNymId
		 * @param note
		 * @throws Exception
		 */
		public static void execute(String accountId, Double volume, String hisNymId, String note) throws Exception {
			if (!ContactModule.verifyContact(hisNymId)) {
				if (readBooleanFromInput("Would you like to add this nym to your contacts?")) {
					String name = readStringFromInput("Enter nym name");
					CreateContact.execute(hisNymId, name);
				}
			}
			AccountModule.checkAvailableFunds(accountId, volume);
			AccountModule accountModule = AccountModule.getInstance(accountId);
			String voucher = accountModule.writeVoucher(volume, hisNymId, note);
			if (readBooleanFromInput("Would you like to send the voucher to the recipient?"))
				accountModule.sendVoucher(voucher, hisNymId);
			else if (readBooleanFromInput("Would you like to save the voucher to a file?"))
				writeStringToFile(Text.FOLDER_CHEQUES, Extension.CONTRACT, voucher);
			AccountCommands.ShowAccount.execute(accountId);
		}
	}

	public static class ExecuteVoucher extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		protected void action(String[] args) throws Exception {
			String voucher = readStringFromInput("Paste a voucher here");
			if (Util.isValidString(voucher) && isValidVoucherContract(voucher)) {
				voucher = Interpreter.restoreNewLines(voucher);
			} else {
				print("This does not look like a voucher");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild voucher was supplied");
				voucher = readStringFromFile(Text.FOLDER_CHEQUES, Extension.CONTRACT);
				if (!Util.isValidString(voucher))
					error("No vaild voucher was supplied");
			}
			execute(DataModel.getMyAccountId(), voucher);
		}
		/**
		 * Execute (deposit) a voucher into an account
		 * 
		 * @param accountId
		 * @param voucher
		 * @throws Exception
		 */
		public static void execute(String accountId, String voucher) throws Exception {
			AccountModule accountModule = AccountModule.getInstance(accountId);
			Double volume = accountModule.verifyVoucher(voucher);
			print(String.format("Voucher value: %.2f", volume));
			if (readBooleanFromInput("Are you sure you want to deposit this voucher?")) {
				accountModule.executeVoucher(voucher);
				AccountCommands.ShowAccount.execute(accountId);
			}
		}
	}

	public static class WriteCheque extends Command {
		private List<Contact> contacts;
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			if (index == 1) {
				contacts = Contact.getList();
				return (new Presenter<Contact>() {
					@Override
					protected String id(Contact t) {
						return t.getNymId();
					}
					@Override
					protected String name(Contact t) {
						return t.getName();
					}
				}).show(contacts, true);
			}
			return true;
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsDouble();
			} else if (index == 1) {
				return getListValidator(contacts);
			} else if (index == 3) {
				return new IsUTC() {
					{
						setCanBeEmpty(true);
					}
				};
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			Double volume = getDouble(0);
			String hisNymId = (new Extractor<Contact>() {
				@Override
				public String get(Contact contact) {
					return contact.getNymId();
				}
			}).eval(1, contacts);
			String note = getString(2, true);
			UTC expiry = getUTC(3, true);
			execute(DataModel.getMyAccountId(), volume, hisNymId, note, expiry);
		}
		/**
		 * Write (withdraw) a cheque
		 * 
		 * @param accountId
		 * @param volume
		 * @param hisNymId
		 * @param note
		 * @param expiry
		 * @throws Exception
		 */
		public static void execute(String accountId, Double volume, String hisNymId, String note, UTC expiry)
				throws Exception {
			if (!ContactModule.verifyContact(hisNymId)) {
				if (readBooleanFromInput("Would you like to add this nym to your contacts?")) {
					String name = readStringFromInput("Enter nym name");
					CreateContact.execute(hisNymId, name);
				}
			}
			AccountModule accountModule = AccountModule.getInstance(accountId);
			String cheque = accountModule.writeCheque(volume, hisNymId, note, expiry);
			if (readBooleanFromInput("Would you like to send the cheque to the recipient?"))
				accountModule.sendCheque(cheque, hisNymId);
			if (readBooleanFromInput("Would you like to save the cheque to a file?"))
				writeStringToFile(Text.FOLDER_CHEQUES, Extension.CONTRACT, cheque);
		}
	}
	
	public static class CancelCheque extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		protected void action(String[] args) throws Exception {
			String cheque = readStringFromInput("Paste a cheque here");
			if (Util.isValidString(cheque) && isValidChequeContract(cheque)) {
				cheque = Interpreter.restoreNewLines(cheque);
			} else {
				print("This does not look like a cheque");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild cheque was supplied");
				cheque = readStringFromFile(Text.FOLDER_CHEQUES, Extension.CONTRACT);
				if (!Util.isValidString(cheque))
					error("No vaild cheque was supplied");
			}
			execute(DataModel.getMyAccountId(), cheque);
		}
		/**
		 * Cancel a cheque so that the recipient can no longer deposit it
		 * 
		 * @param accountId
		 * @param cheque
		 * @throws Exception
		 */
		public static void execute(String accountId, String cheque) throws Exception {
			AccountModule accountModule = AccountModule.getInstance(accountId);
			if (readBooleanFromInput("Are you sure you want to cancel this cheque?"))
				accountModule.cancelCheque(cheque);
		}
	}

	public static class ExcecuteCheque extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		protected void action(String[] args) throws Exception {
			String cheque = readStringFromInput("Paste a cheque here");
			if (Util.isValidString(cheque) && isValidChequeContract(cheque)) {
				cheque = Interpreter.restoreNewLines(cheque);
			} else {
				print("This does not look like a cheque");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild cheque was supplied");
				cheque = readStringFromFile(Text.FOLDER_CHEQUES, Extension.CONTRACT);
				if (!Util.isValidString(cheque))
					error("No vaild cheque was supplied");
			}
			execute(DataModel.getMyAccountId(), cheque);
		}
		/**
		 * Execute (deposit) cheque into an account
		 * 
		 * @param accountId
		 * @param cheque
		 * @throws Exception
		 */
		public static void execute(String accountId, String cheque) throws Exception {
			AccountModule accountModule = AccountModule.getInstance(accountId);
			Double volume = accountModule.verifyCheque(cheque);
			print(String.format("Cheque value: %.2f", volume));
			if (readBooleanFromInput("Are you sure you want to deposit this cheque?")) {
				accountModule.executeCheque(cheque);
				AccountCommands.ShowAccount.execute(accountId);
			}
		}
	}

	public static class DiscardCheque extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		protected void action(String[] args) throws Exception {
			String cheque = readStringFromInput("Paste a cheque here");
			if (Util.isValidString(cheque) && isValidChequeContract(cheque)) {
				cheque = Interpreter.restoreNewLines(cheque);
			} else {
				print("This does not look like a cheque");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild cheque was supplied");
				cheque = readStringFromFile(Text.FOLDER_CHEQUES, Extension.CONTRACT);
				if (!Util.isValidString(cheque))
					error("No vaild cheque was supplied");
			}
			execute(DataModel.getMyServerId(), DataModel.getMyNymId(), cheque);
		}
		/**
		 * Discard a cheque instead of depositing it
		 * 
		 * @param serverId
		 * @param nymId
		 * @param cheque
		 * @throws Exception
		 */
		public static void execute(String serverId, String nymId, String cheque) throws Exception {
			NymModule nymModule = new NymModule(serverId, nymId);
			if (readBooleanFromInput("Are you sure you want to discard this cheque?"))
				nymModule.discardCheque(cheque);
		}
	}

	public static class WriteInvoice extends Command {
		private List<Contact> contacts;
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			if (index == 1) {
				contacts = Contact.getList();
				return (new Presenter<Contact>() {
					@Override
					protected String id(Contact t) {
						return t.getNymId();
					}
					@Override
					protected String name(Contact t) {
						return t.getName();
					}
				}).show(contacts, true);
			}
			return true;
		}
		@Override
		public Validator getValidator(int index) {
			if (index == 0) {
				return new IsDouble();
			} else if (index == 1) {
				return getListValidator(contacts);
			}
			return null;
		}
		@Override
		protected void action(String[] args) throws Exception {
			Double volume = getDouble(0);
			String hisNymId = (new Extractor<Contact>() {
				@Override
				public String get(Contact contact) {
					return contact.getNymId();
				}
			}).eval(1, contacts);
			String note = getString(2, true);
			execute(DataModel.getMyAccountId(), volume, hisNymId, note);
		}
		/**
		 * Write (create) an invoice
		 * 
		 * @param accountId
		 * @param volume
		 * @param hisNymId
		 * @param note
		 * @throws Exception
		 */
		public static void execute(String accountId, Double volume, String hisNymId, String note) throws Exception {
			if (!ContactModule.verifyContact(hisNymId)) {
				if (readBooleanFromInput("Would you like to add this nym to your contacts?")) {
					String name = readStringFromInput("Enter nym name");
					CreateContact.execute(hisNymId, name);
				}
			}
			AccountModule accountModule = AccountModule.getInstance(accountId);
			String invoice = accountModule.writeInvoice(volume, hisNymId, note);
			if (readBooleanFromInput("Would you like to send the invoice to the recipient?"))
				accountModule.sendInvoice(invoice, hisNymId);
			if (readBooleanFromInput("Would you like to save the invoice to a file?"))
				writeStringToFile(Text.FOLDER_CHEQUES, Extension.CONTRACT, invoice);
		}
	}

	public static class CancelInvoice extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		protected void action(String[] args) throws Exception {
			String invoice = readStringFromInput("Paste a invoice here");
			if (Util.isValidString(invoice) && isValidInvoiceContract(invoice)) {
				invoice = Interpreter.restoreNewLines(invoice);
			} else {
				print("This does not look like a invoice");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild invoice was supplied");
				invoice = readStringFromFile(Text.FOLDER_CHEQUES, Extension.CONTRACT);
				if (!Util.isValidString(invoice))
					error("No vaild invoice was supplied");
			}
			execute(DataModel.getMyAccountId(), invoice);
		}
		/**
		 * Cancel an invoice so that the recipient can no longer pay it
		 * 
		 * @param accountId
		 * @param invoice
		 * @throws Exception
		 */
		public static void execute(String accountId, String invoice) throws Exception {
			AccountModule accountModule = AccountModule.getInstance(accountId);
			if (readBooleanFromInput("Are you sure you want to cancel this invoice?"))
				accountModule.cancelInvoice(invoice);
		}
	}

	public static class ExecuteInvoice extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		protected void action(String[] args) throws Exception {
			String invoice = readStringFromInput("Paste a invoice here");
			if (Util.isValidString(invoice) && isValidInvoiceContract(invoice)) {
				invoice = Interpreter.restoreNewLines(invoice);
			} else {
				print("This does not look like a invoice");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild invoice was supplied");
				invoice = readStringFromFile(Text.FOLDER_CHEQUES, Extension.CONTRACT);
				if (!Util.isValidString(invoice))
					error("No vaild invoice was supplied");
			}
			execute(DataModel.getMyAccountId(), invoice);
		}
		/**
		 * Execute (pay) an invoice using funds from a given account
		 * 
		 * @param accountId
		 * @param invoice
		 * @throws Exception
		 */
		public static void execute(String accountId, String invoice) throws Exception {
			AccountModule accountModule = AccountModule.getInstance(accountId);
			Double volume = accountModule.verifyInvoice(invoice);
			print(String.format("Invoice value: %.2f", volume));
			if (readBooleanFromInput("Are you sure you want to execute this invoice?")) {
				accountModule.executeInvoice(invoice);
				AccountCommands.ShowAccount.execute(accountId);
			}
		}
	}
	
	public static class DiscardInvoice extends Command {
		@Override
		public void sanity() throws Exception {
			if (!Util.isValidString(DataModel.getMyAccountId()))
				error("You need to set your account first");
		}
		@Override
		public boolean introduceArgument(int index) {
			return false;
		}
		@Override
		protected void action(String[] args) throws Exception {
			String invoice = readStringFromInput("Paste a invoice here");
			if (Util.isValidString(invoice) && isValidInvoiceContract(invoice)) {
				invoice = Interpreter.restoreNewLines(invoice);
			} else {
				print("This does not look like a invoice");
				if (!readBooleanFromInput("Would you like to open it from a file?"))
					error("No vaild invoice was supplied");
				invoice = readStringFromFile(Text.FOLDER_CHEQUES, Extension.CONTRACT);
				if (!Util.isValidString(invoice))
					error("No vaild invoice was supplied");
			}
			execute(DataModel.getMyServerId(), DataModel.getMyNymId(), invoice);
		}
		/**
		 * Discard an invoice instead of paying it
		 * 
		 * @param serverId
		 * @param nymId
		 * @param invoice
		 * @throws Exception
		 */
		public static void execute(String serverId, String nymId, String invoice) throws Exception {
			NymModule nymModule = new NymModule(serverId, nymId);
			if (readBooleanFromInput("Are you sure you want to discard this invoice?"))
				nymModule.discardInvoice(invoice);
		}
	}
}
