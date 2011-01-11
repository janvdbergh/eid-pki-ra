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

package be.fedict.eid.pkira.blm.model.contracts;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

/**
 * @author Bram Baeyens
 *
 */
@Name(ContractHome.NAME)
public class ContractHome extends EntityHome<AbstractContract> {

	private static final long serialVersionUID = 4867309070918612476L;
	
	public static final String NAME = "be.fedict.eid.pkira.blm.contractHome";

}