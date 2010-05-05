-- Clear the database
DELETE FROM REPORT_ENTRY;
DELETE FROM CERTIFICATE;
DELETE FROM CONTRACT;
DELETE FROM REGISTRATION;
DELETE FROM CERTIFICATEDOMAIN;
DELETE FROM USER;
DELETE FROM CONFIGURATION;
DELETE FROM CA_PARAMETERS;
DELETE FROM CA;

-- Insert the test user
INSERT INTO USER (USER_ID, LAST_NAME, FIRST_NAME, NATIONAL_REGISTER_NUMBER, IS_ADMIN) VALUES(5001, 'Puk', 'Pietje', '99123129212', 0);