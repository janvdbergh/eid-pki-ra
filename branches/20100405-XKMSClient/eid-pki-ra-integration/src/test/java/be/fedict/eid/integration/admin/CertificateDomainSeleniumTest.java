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
package be.fedict.eid.integration.admin;

import org.testng.annotations.Test;

import be.fedict.eid.integration.BaseSeleniumTestCase;

/**
 * @author Jan Van den Bergh
 */
public class CertificateDomainSeleniumTest extends BaseSeleniumTestCase {

	private static final String DNEXPR_INVALID = "xyz";
	private static final String DNEXPR1 = "c=be,ou=test,cn=*";
	private static final String DNEXPR2 = "c=be,ou=test2,cn=*";
	private static final String NAME1 = "First Domain";
	private static final String NAME2 = "Second Domain";
	private static final String NAME3 = "Third Domain";

	@Test
	public void testCreateCertificateDomainInvalidExpression() {
		createCertificateDomain(NAME3, DNEXPR_INVALID, true, false, false);
		assertTextPresent("The distinguished name expression is not valid");
	}
	
	@Test
	public void testCreateCertificateDomainNothingChecked() {
		createCertificateDomain(NAME3, DNEXPR2, false, false, false);
		assertTextPresent("Please select at least one certificate type for this certificate domain");
	}

	@Test(dependsOnMethods = "testCreateFirstCertificateDomain")
	public void testCreateCertificateDomainOverlapping() {
		createCertificateDomain(NAME3, DNEXPR1, true, true, true);
		assertTextPresent("The distinguished name expression overlaps with an existing certificate domain");
	}

	@Test(dependsOnMethods = "testCreateFirstCertificateDomain")
	public void testCreateCertificateDomainSameName() {
		createCertificateDomain(NAME1, DNEXPR2, true, false, false);
		assertTextPresent("The name of the certificate domain is not valid");
	}

	@Test
	public void testCreateFirstCertificateDomain() {
		createCertificateDomain(NAME1, DNEXPR1, true, false, false);
		assertTextPresent("The certificate domain has been added to the database");
	}

	@Test(dependsOnMethods = "testCreateFirstCertificateDomain")
	public void testCreateSecondDomain() {
		createCertificateDomain(NAME2, DNEXPR1, false, true, true);
		assertTextPresent("The certificate domain has been added to the database");
	}

	private void createCertificateDomain(String name, String dnExpr, boolean clientCert, boolean serverCert,
			boolean codeSigningCert) {
		openAndWait(deployUrl);
		clickAndWait("header-form:certificatedomains");
		assertTextPresent("Edit certificate domain");

		getSelenium().type("certificateDetailForm:nameDecoration:name", name);
		getSelenium().type("certificateDetailForm:dnPatternDecoration:dnPattern", dnExpr);
		if (clientCert) {
			getSelenium().check("certificateDetailForm:clientCertDecoration:clientCert");
		}
		if (serverCert) {
			getSelenium().check("certificateDetailForm:serverCertDecoration:serverCert");
		}
		if (codeSigningCert) {
			getSelenium().check("certificateDetailForm:codeSigningCertDecoration:codeSigningCert");
		}
		clickAndWait("certificateDetailForm:submitButtonBox:submit");
	}
}