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
package be.fedict.eid.pkira.blm.model.contracthandler;

import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import be.fedict.eid.pkira.blm.model.domain.Certificate;
import be.fedict.eid.pkira.blm.model.domain.CertificateSigningContract;
import be.fedict.eid.pkira.blm.model.domain.CertificateType;
import be.fedict.eid.pkira.blm.model.eiddss.SignatureVerifier;
import be.fedict.eid.pkira.blm.model.validation.FieldValidator;
import be.fedict.eid.pkira.blm.model.xkms.XKMSService;
import be.fedict.eid.pkira.contracts.AbstractResponseBuilder;
import be.fedict.eid.pkira.contracts.CertificateRevocationResponseBuilder;
import be.fedict.eid.pkira.contracts.CertificateSigningResponseBuilder;
import be.fedict.eid.pkira.crypto.CertificateInfo;
import be.fedict.eid.pkira.crypto.CertificateParser;
import be.fedict.eid.pkira.crypto.CryptoException;
import be.fedict.eid.pkira.generated.contracts.CertificateRevocationRequestType;
import be.fedict.eid.pkira.generated.contracts.CertificateRevocationResponseType;
import be.fedict.eid.pkira.generated.contracts.CertificateSigningRequestType;
import be.fedict.eid.pkira.generated.contracts.CertificateSigningResponseType;
import be.fedict.eid.pkira.generated.contracts.CertificateTypeType;
import be.fedict.eid.pkira.generated.contracts.RequestType;
import be.fedict.eid.pkira.generated.contracts.ResultType;

/**
 * Contract handler bean which is used to handle incoming contracts and send a
 * response back.
 * 
 * @author Jan Van den Bergh
 */
@Stateless
@Name(ContractHandler.NAME)
public class ContractHandlerBean implements ContractHandler {

	@EJB
	private ContractParser contractParser;

	@EJB
	private FieldValidator fieldValidator;

	@EJB
	private SignatureVerifier signatureVerifier;
	
	@EJB
	private XKMSService xkmsService;
	
	@In(value=CertificateParser.NAME)
	private CertificateParser certificateParser;	
	
	@Resource
	private EntityManager entityManager;

	/*
	 * (non-Javadoc)
	 * @see
	 * be.fedict.eid.blm.model.ContractHandler#revokeCertificate(java.lang.String
	 * )
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String revokeCertificate(String requestMsg) {
		CertificateRevocationResponseBuilder responseBuilder = new CertificateRevocationResponseBuilder();
		CertificateRevocationRequestType request = null;
		try {
			// Parse the request
			request = contractParser.unmarshalRequestMessage(requestMsg, CertificateRevocationRequestType.class);

			// Validate the fields
			fieldValidator.validateContract(request);

			// Validate the signature
			String signer = signatureVerifier.verifySignature(requestMsg);

			// TODO business logic
			// Return not implemented message
			fillResponseFromRequest(responseBuilder, request, ResultType.GENERAL_FAILURE, "Not implemented");
		} catch (ContractHandlerBeanException e) {
			fillResponseFromRequest(responseBuilder, request, e.getResultType(), e.getMessage());
		}

		return contractParser.marshalResponseMessage(responseBuilder.toResponseType(),
				CertificateRevocationResponseType.class);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * be.fedict.eid.blm.model.ContractHandler#signCertificate(java.lang.String)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String signCertificate(String requestMsg) {
		CertificateSigningResponseBuilder responseBuilder = new CertificateSigningResponseBuilder();
		CertificateSigningRequestType request = null;
		try {
			// Parse the request
			request = contractParser.unmarshalRequestMessage(requestMsg, CertificateSigningRequestType.class);

			// Validate the fields
			fieldValidator.validateContract(request);

			// Validate the signature
			String signer = signatureVerifier.verifySignature(requestMsg);

			// Persist the contract
			CertificateSigningContract contract = new CertificateSigningContract();
			contract.setRequester(signer);
			contract.setCertificateType(mapCertificateType(request.getCertificateType()));
			contract.setContractDocument(requestMsg);
			contract.setSubject(request.getDistinguishedName());
			contract.setValidityPeriodMonths(request.getValidityPeriodMonths().intValue());
			entityManager.persist(contract);
			
			// Call XKMS
			String certificateAsPem = xkmsService.sign(request.getCSR());
			if (certificateAsPem==null) {
				throw new ContractHandlerBeanException(ResultType.BACKEND_ERROR, "Error contacting the backend service.");
			}
			
			// Persist the certificate
			CertificateInfo certificateInfo;
			try {
				certificateInfo = certificateParser.parseCertificate(certificateAsPem);
			} catch (CryptoException e) {
				// TODO log problem with certificate received from CA
				throw new ContractHandlerBeanException(ResultType.GENERAL_FAILURE, "Error processing received certificate.");
			}
			Certificate certificate = new Certificate(certificateAsPem, certificateInfo, signer);
			entityManager.persist(certificate);

			// TODO send mail containing the certificate
			
			// All ok
			fillResponseFromRequest(responseBuilder, request, ResultType.SUCCESS, "Not implemented");
		} catch (ContractHandlerBeanException e) {
			fillResponseFromRequest(responseBuilder, request, e.getResultType(), e.getMessage());
		}

		return contractParser.marshalResponseMessage(responseBuilder.toResponseType(),
				CertificateSigningResponseType.class);
	}

	/**
	 * @param certificateType
	 * @return
	 */
	private CertificateType mapCertificateType(CertificateTypeType certificateType) {
		switch (certificateType) {
		case CLIENT:
			return CertificateType.ClientCertificate;
		case SERVER:
			return CertificateType.ServerCertificate;
		case CODE:
			return CertificateType.CodeSigningCertificate;
		}
		throw new RuntimeException("Unmapped certificate type: " + certificateType);
	}

	/**
	 * Fills the values in the response, including the request id (when
	 * available).
	 */
	protected void fillResponseFromRequest(AbstractResponseBuilder<?> responseBuilder, RequestType request,
			ResultType resultType, String resultMessage) {
		if (request != null) {
			responseBuilder.setRequestId(request.getRequestId());
		}
		responseBuilder.setResult(resultType);
		responseBuilder.setResultMessage(resultMessage);
	}

	/**
	 * Generates a unique response ID.
	 */
	protected String generateResponseId() {
		return UUID.randomUUID().toString();
	}

	
	protected void setContractParser(ContractParser contractParser) {
		this.contractParser = contractParser;
	}

	
	protected void setFieldValidator(FieldValidator fieldValidator) {
		this.fieldValidator = fieldValidator;
	}

	
	protected void setSignatureVerifier(SignatureVerifier signatureVerifier) {
		this.signatureVerifier = signatureVerifier;
	}

	
	protected void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}