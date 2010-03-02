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
package be.fedict.eid.blm.model.validation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fedict.eid.blm.model.contracthandler.ContractHandlerBeanException;
import be.fedict.eid.pkira.contracts.CertificateSigningRequestBuilder;
import be.fedict.eid.pkira.contracts.EntityBuilder;
import be.fedict.eid.pkira.contracts.util.JAXBUtil;
import be.fedict.eid.pkira.crypto.CSRInfo;
import be.fedict.eid.pkira.crypto.CSRParser;
import be.fedict.eid.pkira.crypto.CryptoException;
import be.fedict.eid.pkira.generated.contracts.CertificateSigningRequestType;
import be.fedict.eid.pkira.generated.contracts.CertificateTypeType;
import be.fedict.eid.pkira.generated.contracts.EntityType;

public class FieldValidatorBeanTest {

	// TODO test for validation of revocation requests

	private static final CertificateTypeType INVALID_CERTIFICATE_TYPE = null;
	private static final String INVALID_CSR="Invalid CSR";
	private static final String INVALID_DESCRIPTION = "";
	private static final String INVALID_DN = "";
	private static final String INVALID_EMAIL = "j.vandenbergh@aca-it@be";
	private static final String INVALID_FUNCTION = "";
	private static final String INVALID_LEGAL_NOTICE = "";
	private static final String INVALID_NAME = null;
	private static final String INVALID_PHONE = "abcde";
	private static final int INVALID_VALIDITY_PERIOD = 23;
	private static final CertificateTypeType VALID_CERTIFICATE_TYPE = CertificateTypeType.SERVER;
	private static final String VALID_CSR="Valid CSR";
	private static final String VALID_DESCRIPTION = "New certificate";
	private static final String VALID_DN = "C=BE,ST=Limburg,L=Hasselt,O=ACA,OU=test,CN=Jan Van den Bergh";
	private static final String VALID_EMAIL = "j.vandenbergh@aca-it.be";
	private static final String VALID_FUNCTION = "Operator";
	private static final String VALID_LEGAL_NOTICE = "Legal notice";
	private static final String VALID_NAME = "Jan Van den Bergh";
	private static final String VALID_PHONE = "+32 3 123.456";
	private static final String VALID_REQUEST_ID = "REQUEST_ID";
	private static final int VALID_VALIDITY_PERIOD = 15;

	private FieldValidatorBean bean;
	private CSRParser csrParser;
	private List<String> messages;

	@BeforeMethod
	public void setup() throws CryptoException {
		csrParser = mock(CSRParser.class);
		when(csrParser.parseCSR(VALID_CSR)).thenReturn(new CSRInfo(VALID_DN));
		when(csrParser.parseCSR(INVALID_CSR)).thenThrow(new CryptoException());
		
		bean = new FieldValidatorBean();		
		bean.setCSRParser(csrParser);
		
		messages = new ArrayList<String>();
	}

	@Test
	public void testValidateCertificateSigningRequest() throws ContractHandlerBeanException {		
		CertificateSigningRequestType contract = createValidCertificateSigningRequest();
		bean.validateContract(contract);
	}

	@Test
	public void testValidateCertificateSigningRequestInvalidDN() {
		CertificateSigningRequestType contract = createValidCertificateSigningRequest();
		contract.setDistinguishedName(VALID_DN + ",test=test");
		try {
			bean.validateContract(contract);
		} catch (ContractHandlerBeanException e) {
			assertMessages(1, e.getMessages());
		}
	}

	@Test
	public void testValidateCertificateSigningRequestNull() throws ContractHandlerBeanException {
		try {
			bean.validateContract((CertificateSigningRequestType) null);
		} catch (ContractHandlerBeanException e) {
			assertMessages(1, e.getMessages());
		}
	}

	@Test
	public void testValidateInvalidCertificateSigningRequest() {
		try {
			bean.validateContract(createInvalidCertificateSigningRequest());
		} catch (ContractHandlerBeanException e) {
			assertMessages(10, e.getMessages());
		}
	}

	@Test
	public void testValidateInvalidOperator() {
		EntityType operator = createInvalidOperator();
		bean.validateOperator(operator, messages);
		assertMessages(4);
	}

	@Test
	public void testValidateNullOperator() {
		bean.validateOperator(null, messages);
		assertMessages(1);
	}

	@Test
	public void testValidateOperator() {
		EntityType operator = createValidOperator();
		bean.validateOperator(operator, messages);
		assertMessages(0);
	}

	private void assertMessages(int count) {
		assertMessages(count, messages);
	}

	private void assertMessages(int count, List<String> messages) {
		System.out.println("Messages: " + messages);
		assertEquals(messages.size(), count, "Expected " + count + " validation errors, but found " + messages.size()
				+ ": " + messages);
	}

	private CertificateSigningRequestType createInvalidCertificateSigningRequest() {
		CertificateSigningRequestType request = new CertificateSigningRequestBuilder(VALID_REQUEST_ID)
				.setCertificateType(INVALID_CERTIFICATE_TYPE).setCsr(INVALID_CSR).setDescription(INVALID_DESCRIPTION)
				.setDistinguishedName(INVALID_DN).setLegalNotice(INVALID_LEGAL_NOTICE).setOperator(
						createInvalidOperator()).setValidityPeriodMonths(INVALID_VALIDITY_PERIOD).toRequestType();
		request.setSignature(JAXBUtil.getObjectFactory().createSignatureType());
		return request;
	}

	private EntityType createInvalidOperator() {
		return new EntityBuilder().setEmail(INVALID_EMAIL).setPhone(INVALID_PHONE).setName(INVALID_NAME).setFunction(
				INVALID_FUNCTION).toEntityType();
	}

	private CertificateSigningRequestType createValidCertificateSigningRequest() {
		CertificateSigningRequestType request = new CertificateSigningRequestBuilder(VALID_REQUEST_ID)
				.setCertificateType(VALID_CERTIFICATE_TYPE).setCsr(VALID_CSR).setDescription(VALID_DESCRIPTION)
				.setDistinguishedName(VALID_DN).setLegalNotice(VALID_LEGAL_NOTICE).setOperator(createValidOperator())
				.setValidityPeriodMonths(VALID_VALIDITY_PERIOD).toRequestType();
		request.setSignature(JAXBUtil.getObjectFactory().createSignatureType());
		return request;
	}

	private EntityType createValidOperator() {
		return new EntityBuilder().setEmail(VALID_EMAIL).setPhone(VALID_PHONE).setName(VALID_NAME).setFunction(
				VALID_FUNCTION).toEntityType();
	}
}
