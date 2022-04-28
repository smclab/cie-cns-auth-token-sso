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

package it.smc.labs.bootcamp.liferay.security.auto.login.token.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.security.sso.token.security.auth.TokenLocation;

/**
 * @author Antonio Musarra
 */
@ExtendedObjectClassDefinition(
	category = "sso", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = CieCnsTokenAutoLoginConfiguration.PID,
	localization = "content/Language",
	name = "cie-cns-token-auto-login-configuration-name"
)
public interface CieCnsTokenAutoLoginConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "false", description = "import-from-external-source-help",
		name = "import-from-external-source", required = false
	)
	public boolean importFromExternalSource();

	@Meta.AD(
		deflt = "X-AUTH-REMOTE-USER", description = "user-token-name-help",
		name = "user-token-name", required = false
	)
	public String userTokenName();

	@Meta.AD(
		deflt = "X-AUTH-ORIGIN", description = "origin-http-header-name-help",
		name = "origin-http-header-name", required = false
	)
	public String originHttpHeaderName();

	@Meta.AD(
		deflt = "RP_IDP_TEST_CIE|RP_IDP_TEST_CNS|",
		description = "origin-http-header-values-help",
		name = "origin-http-header-values", required = false
	)
	public String[] originHttpHeaderValues();

	@Meta.AD(
		deflt = "127.0.0.1|192.168.10.205", description = "whitelist-help",
		name = "whitelist", required = false
	)
	public String[] whitelist();

	@Meta.AD(
		deflt = "false", description = "x-forwarded-help", name = "x-forwarded",
		required = false
	)
	public boolean xForwarded();

	@Meta.AD(
		deflt = "REQUEST_HEADER", description = "token-location-help",
		name = "token-location",
		optionLabels = {
			"token-location-" + TokenLocation.REQUEST,
			"token-location-" + TokenLocation.REQUEST_HEADER
		},
		optionValues = {TokenLocation.REQUEST, TokenLocation.REQUEST_HEADER},
		required = false
	)
	public String tokenLocation();

	public final String PID =
		"it.smc.labs.bootcamp.liferay.security.auto.login.token." +
			"configuration.CieCnsTokenAutoLoginConfiguration";

}