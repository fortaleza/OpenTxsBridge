package net.otxs.bridge.core.dto;

import java.io.Serializable;
import java.util.List;

import net.otxs.bridge.core.OTBridge;

import org.hibernate.Session;

public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nymId;
	private String name;

	public String getNymId() {
		return nymId;
	}

	public String getName() {
		return name;
	}

	public void setNymId(String nymId) {
		this.nymId = nymId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void main(String[] args) {
		{
			add("xxx", "Mr X");
			add("yyy", "Mr Y");
			add("zzz", "Mr Z");
			update("zzz", "Mr Zzz");
			delete("zzz");
		}
		{
			Contact contact = get("xxx");
			System.out.println(contact);
		}
		{
			List<Contact> contacts = getList();
			for (Contact contact : contacts)
				System.out.println(contact);
		}
		System.exit(0);
	}

	public static Contact add(String nymId, String name) {
		Contact contact = null;
		Session sess = null;
		try {
			sess = OTBridge.getDatabase().beginTransaction();
			contact = (Contact) sess.get(Contact.class, nymId);
			if (contact != null) {
				contact = null;
			} else {
				contact = new Contact();
				contact.nymId = nymId;
				contact.name = name;
				sess.save(contact);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OTBridge.getDatabase().commitClose(sess);
		}
		return contact;
	}

	public static Contact update(String nymId, String name) {
		Session sess = null;
		Contact contact = null;
		try {
			sess = OTBridge.getDatabase().beginTransaction();
			contact = (Contact) sess.get(Contact.class, nymId);
			if (contact != null) {
				contact.setName(name);
				sess.update(contact);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OTBridge.getDatabase().commitClose(sess);
		}
		return contact;
	}

	public static Contact delete(String nymId) {
		Session sess = null;
		Contact contact = null;
		try {
			sess = OTBridge.getDatabase().beginTransaction();
			contact = (Contact) sess.get(Contact.class, nymId);
			if (contact != null)
				sess.delete(contact);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OTBridge.getDatabase().commitClose(sess);
		}
		return contact;
	}

	public static Contact get(String nymId) {
		Contact contact = null;
		Session sess = null;
		try {
			sess = OTBridge.getDatabase().beginTransaction();
			contact = (Contact) sess.get(Contact.class, nymId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OTBridge.getDatabase().commitClose(sess);
		}
		return contact;
	}

	@SuppressWarnings("unchecked")
	public static List<Contact> getList() {
		List<Contact> contacts = null;
		Session sess = null;
		try {
			sess = OTBridge.getDatabase().beginTransaction();
			contacts = sess.createCriteria(Contact.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OTBridge.getDatabase().commitClose(sess);
		}
		return contacts;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", nymId, name);
	}
}