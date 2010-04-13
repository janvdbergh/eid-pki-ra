/**
 * eID PKI RA Project. 
 * Copyright (C) 2010 FedICT. 
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

package be.fedict.eid.pkira.blm.model.certificatedomain;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

/**
 * @author Bram Baeyens
 *
 */
@Name(value=AllCertificateDomainQuery.NAME)
public class AllCertificateDomainQuery extends EntityQuery<CertificateDomain> {

	private static final long serialVersionUID = 2047569824505992173L;
	
	public static final String NAME = "be.fedict.eid.pkira.blm.allCertificateDomainQuery";
	
	@Override
	public String getEjbql() {
		return "select cd from CertificateDomain cd";
	}
	
	@Override
	public String getOrderColumn() {
		return "cd.name";
	}

}