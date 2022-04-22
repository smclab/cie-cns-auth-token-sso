/**
 * Copyright (c) 2022 SMC Treviso Srl. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package it.smc.labs.bootcamp.liferay.security.auto.login.token.impl.exportimport;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.exportimport.UserImporter;
import com.liferay.portal.security.pwd.RegExpToolkit;
import com.liferay.portal.util.PropsValues;

import it.smc.labs.bootcamp.liferay.security.auto.login.token.repository.DetailsUserInfo;
import it.smc.labs.bootcamp.liferay.security.auto.login.token.repository.model.ExternalUser;

import java.util.Calendar;
import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Musarra
 */
@Component(immediate = true, service = UserImporter.class)
public class ExternalUserImporterImpl implements UserImporter {

	@Override
	public long getLastImportTime() throws Exception {
		return 0;
	}

	@Override
	public User importUser(
			long ldapServerId, long companyId, String emailAddress,
			String screenName)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method is not implemented. Not supported user import by " +
				"ldap server.");
	}

	@Override
	public User importUser(
			long companyId, String emailAddress, String screenName)
		throws Exception {

		if (Validator.isNotNull(screenName)) {
			return importUserByScreenName(companyId, screenName);
		}

		throw new UnsupportedOperationException(
			"This method is not implemented. Supported user import only by " +
				"screen name.");
	}

	@Override
	public User importUserByScreenName(long companyId, String screenName)
		throws PortalException {

		ExternalUser externalUser = _getExternalUser(
			companyId, StringPool.BLANK, screenName);

		User user = _getUser(companyId, externalUser);

		if (user != null) {
			return user;
		}

		return _addUser(companyId, externalUser);
	}

	@Override
	public User importUserByUuid(long ldapServerId, long companyId, String uuid)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public User importUserByUuid(long companyId, String uuid)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void importUsers() throws PortalException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void importUsers(long companyId) throws PortalException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void importUsers(long ldapServerId, long companyId)
		throws PortalException {

		throw new UnsupportedOperationException(
			"This method is not implemented. Not supported user import by " +
				"ldap server.");
	}

	private User _addUser(long companyId, ExternalUser externalUser)
		throws PortalException {

		long[] groupIds = new long[0];
		long[] organizationIds = new long[0];
		long[] roleIds = new long[0];
		long[] userGroupIds = new long[0];

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();
		Date birthday = externalUser.getBirthDate();

		birthdayCal.setTime(birthday);

		int birthdayMonth = birthdayCal.get(Calendar.MONTH);
		int birthdayDay = birthdayCal.get(Calendar.DAY_OF_MONTH);
		int birthdayYear = birthdayCal.get(Calendar.YEAR);

		RegExpToolkit regExpToolkit = new RegExpToolkit();

		String password = regExpToolkit.generate(null);

		User user = _userLocalService.addUser(
			0L, companyId, true, password, password, false,
			externalUser.getFiscalCode(), externalUser.getEmailAddress(),
			LocaleUtil.fromLanguageId(externalUser.getLanguageId()),
			externalUser.getFirstName(), externalUser.getMiddleName(),
			externalUser.getLastName(), 0, 0, externalUser.isMale(),
			birthdayMonth, birthdayDay, birthdayYear, StringPool.BLANK,
			groupIds, organizationIds, roleIds, userGroupIds, false,
			new ServiceContext());

		_userLocalService.updateEmailAddressVerified(user.getUserId(), true);
		_userLocalService.updateAgreedToTermsOfUse(user.getUserId(), true);

		_userLocalService.updateReminderQuery(
			user.getUserId(),
			"ReminderQueryQuestion-" + StringUtil.randomString(),
			"ReminderQueryAnswer-" + StringUtil.randomString());
		_userLocalService.addDefaultRoles(user.getUserId());
		_userLocalService.addDefaultGroups(user.getUserId());
		_userLocalService.addDefaultUserGroups(user.getUserId());

		return _userLocalService.updatePassword(
			user.getUserId(), password, password, false);
	}

	private ExternalUser _getExternalUser(
			long companyId, String email, String screenName)
		throws PortalException {

		if (Validator.isNotNull(screenName)) {
			return _detailsUserInfo.getUserByScreenName(companyId, screenName);
		}

		if (Validator.isNotNull(email)) {
			return _detailsUserInfo.getUserByEmail(companyId, screenName);
		}

		throw new PortalException("Email or ScreenName cannot be null");
	}

	private User _getUser(long companyId, ExternalUser externalUser) {
		String authType = PrefsPropsUtil.getString(
			companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
			PropsValues.COMPANY_SECURITY_AUTH_TYPE);

		if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			return _userLocalService.fetchUserByScreenName(
				companyId, externalUser.getFiscalCode());
		}

		return _userLocalService.fetchUserByEmailAddress(
			companyId, externalUser.getEmailAddress());
	}

	@Reference
	private DetailsUserInfo _detailsUserInfo;

	@Reference
	private UserLocalService _userLocalService;

}