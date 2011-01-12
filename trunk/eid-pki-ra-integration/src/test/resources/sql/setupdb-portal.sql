-- Clear the database
DELETE FROM REPORT_ENTRY;
DELETE FROM TRIGGERHANDLES;
DELETE FROM CERTIFICATE;
DELETE FROM CONTRACT;
DELETE FROM REGISTRATION;
DELETE FROM CERTIFICATE_DOMAIN;
DELETE FROM USER;
DELETE FROM CONFIGURATION;
DELETE FROM CA_PARAMETERS;
DELETE FROM CA;

-- Insert the test user
INSERT INTO USER (USER_ID, LAST_NAME, FIRST_NAME, NATIONAL_REGISTER_NUMBER, IS_ADMIN) VALUES(5001, 'User', 'Test', '99999999999', 1);
INSERT INTO USER (USER_ID, LAST_NAME, FIRST_NAME, NATIONAL_REGISTER_NUMBER, IS_ADMIN) VALUES(5002, 'AAA Admin', 'Test', '88888888888', 1);
INSERT INTO USER (USER_ID, LAST_NAME, FIRST_NAME, NATIONAL_REGISTER_NUMBER, IS_ADMIN) VALUES(5003, 'AAB Not Admin', 'Test', '77777777777', 0);
	
-- Certificate authorities
INSERT INTO CA(CA_ID, NAME, XKMS_URL, LEGAL_NOTICE) VALUES (5001, 'CertiPost CA', 'http://xkms-url.be/xkms', 'Test legal notice');

-- Certificate domains
INSERT INTO CERTIFICATE_DOMAIN (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, PERSONALCERT, CA_ID) VALUES (1001, 'eHealth Client Certificates', 'c=be,ou=eHealth,uid=*', 1, 0, 0, 0, 5001);
INSERT INTO CERTIFICATE_DOMAIN (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, PERSONALCERT, CA_ID) VALUES (1002, 'eHealth Server Certificates', 'c=be,ou=eHealth,cn=*', 0, 1, 0, 0, 5001);
INSERT INTO CERTIFICATE_DOMAIN (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, PERSONALCERT, CA_ID) VALUES (1003, 'eHealth Code Signing Certificates', 'c=be,ou=eHealth,uid=*', 0, 0, 1, 0, 5001);
INSERT INTO CERTIFICATE_DOMAIN (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, PERSONALCERT, CA_ID) VALUES (1005, 'eHealth Personal Certificates', 'c=be,ou=eHealth,uid=*', 0, 0, 0, 1, 5001);
INSERT INTO CERTIFICATE_DOMAIN (CERTIFICATE_DOMAIN_ID, CERTIFICATE_DOMAIN_NAME, DN_EXPRESSION, CLIENTCERT, SERVERCERT, CODECERT, PERSONALCERT, CA_ID) VALUES (1004, 'Test', 'O=ACA,C=BE', 1, 1, 1, 1, 5001);

-- Configuration
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2001, 'MAIL_SERVER', 'mail.aca-it.be');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2002, 'MAIL_PORT', '25');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2003, 'NOTIFICATION_MAIL_MINUTES', 7);
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2004, 'VALIDITY_PERIODS', 15);
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2005, 'DSS_SERVLET', 'https://www.e-contract.be/eid-dss/dss-post-entry');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2006, 'DSS_WS_CLIENT', 'http://www.e-contract.be/eid-dss-ws/dss');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2007, 'IDP_SERVLET', 'https://www.e-contract.be/eid-idp-sp/saml-request');
INSERT INTO CONFIGURATION(ENTRY_ID, ENTRY_KEY, ENTRY_VALUE) VALUES(2008, 'IDP_DESTINATION', 'https://www.e-contract.be/eid-idp/protocol/saml2');

-- Registration
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3001, 'NEW', 'p.puk@aca-it.be', 5002, 1001);
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3002, 'APPROVED', 'p.puk@aca-it.be', 5002, 1002);
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3003, 'APPROVED', 'p.puk@aca-it.be', 5001, 1001);
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3004, 'APPROVED', 'p.puk@aca-it.be', 5001, 1002);
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3005, 'APPROVED', 'p.puk@aca-it.be', 5001, 1003);
INSERT INTO REGISTRATION(REGISTRATION_ID, REGISTRATION_STATUS, EMAIL, FK_REQUESTER_ID, FK_CERTIFICATE_DOMAIN_ID) VALUES(3006, 'APPROVED', 'p.puk@aca-it.be', 5001, 1004);

-- Certificate signing contract
INSERT INTO CONTRACT (CONTRACT_ID, CONTRACT_DOCUMENT, SUBJECT, REQUESTER, CERTIFICATE_DOMAIN_ID, CREATION_DATE, VALIDITY, CERTIFICATE_TYPE, TYPE) VALUES (1001, '<?xml version="1.0" encoding="UTF-8"?><contract>Server signing contract</contract>', 'c=be,ou=eHealth,cn=*', 'Test User', 1002, '2010-01-01', 15, 'SERVER', 'Sign');
INSERT INTO CONTRACT (CONTRACT_ID, CONTRACT_DOCUMENT, SUBJECT, REQUESTER, CERTIFICATE_DOMAIN_ID, CREATION_DATE, VALIDITY, CERTIFICATE_TYPE, TYPE) VALUES (1002, '<?xml version="1.0" encoding="UTF-8"?><contract>Code signing contract</contract>', 'c=be,ou=eHealth,cn=*', 'Test User', 1002, '2010-03-01', 15, 'CODE', 'Sign');
INSERT INTO CONTRACT (CONTRACT_ID, CONTRACT_DOCUMENT, SUBJECT, REQUESTER, CERTIFICATE_DOMAIN_ID, CREATION_DATE, VALIDITY, CERTIFICATE_TYPE, TYPE) VALUES (1003, '<?xml version="1.0" encoding="UTF-8"?><contract>Client signing contract</contract>', 'c=be,ou=eHealth,cn=*', 'Test User', 1002, '2010-05-01', 15, 'CLIENT', 'Sign');

-- Certificate revocation contract
INSERT INTO CONTRACT (CONTRACT_ID, CONTRACT_DOCUMENT, SUBJECT, REQUESTER, CERTIFICATE_DOMAIN_ID, CREATION_DATE, START_DATE, END_DATE, TYPE) VALUES (2001, '<?xml version="1.0" encoding="UTF-8"?><contract>Server revocation contract</contract>', 'c=be,ou=eHealth,uid=*', 'Test User', 1003, '2010-02-01', '2010-02-01', '2011-08-01', 'Revoke');
INSERT INTO CONTRACT (CONTRACT_ID, CONTRACT_DOCUMENT, SUBJECT, REQUESTER, CERTIFICATE_DOMAIN_ID, CREATION_DATE, START_DATE, END_DATE, TYPE) VALUES (2002, '<?xml version="1.0" encoding="UTF-8"?><contract>Code revocation signing contract</contract>', 'c=be,ou=eHealth,uid=*', 'Test User', 1003, '2010-04-01', '2010-04-01', '2011-10-01', 'Revoke');
INSERT INTO CONTRACT (CONTRACT_ID, CONTRACT_DOCUMENT, SUBJECT, REQUESTER, CERTIFICATE_DOMAIN_ID, CREATION_DATE, START_DATE, END_DATE, TYPE) VALUES (2003, '<?xml version="1.0" encoding="UTF-8"?><contract>Client revocation contract</contract>', 'c=be,ou=eHealth,uid=*', 'Test User', 1003, '2010-06-01', '2010-06-01', '2011-12-01', 'Revoke');

-- Reports
INSERT INTO REPORT_ENTRY (REPORT_ENTRY_ID, CERTIFICATE_AUTHORITY_NAME, CERTIFICATE_DOMAIN_NAME, CONTRACT_TYPE, LOG_TIME, MONTH, SUCCESS, REQUESTER, SUBJECT) VALUES (9001, 'CertiPost CA', 'Test',  'REQUEST', '2010-04-23 15:00:00', '2010-04', true, 'Pietje Puk', 'c=be,ou=test');
INSERT INTO REPORT_ENTRY (REPORT_ENTRY_ID, CERTIFICATE_AUTHORITY_NAME, CERTIFICATE_DOMAIN_NAME, CONTRACT_TYPE, LOG_TIME, MONTH, SUCCESS, REQUESTER, SUBJECT) VALUES (9002, 'CertiPost CA', 'Test',  'REQUEST', '2010-04-23 15:05:00', '2010-04', false, 'Pietje Puk', 'c=be,ou=test');
INSERT INTO REPORT_ENTRY (REPORT_ENTRY_ID, CERTIFICATE_AUTHORITY_NAME, CERTIFICATE_DOMAIN_NAME, CONTRACT_TYPE, LOG_TIME, MONTH, SUCCESS, REQUESTER, SUBJECT) VALUES (9003, 'CertiPost CA', 'Test',  'REQUEST', '2010-05-03 15:05:00', '2010-05', true, 'Pietje Puk', 'c=be,ou=test');
