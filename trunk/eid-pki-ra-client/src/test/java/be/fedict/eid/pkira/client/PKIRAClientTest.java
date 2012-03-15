package be.fedict.eid.pkira.client;

import static be.fedict.eid.pkira.client.TestConstants.CERTIFICATE;
import static be.fedict.eid.pkira.client.TestConstants.CERTIFICATE_REVOCATION_CONTRACT;
import static be.fedict.eid.pkira.client.TestConstants.CERTIFICATE_REVOCATION_RESPONSE;
import static be.fedict.eid.pkira.client.TestConstants.CERTIFICATE_SIGNING_CONTRACT;
import static be.fedict.eid.pkira.client.TestConstants.CERTIFICATE_SIGNING_RESPONSE;
import static be.fedict.eid.pkira.client.TestConstants.CERTIFICATE_TYPE;
import static be.fedict.eid.pkira.client.TestConstants.CSR;
import static be.fedict.eid.pkira.client.TestConstants.DESCRIPTION;
import static be.fedict.eid.pkira.client.TestConstants.LEGAL_NOTICE;
import static be.fedict.eid.pkira.client.TestConstants.OPERATOR_FUNCTION;
import static be.fedict.eid.pkira.client.TestConstants.OPERATOR_NAME;
import static be.fedict.eid.pkira.client.TestConstants.OPERATOR_PHONE;
import static be.fedict.eid.pkira.client.TestConstants.REQUEST_ID;
import static be.fedict.eid.pkira.client.TestConstants.VALIDITY_PERIOD;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.StringReader;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import be.fedict.eid.pkira.generated.contracts.CertificateRevocationResponseType;
import be.fedict.eid.pkira.generated.contracts.CertificateSigningResponseType;
import be.fedict.eid.pkira.publicws.EIDPKIRAServiceClient;

public class PKIRAClientTest {

	public static final char[] JKS_PASSWORD = "changeit".toCharArray();


	
	private static final String DUMMY_CONTRACT = "Contract";

	private final PKIRAClientImpl client = new PKIRAClientImpl();
	@Mock
	private EIDPKIRAServiceClient pkiraServiceClient;
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		client.setPKIRAServiceClient(pkiraServiceClient);
	}

	@Test
	public void testCreateCertificateRevocationRequestContract() throws Exception {
		String contract = client.createCertificateRevocationRequestContract(REQUEST_ID, CERTIFICATE, OPERATOR_NAME,
				OPERATOR_FUNCTION, OPERATOR_PHONE, DESCRIPTION, LEGAL_NOTICE);
		Diff diff = XMLUnit.compareXML(CERTIFICATE_REVOCATION_CONTRACT, contract);
		assertTrue(diff.identical(), diff.toString());
	}

	@Test
	public void testCreateCertificateSigningRequestContract() throws Exception {
		String contract = client.createCertificateSigningRequestContract(REQUEST_ID, CSR, CERTIFICATE_TYPE,
				OPERATOR_NAME, OPERATOR_FUNCTION, OPERATOR_PHONE, VALIDITY_PERIOD, DESCRIPTION, LEGAL_NOTICE);
		Diff diff = XMLUnit.compareXML(CERTIFICATE_SIGNING_CONTRACT, contract);
		assertTrue(diff.identical(), diff.toString());
	}

	@Test
	public void testSignRequestContract() throws Exception {
		// Load the keystore and get the private key
		URL url = getClass().getClassLoader().getResource("test.jks");
		KeyStore keyStore = KeyStore.getInstance("jks");
		keyStore.load(url.openStream(), JKS_PASSWORD);
		PasswordProtection passwordProtection = new PasswordProtection(JKS_PASSWORD);
		PrivateKeyEntry entry = (PrivateKeyEntry) keyStore.getEntry("test", passwordProtection);

		// Sign the document
		String signedContract = client.signRequestContract(CERTIFICATE_SIGNING_CONTRACT, entry);
		
		validateSignature(signedContract, entry.getCertificate().getPublicKey());
	}
	
	@Test
	public void testSetServiceUrl() {
		final String URL = "theUrl";
		client.setServiceUrl(URL);
		
		verify(pkiraServiceClient).setServiceUrl(URL);
	}
	
	@Test
	public void testSendCertificateSigningRequest() throws PKIRAClientException {
		doReturn(CERTIFICATE_SIGNING_RESPONSE).when(pkiraServiceClient).signCertificate(DUMMY_CONTRACT);
		
		CertificateSigningResponseType response = client.sendCertificateSigningRequest(DUMMY_CONTRACT);
		assertTrue(client.responseContainsErrors(response));
	}
	
	@Test
	public void testSendCertificateRevocationRequest() throws PKIRAClientException {
		doReturn(CERTIFICATE_REVOCATION_RESPONSE).when(pkiraServiceClient).revokeCertificate(DUMMY_CONTRACT);
		
		CertificateRevocationResponseType response = client.sendCertificateRevocationRequest(DUMMY_CONTRACT);
		assertTrue(client.responseContainsErrors(response));
	}

	private void validateSignature(String signedContract, final Key certificateKey) throws Exception {
		// Read the document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(signedContract)));

		// Get the signature element
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		assertEquals(nl.getLength(), 1);

		// Create validation context
		KeySelector keySelector = new KeySelector() {
			@Override
			public KeySelectorResult select(KeyInfo arg0, Purpose arg1, AlgorithmMethod arg2, XMLCryptoContext arg3)
					throws KeySelectorException {
				return new KeySelectorResult() {
					@Override
					public Key getKey() {
						return certificateKey;
					}
				};
			}
		};
		DOMValidateContext valContext = new DOMValidateContext(keySelector, nl.item(0));
		
		// Create signature factory and signature
		XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
		XMLSignature signature = factory.unmarshalXMLSignature(valContext);

		// Check if valid
		assertTrue(signature.validate(valContext));
	}
}
