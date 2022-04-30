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

package it.smc.labs.bootcamp.liferay.security.auto.login.token.impl.settings.definition;

import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;

import it.smc.labs.bootcamp.liferay.security.auto.login.token.configuration.CieCnsTokenAutoLoginConfiguration;

import org.osgi.service.component.annotations.Component;

/**
 * Component for registration of the application configuration, registration
 * required to be able to read the configuration through the
 * {@link com.liferay.portal.kernel.module.configuration.ConfigurationProvider}
 * and in the right scope.
 *
 * @author Antonio Musarra
 */
@Component(service = ConfigurationBeanDeclaration.class)
public class CieCnsTokenAutoLoginCompanyServiceConfigurationBeanDeclaration
	implements ConfigurationBeanDeclaration {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CieCnsTokenAutoLoginConfiguration.class;
	}

}