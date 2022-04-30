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

package it.smc.labs.bootcamp.liferay.security.auto.login.token.repository.model;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * It is a classic POJO whose purpose is to represent user information that
 * comes from the external system.
 *
 * @author Antonio Musarra
 */
public class ExternalUser implements Serializable {

	public Map<String, Object> getAdditionalProperties() {
		return _additionalProperties;
	}

	public Date getBirthDate() {
		return _birthDate;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getFirstName() {
		return _firstName;
	}

	public String getFiscalCode() {
		return _fiscalCode;
	}

	public String getGender() {
		return _gender;
	}

	public String getLanguageId() {
		return _languageId;
	}

	public String getLastName() {
		return _lastName;
	}

	public String getMiddleName() {
		return _middleName;
	}

	public String[] getRolesName() {
		return _rolesName;
	}

	public Integer getStatus() {
		return _status;
	}

	public String getUuid() {
		return _uuid;
	}

	public boolean isFemale() {
		return getGender().equalsIgnoreCase("F");
	}

	public boolean isMale() {
		return getGender().equalsIgnoreCase("M");
	}

	public void setAdditionalProperty(String name, Object value) {
		_additionalProperties.put(name, value);
	}

	public void setBirthDate(Date birthDate) {
		_birthDate = birthDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	public void setFirstName(String firstName) {
		_firstName = firstName;
	}

	public void setFiscalCode(String fiscalCode) {
		_fiscalCode = fiscalCode;
	}

	public void setGender(String gender) {
		_gender = gender;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	public void setLastName(String lastName) {
		_lastName = lastName;
	}

	public void setMiddleName(String middleName) {
		_middleName = middleName;
	}

	public void setRolesName(String[] rolesName) {
		_rolesName = rolesName;
	}

	public void setStatus(Integer status) {
		_status = status;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	private static final long serialVersionUID = -4964541876685923984L;

	private Map<String, Object> _additionalProperties = new HashMap<>();
	private Date _birthDate;
	private Date _createDate;
	private String _emailAddress;
	private String _firstName;
	private String _fiscalCode;
	private String _gender;
	private String _languageId;
	private String _lastName;
	private String _middleName;
	private String[] _rolesName;
	private int _status;
	private String _uuid;

}