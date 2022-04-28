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

package it.smc.labs.bootcamp.liferay.security.auto.login.token.impl.remote;

import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.uuid.PortalUUID;

import it.smc.labs.bootcamp.liferay.security.auto.login.token.repository.DetailsUserInfo;
import it.smc.labs.bootcamp.liferay.security.auto.login.token.repository.model.ExternalUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Musarra
 */
@Component(immediate = true, service = DetailsUserInfo.class)
public class DetailsUserInfoMemoryImpl implements DetailsUserInfo {

	public ExternalUser getUserByEmail(long companyId, String email)
		throws NoSuchUserException {

		throw new UnsupportedOperationException(
			"This method is not implemented. Supported user import only by " +
				"screen name.");
	}

	public ExternalUser getUserByScreenName(long companyId, String screenName)
		throws PortalException {

		if (_externalUserHashMap.containsKey(screenName)) {
			return _externalUserHashMap.get(screenName);
		}

		throw new NoSuchUserException(
			String.format(
				"No such user for companyId: %s and screenName: %s", companyId,
				screenName));
	}

	@Activate
	protected void activate() {
		if (_externalUserHashMap.isEmpty()) {
			ExternalUser externalUser_1 = new ExternalUser();
			ExternalUser externalUser_2 = new ExternalUser();
			ExternalUser externalUser_3 = new ExternalUser();

			externalUser_1.setFirstName("Antonio");
			externalUser_1.setLastName("Musarra");
			externalUser_1.setGender("M");
			externalUser_1.setFiscalCode("MSRNTN77H15C351X");
			externalUser_1.setBirthDate(new Date(235173600));
			externalUser_1.setEmailAddress("antonio.musarra@smc.it");
			externalUser_1.setLanguageId(LocaleUtil.toLanguageId(Locale.ITALY));
			externalUser_1.setStatus(1);
			externalUser_1.setUuid(_portalUUID.generate());
			externalUser_1.setCreateDate(new Date());
			externalUser_1.setRolesName(new String[] {"Administrator"});

			externalUser_2.setFirstName("Mario");
			externalUser_2.setLastName("Rossi");
			externalUser_2.setGender("M");
			externalUser_2.setFiscalCode("ROSMRO78H15C651F");
			externalUser_2.setBirthDate(new Date(235173600));
			externalUser_2.setEmailAddress("mario.rossi@nomail.it");
			externalUser_2.setLanguageId(LocaleUtil.toLanguageId(Locale.ITALY));
			externalUser_2.setStatus(1);
			externalUser_2.setUuid(_portalUUID.generate());
			externalUser_2.setCreateDate(new Date());

			externalUser_3.setFirstName("Laura");
			externalUser_3.setLastName("Bianchi");
			externalUser_3.setGender("F");
			externalUser_3.setFiscalCode("LURBAC67G10C371B");
			externalUser_3.setBirthDate(new Date(235173600));
			externalUser_3.setEmailAddress("laura.bianchi@nomail.it");
			externalUser_3.setLanguageId(LocaleUtil.toLanguageId(Locale.ITALY));
			externalUser_3.setStatus(1);
			externalUser_3.setUuid(_portalUUID.generate());
			externalUser_3.setCreateDate(new Date());

			_externalUserHashMap.put(
				externalUser_1.getFiscalCode(), externalUser_1);
			_externalUserHashMap.put(
				externalUser_2.getFiscalCode(), externalUser_2);
			_externalUserHashMap.put(
				externalUser_3.getFiscalCode(), externalUser_3);

			if (_log.isDebugEnabled()) {
				_log.debug("Loaded the external users to memory");
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		_externalUserHashMap.clear();

		if (_log.isDebugEnabled()) {
			_log.debug("Unloaded the external users from memory");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DetailsUserInfoMemoryImpl.class);

	private static final Map<String, ExternalUser> _externalUserHashMap =
		new HashMap<>();

	@Reference
	private PortalUUID _portalUUID;

}