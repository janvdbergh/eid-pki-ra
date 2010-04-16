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
package be.fedict.eid.pkira.blm.model.contracts;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import be.fedict.eid.pkira.blm.model.certificatedomain.CertificateDomain;

/**
 * A received contract document.
 * 
 * @author Jan Van den Bergh
 */
@Entity
@Table(name = "CONTRACT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 16)
public abstract class AbstractContract implements Serializable {

	private static final long serialVersionUID = -5082287440865809644L;

	@Id	@GeneratedValue
	@Column(name = "CONTRACT_ID", nullable=false)
	private Integer id;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "CONTRACT_DOCUMENT", nullable=false)
	private String contractDocument;
	
	@Column(name = "SUBJECT", nullable=false)
	private String subject;
	
	@Column(name = "REQUESTER", nullable=false)
	private String requester;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="CERTIFICATE_DOMAIN_ID", nullable=false)
	private CertificateDomain certificateDomain;

	public String getContractDocument() {
		return contractDocument;
	}

	public void setContractDocument(String contractDocument) {
		this.contractDocument = contractDocument;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public Integer getId() {
		return id;
	}

	
	public CertificateDomain getCertificateDomain() {
		return certificateDomain;
	}

	
	public void setCertificateDomain(CertificateDomain certificateDomain) {
		this.certificateDomain = certificateDomain;
	}
}
