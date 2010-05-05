-- Users
INSERT INTO USER (USER_ID, LAST_NAME, FIRST_NAME, NATIONAL_REGISTER_NUMBER, IS_ADMIN) VALUES(2001, 'Puk', 'Pietje', '99123129212', 0);
INSERT INTO USER (USER_ID, LAST_NAME, FIRST_NAME, NATIONAL_REGISTER_NUMBER, IS_ADMIN) VALUES (2002, 'Baeyens', 'Bram', '75033022781', 1);
	
-- Certificate authorities
INSERT INTO CA(CA_ID, NAME, XKMS_URL, LEGAL_NOTICE) VALUES (5001, 'CertiPost CA', 'http://xkms-url.be/xkms', 'Test Legal Notice');

-- Certificate domains
INSERT INTO CertificateDomain (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, CA_ID) VALUES (1001, 'eHealth Client Certificates', 'c=be,ou=eHealth,uid=*', 1, 0, 0, 5001);
INSERT INTO CertificateDomain (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, CA_ID) VALUES (1002, 'eHealth Server Certificates', 'c=be,ou=eHealth,cn=*', 0, 1, 0, 5001);
INSERT INTO CertificateDomain (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, CA_ID) VALUES (1003, 'eHealth Code Signing Certificates', 'c=be,ou=eHealth,uid=*', 0, 0, 1, 5001);
INSERT INTO CertificateDomain (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, CA_ID) VALUES (1004, 'Test', 'O=ACA,C=BE', 1, 1, 1, 5001);

-- Registrations
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3001, 'NEW', 'p.puk@aca-it.be', 2001, 1001);
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3002, 'APPROVED', 'p.puk@aca-it.be', 2001, 1002);

-- Configuration
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2001, 'MAIL_SERVER', 'test');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2002, 'MAIL_PORT', '1111');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2003, 'NOTIFICATION_MAIL_DAYS', '14');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2004, 'VALIDITY_PERIODS', '6,12,15,24,36,120');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2005, 'DSS_WS_LOCATION', 'http://localhost:8080/dds');
