/**
 * eID PKI RA Project. 
 * Copyright (C) 2010-2012 FedICT. 
 * 
 * This is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License version 
 * 3.0 as published by the Free Software Foundation. 
 * 
 * This software is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/. 
 */

package be.fedict.eid.pkira.portal.certificaterequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import be.fedict.eid.pkira.crypto.csr.CSRInfo;
import be.fedict.eid.pkira.crypto.exception.CryptoException;
import be.fedict.eid.pkira.dnfilter.DistinguishedNameManager;


/**
 * @author Bram Baeyens
 * 
 */
public class CSRUploadHandlerBeanTest {

	private final CSRUploadHandlerBean handler = new CSRUploadHandlerBean();
	
	@Mock private Log log;
	@Mock private CSRUpload csrUpload;
	@Mock private FacesMessages facesMessages;
	@Mock private CSRInfo csrInfo;
	@Mock private DistinguishedNameManager distinguishedNameManager;
	private RequestContract contract;
	
	@BeforeMethod
	protected void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		handler.setLog(log);
		handler.setCsrUpload(csrUpload);
		contract = new RequestContract();
		handler.setRequestContract(contract);
		handler.setFacesMessages(facesMessages);
		handler.setDistinguishedNameManager(distinguishedNameManager);
	}	
	
	@Test
	public void uploadCertificateSigningRequestValid() throws Exception {
		when(csrUpload.extractCsrInfo()).thenReturn(csrInfo);
		when(csrInfo.getSubject()).thenReturn("testDN");
		when(csrUpload.getBase64Csr()).thenReturn("testBase64CSR");
		
		String result = handler.uploadCertificateSigningRequest();
		assertEquals("success", result);
		assertEquals("testDN", contract.getDistinguishedName());	
		assertEquals("testBase64CSR", contract.getBase64Csr());
	}
	
	@Test
	public void uploadCertificateSigningRequestInvalid() throws Exception {
		when(csrUpload.extractCsrInfo()).thenThrow(new CryptoException("Invalid CSR"));
		
		String result = handler.uploadCertificateSigningRequest();
		assertNull(result);
		verify(facesMessages).addFromResourceBundle("validator.invalid.csr");
	}
}
