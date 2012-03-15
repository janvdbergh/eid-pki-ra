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
package be.fedict.eid.pkira.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import be.fedict.eid.pkira.contracts.CertificateRevocationRequestBuilder;
import be.fedict.eid.pkira.contracts.CertificateSigningRequestBuilder;
import be.fedict.eid.pkira.contracts.EIDPKIRAContractsClient;
import be.fedict.eid.pkira.contracts.EntityBuilder;
import be.fedict.eid.pkira.contracts.XmlMarshallingException;
import be.fedict.eid.pkira.crypto.certificate.CertificateInfo;
import be.fedict.eid.pkira.crypto.certificate.CertificateParser;
import be.fedict.eid.pkira.crypto.certificate.CertificateParserImpl;
import be.fedict.eid.pkira.crypto.csr.CSRInfo;
import be.fedict.eid.pkira.crypto.csr.CSRParser;
import be.fedict.eid.pkira.crypto.csr.CSRParserImpl;
import be.fedict.eid.pkira.crypto.exception.CryptoException;
import be.fedict.eid.pkira.crypto.xmlsign.XmlDocumentSigner;
import be.fedict.eid.pkira.generated.contracts.CertificateRevocationRequestType;
import be.fedict.eid.pkira.generated.contracts.CertificateRevocationResponseType;
import be.fedict.eid.pkira.generated.contracts.CertificateSigningRequestType;
import be.fedict.eid.pkira.generated.contracts.CertificateSigningResponseType;
import be.fedict.eid.pkira.generated.contracts.CertificateTypeType;
import be.fedict.eid.pkira.generated.contracts.EntityType;
import be.fedict.eid.pkira.generated.contracts.ResponseType;
import be.fedict.eid.pkira.generated.contracts.ResultType;
import be.fedict.eid.pkira.publicws.EIDPKIRAServiceClient;

/**
 * Client for the PKIRA public web service.
 */
public class PKIRAClient {

	private final CSRParser csrParser = new CSRParserImpl();
	private final CertificateParser certificateParser = new CertificateParserImpl();
	private final EIDPKIRAContractsClient contractsClient = new EIDPKIRAContractsClient();
	private final XmlDocumentSigner xmlDocumentSigner = new XmlDocumentSigner();
	private EIDPKIRAServiceClient pkiraServiceClient = new EIDPKIRAServiceClient();

	/**
	 * Creates a certificate signing request based on the information supplied.
	 * All fields are mandatory unless indicated otherwise.
	 * 
	 * @param requestId
	 *            Optional id to give to the request. PKIRA will return this
	 *            request id in its response.
	 * @param csr
	 *            certificate signing request (CSR) in PEM format.
	 * @param certificateType
	 *            type of certificate to generate.
	 * @param operatorName
	 *            name of the operator requesting the certificate.
	 * @param operatorFunction
	 *            function of the operator requesting the certificate.
	 * @param operatorPhone
	 *            phone number of the operator requesting the certificate. This
	 *            field has to contains a valid phone number.
	 * @param validityPeriodMonths
	 *            validity period in months. Contact Fedict to the allowed
	 *            values.
	 * @param description
	 *            additional description to be added to the contract.
	 * @param legalNotice
	 *            legal notice to be added to the contract. Contact Fedict to
	 *            get the allowed value.
	 * @return the unsigned certicate signing request contract as XML document.
	 * @throws PKIRAClientException
	 *             when the contract could not be generated. This usually
	 *             indicates an error in one of the inputs.
	 */
	public String createCertificateSigningRequestContract(String requestId, String csr,
			CertificateTypeType certificateType, String operatorName, String operatorFunction, String operatorPhone,
			int validityPeriodMonths, String description, String legalNotice) throws PKIRAClientException {
		try {
			CSRInfo csrInfo = csrParser.parseCSR(csr);

			EntityType operator = new EntityBuilder().setName(operatorName).setFunction(operatorFunction)
					.setPhone(operatorPhone).toEntityType();

			CertificateSigningRequestType contractDocument = new CertificateSigningRequestBuilder(requestId)
					.setCsr(csr).setCertificateType(certificateType).setOperator(operator)
					.setValidityPeriodMonths(validityPeriodMonths)
					.setAlternativeNames(csrInfo.getSubjectAlternativeNames()).setDescription(description)
					.setDistinguishedName(csrInfo.getSubject()).setLegalNotice(legalNotice).toRequestType();

			return contractsClient.marshal(contractDocument, CertificateSigningRequestType.class);
		} catch (CryptoException e) {
			throw new PKIRAClientException("Could not parse csr.", e);
		} catch (XmlMarshallingException e) {
			throw new PKIRAClientException("Error generating xml document from input.", e);
		}
	}

	/**
	 * Creates a certificate revocation request based on the information
	 * supplied. All fields are mandatory unless indicated otherwise.
	 * 
	 * @param requestId
	 *            Optional id to give to the request. PKIRA will return this
	 *            request id in its response.
	 * @param certificate
	 *            certificate to be revoked in PEM format.
	 * @param operatorName
	 *            name of the operator requesting the certificate.
	 * @param operatorFunction
	 *            function of the operator requesting the certificate.
	 * @param operatorPhone
	 *            phone number of the operator requesting the certificate. This
	 *            field has to contains a valid phone number.
	 * @param description
	 *            additional description to be added to the contract.
	 * @param legalNotice
	 *            legal notice to be added to the contract. Contact Fedict to
	 *            get the allowed value.
	 * @return the unsigned certificate revocation request contract as XML
	 *         document.
	 * @throws PKIRAClientException
	 *             when the contract could not be generated. This usually
	 *             indicates an error in one of the inputs.
	 */
	public String createCertificateRevocationRequestContract(String requestId, String certificate, String operatorName,
			String operatorFunction, String operatorPhone, String description, String legalNotice)
			throws PKIRAClientException {
		try {
			CertificateInfo certificateInfo = certificateParser.parseCertificate(certificate);

			EntityType operator = new EntityBuilder().setName(operatorName).setFunction(operatorFunction)
					.setPhone(operatorPhone).toEntityType();
			CertificateRevocationRequestType contractDocument = new CertificateRevocationRequestBuilder(requestId)
					.setCertificate(certificate).setOperator(operator).setDescription(description)
					.setDistinguishedName(certificateInfo.getDistinguishedName())
					.setValidityStart(certificateInfo.getValidityStart())
					.setValidityEnd(certificateInfo.getValidityEnd()).setLegalNotice(legalNotice).toRequestType();

			return contractsClient.marshal(contractDocument, CertificateRevocationRequestType.class);
		} catch (CryptoException e) {
			throw new PKIRAClientException("Could not parse certificate.", e);
		} catch (XmlMarshallingException e) {
			throw new PKIRAClientException("Error generating xml document from input.", e);
		}
	}

	public String signRequestContract(String requestContract, X509Certificate certificate, PrivateKey privateKey)
			throws PKIRAClientException {
		// Parse into DOM tree
		Document doc = stringToDom(requestContract);

		// Sign it
		try {
			xmlDocumentSigner.signXMLDocument(doc, certificate, privateKey, doc.getDocumentElement(),
					doc.getDocumentElement());
		} catch (CryptoException e) {
			throw new PKIRAClientException("Could not signing contract document.", e);
		}

		// Convert back to text
		return domToString(doc);
	}

	public String signRequestContract(String requestContract, PrivateKeyEntry privateKeyEntry)
			throws PKIRAClientException {
		return signRequestContract(requestContract, (X509Certificate) privateKeyEntry.getCertificate(),
				privateKeyEntry.getPrivateKey());
	}

	/**
	 * Sends the certificate signing request to the PKI RA web service. The URL
	 * of the web service has to be set beforehand using setServiceUrl().
	 * 
	 * @param signedRequestContract
	 *            the request contract containing the signing request. This
	 *            contract has to be signed.
	 * @return the response of the web service (already unmarshalled).
	 * @throws PKIRAClientException
	 *             if an error occurred contacting the web service or decoding
	 *             the result.
	 * @see #setServiceUrl
	 */
	public CertificateSigningResponseType sendCertificateSigningRequest(String signedRequestContract)
			throws PKIRAClientException {
		try {
			String response = pkiraServiceClient.signCertificate(signedRequestContract);
			return contractsClient.unmarshal(response, CertificateSigningResponseType.class);
		} catch (XmlMarshallingException e) {
			throw new PKIRAClientException("Error unmarshalling webservice response.", e);
		} catch (WebServiceException e) {
			throw new PKIRAClientException("Error contacting web service.", e);
		}
	}

	/**
	 * Sends the certificate revocations request to the PKI RA web service. The
	 * URL of the web service has to be set beforehand using setServiceUrl().
	 * 
	 * @param signedRequestContract
	 *            the request contract containing the revocation request. This
	 *            contract has to be signed.
	 * @return the response of the web service (already unmarshalled).
	 * @throws PKIRAClientException
	 *             if an error occurred contacting the web service or decoding
	 *             the result.
	 * @see #setServiceUrl(String)
	 * @see #responseContainsError(ResponseType)
	 */
	public CertificateRevocationResponseType sendCertificateRevocationRequest(String signedRequestContract)
			throws PKIRAClientException {
		try {
			String response = pkiraServiceClient.revokeCertificate(signedRequestContract);
			return contractsClient.unmarshal(response, CertificateRevocationResponseType.class);
		} catch (XmlMarshallingException e) {
			throw new PKIRAClientException("Error unmarshalling web service.", e);
		} catch (WebServiceException e) {
			throw new PKIRAClientException("Error contacting web service.", e);
		}
	}

	/**
	 * Checks if the response from the web service contains an error.
	 * 
	 * @param response
	 *            response returned by sendCertificateSigningRequest() or
	 *            sendCertificateRevocationRequest().
	 */
	public boolean responseContainsError(ResponseType response) {
		return response == null || response.getResult() != ResultType.SUCCESS;
	}

	/**
	 * Sets the URL of the eID PKI RA Public Webservice. This value can be
	 * obtained from Fedict.
	 * 
	 * @param serviceUrl
	 *            the new url.
	 */
	public void setServiceUrl(String serviceUrl) {
		pkiraServiceClient.setServiceUrl(serviceUrl);
	}

	private String domToString(Document doc) throws PKIRAClientException {
		try {
			Source source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerException e) {
			throw new PKIRAClientException("Could not converting xml to string.", e);
		}
	}

	private Document stringToDom(String xml) throws PKIRAClientException {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			InputSource input = new InputSource();
			input.setCharacterStream(new StringReader(xml));

			Document result = documentBuilder.parse(input);
			result.getDocumentElement().normalize();
			return result;
		} catch (IOException e) {
			// Should not happen.
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new PKIRAClientException("Could not parse XML document.", e);
		} catch (ParserConfigurationException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
	}

	/**
	 * Injects the service client (used in unit testing).
	 */
	void setPKIRAServiceClient(EIDPKIRAServiceClient pkiraServiceClient) {
		this.pkiraServiceClient = pkiraServiceClient;
	}
}
