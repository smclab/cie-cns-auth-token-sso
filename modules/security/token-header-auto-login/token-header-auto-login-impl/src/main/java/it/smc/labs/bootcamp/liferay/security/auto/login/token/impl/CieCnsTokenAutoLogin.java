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

package it.smc.labs.bootcamp.liferay.security.auto.login.token.impl;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.exportimport.UserImporter;
import com.liferay.portal.security.sso.token.security.auth.TokenRetriever;
import com.liferay.portal.util.PropsValues;

import it.smc.labs.bootcamp.liferay.security.auto.login.token.configuration.CieCnsTokenAutoLoginConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Participates in every unauthenticated HTTP request to Liferay Portal.
 *
 * <p>
 * If this class finds an authentication token in the HTTP request and
 * successfully identifies a Liferay Portal user using it, then this user is
 * logged in without any further challenge.
 * </p>
 *
 * @author Antonio Musarra
 */
@Component(
	configurationPid = CieCnsTokenAutoLoginConfiguration.PID,
	configurationPolicy = ConfigurationPolicy.OPTIONAL,
	service = AutoLogin.class
)
public class CieCnsTokenAutoLogin extends BaseAutoLogin {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_tokenRetrievers = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, TokenRetriever.class, "token.location");
	}

	@Deactivate
	protected void deactivate() {
		_tokenRetrievers.close();
	}

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long companyId = _portal.getCompanyId(httpServletRequest);

		CieCnsTokenAutoLoginConfiguration cieCnsTokenCompanyServiceSettings =
			_configurationProvider.getConfiguration(
				CieCnsTokenAutoLoginConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, CieCnsTokenAutoLoginConfiguration.PID));

		if (!cieCnsTokenCompanyServiceSettings.enabled()) {
			if (_log.isDebugEnabled()) {
				_log.debug("CIE/CNS Token Auto Login not enabled");
			}

			return null;
		}

		String userTokenName =
			cieCnsTokenCompanyServiceSettings.userTokenName();

		String tokenLocation =
			cieCnsTokenCompanyServiceSettings.tokenLocation();

		TokenRetriever tokenRetriever = _tokenRetrievers.getService(
			tokenLocation);

		if (_log.isDebugEnabled()) {
			_log.debug(
				String.format(
					"Looking for userToken %s in %s", userTokenName,
					tokenLocation));
		}

		if (tokenRetriever == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No token retriever found for " + tokenLocation);
			}

			return null;
		}

		String login = tokenRetriever.getLoginToken(
			httpServletRequest, userTokenName);

		if (Validator.isNull(login)) {
			if (_log.isInfoEnabled()) {
				_log.info("No login found for " + tokenLocation);
			}

			return null;
		}

		if (!_isValidOrigin(
				httpServletRequest, cieCnsTokenCompanyServiceSettings)) {

			return null;
		}

		if (!_isValidSource(
				httpServletRequest, cieCnsTokenCompanyServiceSettings)) {

			return null;
		}

		User user = _userLocalService.fetchUserByScreenName(companyId, login);

		if (user == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					String.format(
						"The user with screenName %s does not exits in the " +
							"company %s",
						login, companyId));
			}

			user = getUser(companyId, login, cieCnsTokenCompanyServiceSettings);
		}
		else {
			//TODO: Provide a possible update of the user from the external

			// source.

			if (_log.isDebugEnabled()) {
				_log.debug(
					String.format(
						"The user with screenName %s already exits in the " +
							"company %s",
						login, companyId));
			}
		}

		addRedirect(httpServletRequest);

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	protected User getUser(
			long companyId, String login,
			CieCnsTokenAutoLoginConfiguration cieCnsTokenCompanyServiceSettings)
		throws PortalException {

		User user = null;

		String authType = PrefsPropsUtil.getString(
			companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
			PropsValues.COMPANY_SECURITY_AUTH_TYPE);

		if (cieCnsTokenCompanyServiceSettings.importFromExternalSource()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					String.format(
						"Try to import user with screenName %s in the " +
							"company %s",
						login, companyId));
			}

			try {
				if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					user = _userImporter.importUser(
						companyId, StringPool.BLANK, login);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					user = _userImporter.importUser(
						companyId, login, StringPool.BLANK);
				}
				else {
					if (_log.isWarnEnabled()) {
						StringBundler sb = new StringBundler(7);

						sb.append("The property \"");
						sb.append(PropsKeys.COMPANY_SECURITY_AUTH_TYPE);
						sb.append("\" must be set to either \"");
						sb.append(CompanyConstants.AUTH_TYPE_EA);
						sb.append("\" or \"");
						sb.append(CompanyConstants.AUTH_TYPE_SN);
						sb.append("\"");

						_log.warn(sb.toString());
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to import from external source", exception);
				}
			}
		}

		if (user != null) {
			return user;
		}

		if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			user = _userLocalService.getUserByScreenName(companyId, login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			user = _userLocalService.getUserByEmailAddress(companyId, login);
		}
		else {
			if (_log.isWarnEnabled()) {
				StringBundler sb = new StringBundler(6);

				sb.append("Incompatible setting for: ");
				sb.append(PropsKeys.COMPANY_SECURITY_AUTH_TYPE);
				sb.append(". Please configure to either: ");
				sb.append(CompanyConstants.AUTH_TYPE_EA);
				sb.append(" or ");
				sb.append(CompanyConstants.AUTH_TYPE_SN);

				_log.warn(sb.toString());
			}
		}

		return user;
	}

	private boolean _isValidOrigin(
		HttpServletRequest httpServletRequest,
		CieCnsTokenAutoLoginConfiguration cieCnsTokenCompanyServiceSettings) {

		String authOriginValue = httpServletRequest.getHeader(
			cieCnsTokenCompanyServiceSettings.originHttpHeaderName());

		if (_log.isDebugEnabled()) {
			_log.debug(
				String.format(
					"Value received on the HTTP Header %s is %s",
					cieCnsTokenCompanyServiceSettings.originHttpHeaderName(),
					authOriginValue));

			_log.debug(
				String.format(
					"Value admitted for HTTP Header %s are %s",
					cieCnsTokenCompanyServiceSettings.originHttpHeaderName(),
					StringUtil.merge(
						cieCnsTokenCompanyServiceSettings.
							originHttpHeaderValues(),
						StringPool.COMMA)));
		}

		if (ArrayUtil.contains(
				cieCnsTokenCompanyServiceSettings.originHttpHeaderValues(),
				authOriginValue)) {

			return true;
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Failed to validate origin. Check the configuration.");
		}

		return false;
	}

	private boolean _isValidSource(
		HttpServletRequest httpServletRequest,
		CieCnsTokenAutoLoginConfiguration cieCnsTokenCompanyServiceSettings) {

		String[] ipWhitelist = cieCnsTokenCompanyServiceSettings.whitelist();

		if (_log.isDebugEnabled()) {
			_log.debug(
				String.format(
					"Value admitted for Remote IP/FQDN are %s. Remote " +
						"IP/FQDN is %s",
					StringUtil.merge(ipWhitelist, StringPool.COMMA),
					httpServletRequest.getRemoteHost()));
		}

		if (cieCnsTokenCompanyServiceSettings.xForwarded()) {
			String forwardedHost = _portal.getForwardedHost(httpServletRequest);

			if (_log.isDebugEnabled()) {
				_log.debug(
					String.format(
						"X-FORWARDED check enabled. Forwarded host is %s",
						forwardedHost));
			}

			if (ArrayUtil.contains(ipWhitelist, forwardedHost)) {
				return true;
			}
		}

		if (ArrayUtil.contains(
				ipWhitelist, httpServletRequest.getRemoteHost())) {

			return true;
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Failed to validate source. Check the configuration.");
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CieCnsTokenAutoLogin.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, TokenRetriever> _tokenRetrievers;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(component.name=it.smc.labs.bootcamp.liferay.security.auto.login.token.impl.exportimport.ExternalUserImporterImpl)"
	)
	private UserImporter _userImporter;

	@Reference
	private UserLocalService _userLocalService;

}