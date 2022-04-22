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

package it.smc.labs.bootcamp.liferay.security.auto.login.token.repository;

import com.liferay.portal.kernel.exception.PortalException;

import it.smc.labs.bootcamp.liferay.security.auto.login.token.repository.model.ExternalUser;

/**
 * @author Antonio Musarra
 */
public interface DetailsUserInfo {

	public ExternalUser getUserByEmail(long companyId, String email)
		throws PortalException;

	public ExternalUser getUserByScreenName(long companyId, String screenName)
		throws PortalException;

}