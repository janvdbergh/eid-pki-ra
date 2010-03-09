/*
 * eID PKI RA Project.
 * Copyright (C) 2010 FedICT.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see
 * http://www.gnu.org/licenses/.
 */
package be.fedict.eid.pkira.contracts;

import static be.fedict.eid.pkira.contracts.util.JAXBUtil.createXmlGregorianCalendar;
import static be.fedict.eid.pkira.contracts.util.JAXBUtil.getObjectFactory;

import java.util.Date;

import be.fedict.eid.pkira.generated.contracts.CertificateRevocationRequestType;

/**
 * Request builder to build certificate revocation contracts.
 * 
 * @author Jan Van den Bergh
 */
public class CertificateRevocationRequestBuilder extends AbstractRequestBuilder<CertificateRevocationRequestBuilder> {

	private String certificate;	
	private String distinguishedName;
	private Date endDate;
	private Date startDate;

	/**
	 * Creates a certificate revocation request builder setting a random id.
	 */
	public CertificateRevocationRequestBuilder() {
		super();
	}

	/**
	 * Creates a certificate revocation request builder setting the specified request id.
	 */
	public CertificateRevocationRequestBuilder(String requestId) {
		super(requestId);
	}

	public CertificateRevocationRequestBuilder setCertificate(String certificate) {
		this.certificate = certificate;
		return this;
	}

	public CertificateRevocationRequestBuilder setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
		return this;
	}

	public CertificateRevocationRequestBuilder setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public CertificateRevocationRequestBuilder setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public CertificateRevocationRequestType toRequestType() {
		CertificateRevocationRequestType requestType = getObjectFactory().createCertificateRevocationRequestType();		
		super.fillInRequestType(requestType);
		
		requestType.setCertificate(certificate);
		requestType.setDistinguishedName(distinguishedName);
		requestType.setEndDate(createXmlGregorianCalendar(endDate));
		requestType.setStartDate(createXmlGregorianCalendar(startDate));

		return requestType;
	}	
}