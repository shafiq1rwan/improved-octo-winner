drop database ecpos_manager;

create database ecpos_manager;

use ecpos_manager;

CREATE TABLE group_category (
	id BIGINT PRIMARY KEY NOT NULL,
	group_category_name NVARCHAR(50) NOT NULL UNIQUE,
	created_date DATETIME NOT NULL
);

CREATE TABLE category (
	id bigInt PRIMARY KEY NOT NULL,
	group_category_id bigInt NOT NULL,
	category_name nvarchar(150) NOT NULL UNIQUE,
	category_description nvarchar(255),
	category_image_path text,
	category_sequence INT,
	is_active bit DEFAULT 1,
	created_date DATETIME NOT NULL
);

CREATE TABLE menu_item (
	id BIGINT PRIMARY KEY NOT NULL,
	backend_id NVARCHAR(50) NOT NULL UNIQUE,
	modifier_group_id BIGINT,
	menu_item_name NVARCHAR(150) NOT NULL,
    menu_item_alt_name NVARCHAR(50),
    menu_item_barcode NVARCHAR(100) NULL UNIQUE,
	menu_item_description NVARCHAR(255),
	menu_item_image_path text,
	menu_item_base_price DECIMAL(10,2) DEFAULT 0.00,
	menu_item_type INT DEFAULT 0, 
	is_taxable BIT DEFAULT 0,
	is_discountable BIT DEFAULT 0,
	is_active BIT DEFAULT 1,
	created_date DATETIME NOT NULL
);

CREATE TABLE category_menu_item (
	category_id BIGINT NOT NULL,
	menu_item_id BIGINT NOT NULL,
	category_menu_item_sequence INT NOT NULL
);

CREATE TABLE menu_item_group (
	id BIGINT PRIMARY KEY NOT NULL,
	menu_item_group_name NVARCHAR(150) NOT NULL UNIQUE,
	is_active BIT DEFAULT 1,
	created_date DATETIME NOT NULL
);

CREATE TABLE menu_item_group_sequence (
	menu_item_group_id BigInt,
	menu_item_id BigInt,
	menu_item_group_sequence INT NOT NULL	
);

CREATE TABLE modifier_group (
	id BIGINT PRIMARY KEY NOT NULL,
	modifier_group_name NVARCHAR(100) NOT NULL UNIQUE,
	is_active BIT DEFAULT 1,
	created_date DATETIME NOT NULL
);

CREATE TABLE menu_item_modifier_group (
	menu_item_id BIGINT,
	modifier_group_id BIGINT,
	menu_item_modifier_group_sequence INT
);

CREATE TABLE modifier_item_sequence (
	modifier_group_id BIGINT NOT NULL,
	menu_item_id BIGINT NOT NULL,
	modifier_item_sequence INT
);

CREATE TABLE combo_detail (
	id BIGINT PRIMARY KEY NOT NULL,
	menu_item_id BIGINT NOT NULL,
	combo_detail_name NVARCHAR(50) NOT NULL,
	combo_detail_quantity INT DEFAULT 0,
	combo_detail_sequence INT NOT NULL,
	created_date DATETIME NOT NULL
);

CREATE TABLE combo_item_detail (
	id BIGINT PRIMARY KEY NOT NULL,
	combo_detail_id BIGINT NOT NULL,
	menu_item_id BIGINT,
	menu_item_group_id BIGINT,
	combo_item_detail_sequence INT NOT NULL,
	created_date DATETIME NOT NULL
);

CREATE TABLE store (
		id BIGINT PRIMARY KEY NOT NULL,
		store_type_id BIGINT DEFAULT 0,
		backend_id NVARCHAR(50) NOT NULL UNIQUE,
		store_name NVARCHAR(150) NOT NULL UNIQUE,
		store_logo_path text,
		store_address NVARCHAR(150),
		store_longitude DECIMAL(15,8),
		store_latitude DECIMAL(15,8),
		store_country NVARCHAR(100),
		store_currency NVARCHAR(50),
		store_start_operating_time time NOT NULL,
		store_end_operating_time time NOT NULL,
		store_contact_person VARCHAR(150) NOT NULL,
		store_contact_hp_number VARCHAR(50) NOT NULL,
		store_contact_email VARCHAR(150) NOT NULL,
		last_update_date datetime,
		is_publish BIT DEFAULT 0,
        byod_payment_delay_id BIGINT DEFAULT 0,
		kiosk_payment_delay_id BIGINT DEFAULT 0,
        ecpos_takeaway_detail_flag BIT,
		login_type_id BIGINT,
		login_switch_flag BIT,
		created_date DATETIME NOT NULL
);

CREATE TABLE store_db_sync (
		sync_date datetime not null
);

CREATE TABLE table_setting 
(
		id BIGINT PRIMARY KEY NOT NULL,
		table_name NVARCHAR(150) NOT NULL,
		status_lookup_id BIGINT,
		created_date DATETIME NOT NULL,
		last_update_date DATETIME
);

CREATE TABLE status_lookup
(
		id INT UNIQUE NOT NULL, 
		name NVARCHAR(50) NOT NULL UNIQUE 
);

CREATE TABLE staff  (
		id BIGINT PRIMARY KEY NOT NULL,
		staff_name NVARCHAR(150) NOT NULL,
		staff_username NVARCHAR(100) NOT NULL UNIQUE,
		staff_password NVARCHAR(200) NOT NULL,
		staff_role INT NOT NULL,
		staff_contact_hp_number NVARCHAR(50) NOT NULL,
		staff_contact_email VARCHAR(320) NOT NULL,
		is_active BIT DEFAULT 1 NOT NULL,
		created_date DATETIME NOT NULL,
		last_update_date DATETIME
);

CREATE TABLE role_lookup
(
	id BIGINT PRIMARY KEY NOT NULL,
	role_name NVARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO role_lookup VALUES (1, 'Admin'),(2, 'Store Manager');

CREATE TABLE tax_charge
(
	id BIGINT PRIMARY KEY NOT NULL,
	tax_charge_name NVARCHAR(100) NOT NULL,
	rate INT DEFAULT 0,
	charge_type INT DEFAULT 1,
	is_active BIT DEFAULT 0,
	created_date DATETIME NOT NULL
);

CREATE TABLE charge_type_lookup
(
	charge_type_number INT UNIQUE NOT NULL, 
	charge_type_name NVARCHAR(50) NOT NULL
);

CREATE TABLE store_type_lookup
(
	id INT UNIQUE NOT NULL,
	store_type_name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE payment_delay_lookup
(
	id INT UNIQUE NOT NULL,
	payment_delay_name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE login_type_lookup
(
	id INT UNIQUE NOT NULL,
	login_type_name NVARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO store_type_lookup VALUES (1, 'Retail'),(2, 'F&B');
INSERT INTO payment_delay_lookup VALUES (1, 'Pay Now/Later'), (2, 'Pay Now'), (3, 'Pay Later');
INSERT INTO charge_type_lookup VALUES (1, 'Total Tax'),(2, 'Overall Tax');
INSERT INTO login_type_lookup VALUES (1, 'Username & Password'), (2, 'Scan QR');

CREATE TABLE menu_item_type_lookup (
	menu_item_type_number INT NOT NULL UNIQUE,
	menu_item_type_name NVARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO menu_item_type_lookup VALUES (0,'A La Carte');
INSERT INTO menu_item_type_lookup VALUES (1,'Combo');
INSERT INTO menu_item_type_lookup VALUES (2,'Modifier');

create table `master` (
	`type` varchar(255) NOT NULL,
	`count` int NOT NULL,
	`updated_date` datetime NOT NULL
);

create table `check_status` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `device_type` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `order_type` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `check` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`check_number` bigint (20) NOT NULL,
    `staff_id` bigint (20) NULL,
	`order_type` bigint (20) NOT NULL,
    `table_number` int(20) NULL,
	`total_item_quantity` int(20) NOT NULL,
	`total_amount` decimal(25, 4) NOT NULL,
	`total_amount_with_tax` decimal(25, 4) NOT NULL,
	`total_amount_with_tax_rounding_adjustment` decimal(25, 4) NOT NULL,
	`grand_total_amount` decimal(25, 4) NOT NULL,
	`deposit_amount` decimal(25, 4) NOT NULL,
	`tender_amount` decimal(25, 4) NOT NULL,
	`overdue_amount` decimal(25, 4) NOT NULL,
    `check_status` bigint(20) NOT NULL,
	`created_date` datetime NOT NULL,
    `updated_date` datetime NULL,
    PRIMARY KEY (`id`)
);

create table `check_tax_charge` (
	`check_id` bigint(20) NOT NULL,
	`check_number` bigint (20) NOT NULL,
    `tax_charge_id` varchar(255) NOT NULL,
	`total_charge_amount` decimal(25, 4) NOT NULL,
	`total_charge_amount_rounding_adjustment` decimal(25, 4) NOT NULL,
	`grand_total_charge_amount` decimal(25, 4) NOT NULL
);

create table `transaction_settlement_status` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `transaction_type` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `payment_method` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `payment_type` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `nii_type` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `settlement` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
    `staff_id` bigint (20) NOT NULL,
	`nii_type` bigint (20) NOT NULL,
	`settlement_status` bigint(20) NOT NULL,
	`created_date` datetime NOT NULL,
	`response_code` varchar(255) NULL,
    `response_message` varchar(255) NULL,
	`updated_date` datetime NULL,
	`wifi_ip` varchar(255) NULL,
    `wifi_port` varchar(255) NULL,
	`merchant_info` varchar(255) NULL,
	`bank_mid` varchar(255) NULL,
    `bank_tid` varchar(255) NULL,
    `batch_number` varchar(255) NULL,
    `transaction_date` varchar(255) NULL,
	`transaction_time` varchar(255) NULL,
	`batch_total` varchar(255) NULL,
	`nii` varchar(255) NULL,
    PRIMARY KEY (`id`)
);

create table `transaction` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
    `staff_id` bigint (20) NOT NULL,
    `check_id` bigint(20) NOT NULL,
	`check_number` bigint(20) NOT NULL,
    `transaction_type` bigint(20) NOT NULL,
    `payment_method` bigint(20) NOT NULL,
	`payment_type` bigint(20) NOT NULL,
	`terminal_serial_number` varchar(255) NULL,
    `transaction_currency` nvarchar(100) NOT NULL,
    `transaction_amount` decimal(25, 4) NOT NULL,
	`received_amount` decimal(25, 4) NOT NULL,
    `transaction_tips` decimal(25, 4) NULL,
	`change_amount` decimal(25, 4) NOT NULL,
    `transaction_status`bigint(20) NOT NULL,
	`unique_trans_number` varchar(255) NULL,
	`qr_content` varchar(255) NULL,
    `created_date` datetime NOT NULL,
    `response_code` varchar(255) NULL,
    `response_message` varchar(255) NULL,
    `updated_date` datetime NULL,
	`wifi_ip` varchar(255) NULL,
    `wifi_port` varchar(255) NULL,
	`approval_code` varchar(255) NULL,
	`bank_mid` varchar(255) NULL,
    `bank_tid` varchar(255) NULL,
	`transaction_date` varchar(255) NULL,
	`transaction_time` varchar(255) NULL,
	`original_invoice_number` varchar(255) NULL,
	`invoice_number` varchar(255) NULL,
	`merchant_info` varchar(255) NULL,
	`card_issuer_name` varchar(255) NULL,
	`masked_card_number` varchar(255) NULL,
	`card_expiry_date` varchar(255) NULL,
	`batch_number` varchar(255) NULL,
    `rrn` varchar(255) NULL,
	`card_issuer_id` varchar(255) NULL,
    `cardholder_name` varchar(255) NULL,
    `aid` varchar(255) NULL,
    `app_label` varchar(255) NULL,
    `tc` varchar(255) NULL,
    `terminal_verification_result` varchar(255) NULL,
	`original_trace_number` varchar(255) NULL,
	`trace_number` varchar(255) NULL,
	`qr_issuer_type` varchar(255) NULL,
	`mpay_mid` varchar(255) NULL,
	`mpay_tid` varchar(255) NULL,
	`qr_ref_id` varchar(255) NULL,
	`qr_user_id` varchar(255) NULL,
	`qr_amount_myr` varchar(255) NULL,
	`qr_amount_rmb` varchar(255) NULL,
	`auth_number` varchar(255) NULL,
    PRIMARY KEY (`id`)
);

create table `check_detail` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
    `check_id` bigint(20) NOT NULL,
	`check_number` bigint(20) NOT NULL,
    `device_type` bigint (20) NOT NULL,
	`parent_check_detail_id` bigint(20) NULL,
    `menu_item_id` bigint(20) NOT NULL,
	`menu_item_code` nvarchar(50) NOT NULL,
	`menu_item_name` nvarchar(150) NOT NULL,
    `menu_item_price` decimal(25, 4) NOT NULL,
	`quantity` int(20) NOT NULL,
	`total_amount` decimal(25, 4) NOT NULL,
    `check_detail_status` bigint(20) NOT NULL,
	`transaction_id` bigint(20) NULL,
	`created_date` datetime NOT NULL,
    `updated_date` datetime NULL,
    PRIMARY KEY (`id`)
);

create table `terminal` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`serial_number` varchar(255) NOT NULL,
	`wifi_IP` varchar(45) NULL,
	`wifi_Port` varchar(45) NULL,
	PRIMARY KEY (`id`)
);

create table `printer` (
	`model_name` varchar(255) NOT NULL,
	`port_name` varchar(255) NOT NULL,
	`paper_size` INT default 1
);

create table `general_configuration` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`description` varchar(255) NOT NULL,
	`parameter` varchar(255) NOT NULL,
	`value` varchar(255) NOT NULL, 
	PRIMARY KEY (`id`)
);

CREATE TABLE check_transaction_settlement_cloud_sync (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`sync_date` datetime not null,
	`response_code` varchar(255) NULL,
    `response_message` varchar(255) NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE device_manufacturer_lookup (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE port_name_lookup (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE cash_drawer (
	`device_manufacturer` bigint(20) NOT NULL,
	`port_name` bigint(20) NOT NULL,
	`cash_amount` DECIMAL(20,2) NOT NULL DEFAULT '0.00',
	`cash_alert` bigint(20) NOT NULL DEFAULT '0'
);

INSERT INTO cash_drawer (device_manufacturer, port_name, cash_amount, cash_alert) VALUES (1, 1, 0, 0);

CREATE TABLE cash_drawer_log (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`cash_amount` DECIMAL(20,2) NOT NULL DEFAULT '0.00',
	`new_amount` DECIMAL(20,2) NOT NULL DEFAULT '0.00',
	`reference` varchar(255) NOT NULL,
	`performed_by` bigint(20) NOT NULL,
	`created_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`)
);

CREATE TABLE receipt_printer_manufacturer_lookup (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE receipt_printer (
	`receipt_printer_manufacturer` bigint(20) NOT NULL
);

INSERT INTO general_configuration (description, parameter, value) VALUES ('Activation ID', 'ACTIVATION_ID', '');
INSERT INTO general_configuration (description, parameter, value) VALUES ('Activation Key', 'ACTIVATION_KEY', '');
INSERT INTO general_configuration (description, parameter, value) VALUES ('Mac Address', 'MAC_ADDRESS', '');
INSERT INTO general_configuration (description, parameter, value) VALUES ('Brand ID', 'BRAND_ID', '');
INSERT INTO general_configuration (description, parameter, value) VALUES ('Version Number', 'VERSION_NUMBER', '');

insert into `master` values
('check', 0, now());

insert into `check_status` values
(1, 'New'), (2, 'Pending'), (3, 'Closed'), (4, 'Cancelled');

insert into `device_type` values
(1, 'ECPOS'), (2, 'BYOD'), (3, 'KIOSK');

insert into `order_type` values
(1, 'table'), (2, 'take away');

insert into `transaction_settlement_status` values
(1, 'New'), (2, 'Pending'), (3, 'Approved'), (4, 'Declined'), (5, 'Voided'), (6, 'Refunded'), (7, 'Reversed');

insert into `transaction_type` values
(1, 'Sale'), (2, 'Void'), (3, 'Refund'), (4, 'Reversal');

insert into `payment_method` values
(1, 'Cash'), (2, 'Card'), (3, 'QR');

insert into `payment_type` values
(1, 'Full Payment'), (2, 'Partial Payment'), (3, 'Split Payment'), (4, 'Deposit Payment');

insert into `nii_type` values
(1, 'NR'), (2, 'AMEX'), (3, 'MCCS'), (4, 'UNIONPAY');

insert into general_configuration (description, parameter, value)
values ('BYOD_QR_Encrypt_Key', 'BYOD QR ENCRYPT KEY', '8y0DtH3s3Cr3Tk3Y');

insert into device_manufacturer_lookup values (1, 'No Cash Drawer'), (2, 'Posiflex'), (3, 'ETech');

insert into port_name_lookup values 
(1, 'COM1'), (2, 'COM2'), (3, 'COM3'), (4, 'COM4'), (5, 'COM5'), (6, 'COM6'), (7, 'COM7'), (8, 'COM8');

insert into receipt_printer_manufacturer_lookup values (1, 'No Printing'), (2, 'Posiflex'), (3,'EPSON');

INSERT INTO status_lookup (id, name) VALUES (1, 'PENDING');
INSERT INTO status_lookup (id, name) VALUES (2, 'ACTIVE');
INSERT INTO status_lookup (id, name) VALUES (3, 'INACTIVE');