package com.perficient.etm.security;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

public class AppUserDetailsImpl extends LdapUserDetailsImpl implements AppUserDetails {

	private static final long serialVersionUID = -435642509341358317L;

	private String firstName;

	private String lastName;

	private String email;

	private String employeeId;

	private String title;

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getEmployeeId() {
		return employeeId;
	}

	public static class Essence extends LdapUserDetailsImpl.Essence {

		public Essence() {
		}

		public Essence(DirContextOperations ctx) {
			super(ctx);
			setFirstName(ctx.getStringAttribute("givenName"));
			setLastName(ctx.getStringAttribute("sn"));
			setEmail(ctx.getStringAttribute("mail"));
			setEmployeeId(ctx.getStringAttribute("employeeID"));
			setTitle(ctx.getStringAttribute("title"));
		}

		public Essence(AppUserDetailsImpl copyMe) {
			super(copyMe);
			setFirstName(copyMe.firstName);
			setLastName(copyMe.lastName);
			setEmail(copyMe.email);
			setEmployeeId(copyMe.employeeId);
			setTitle(copyMe.title);
		}

		protected LdapUserDetailsImpl createTarget() {
			return new AppUserDetailsImpl();
		}

		public void setLastName(String lastName) {
			((AppUserDetailsImpl) instance).lastName = lastName;
		}

		public void setFirstName(String firstName) {
			((AppUserDetailsImpl) instance).firstName = firstName;
		}

		public void setEmail(String email) {
			((AppUserDetailsImpl) instance).email = email;
		}

		public void setEmployeeId(String employeeId) {
			((AppUserDetailsImpl) instance).employeeId = employeeId;
		}

		public void setTitle(String title) {
			((AppUserDetailsImpl) instance).title = title;
		}

		public AppUserDetails createAppUserDetails() {
			return (AppUserDetails) super.createUserDetails();
		}
	}
}
