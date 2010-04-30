package be.fedict.eid.pkira.portal.ra.certificaterequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.faces.FacesMessages;
import org.testng.Assert;
import org.testng.annotations.Test;

import be.fedict.eid.pkira.contracts.EIDPKIRAContractsClient;
import be.fedict.eid.pkira.generated.contracts.CertificateSigningResponseType;
import be.fedict.eid.pkira.generated.contracts.ResultType;
import be.fedict.eid.pkira.portal.ra.AbstractDssSigningHandlerTest;
import be.fedict.eid.pkira.publicws.EIDPKIRAServiceClient;

public class RequestContractDssSigningHandlerTest
		extends AbstractDssSigningHandlerTest<RequestContractDssSigningHandler, 
				CertificateSigningResponseType> {

	@Override
	protected void initHandler() {
		handler = new RequestContractDssSigningHandler() {

			@Override
			protected EIDPKIRAServiceClient getServiceClient() {
				return serviceClient;
			}

			@Override
			protected EIDPKIRAContractsClient getContractsClient() {
				return contractsClient;
			}

			@Override
			protected CertificateSigningResponseType unmarshall(String result) {
				return certificateResponse;
			}
			
			@Override
			protected FacesMessages getFacesMessages() {
				return facesMessages;
			}
			
			@Override
			protected HttpServletRequest getRequest(){
				return request;
			} 
		};
	}

	protected void initMocks() throws Exception {
		super.initMocks();
		certificateResponse = mock(CertificateSigningResponseType.class);
	}

	@Test
	public void successfulRequest() throws Exception {
		when(request.getParameter(SIGNATURE_STATUS_PARAMETER)).thenReturn("OK");
		when(certificateResponse.getResult()).thenReturn(ResultType.SUCCESS);
		String outcome = handler.handleDssRequest();
		Assert.assertEquals(outcome, "success");
	}

	@Test
	public void signatureNotOk() throws Exception {
		when(request.getParameter(SIGNATURE_STATUS_PARAMETER)).thenReturn(
				"NOT_OK");
		String outcome = handler.handleDssRequest();
		Assert.assertEquals(outcome, "error");
	}

	@Test
	public void backendError() throws Exception {
		when(request.getParameter(SIGNATURE_STATUS_PARAMETER)).thenReturn("OK");
		when(certificateResponse.getResult()).thenReturn(
				ResultType.BACKEND_ERROR);
		String outcome = handler.handleDssRequest();
		Assert.assertEquals(outcome, "error");
	}
}