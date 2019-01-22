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
	backend_id nvarchar(50) NOT NULL UNIQUE,
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
	menu_item_name NVARCHAR(150) NOT NULL UNIQUE,
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
	backend_id NVARCHAR(50) NOT NULL UNIQUE,
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
	backend_id NVARCHAR(50) NOT NULL UNIQUE,
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
		group_category_id BIGINT DEFAULT 0,
		tax_charge_id BIGINT DEFAULT 0,
		backend_id NVARCHAR(50) NOT NULL UNIQUE,
		store_name NVARCHAR(150) NOT NULL UNIQUE,
		store_logo_path text,
		store_address NVARCHAR(150),
		store_longitude DECIMAL(11,8),
		store_latitude DECIMAL(10,8),
		store_country NVARCHAR(100),
		store_currency NVARCHAR(50),
		store_table_count INT DEFAULT 0,
		store_start_operating_time time NOT NULL,
		store_end_operating_time time NOT NULL,
		last_update_date datetime,
		is_publish BIT DEFAULT 0,
		ecpos BIT DEFAULT 0,
		created_date DATETIME NOT NULL
);

CREATE TABLE store_db_sync (
		sync_date datetime not null
);

CREATE TABLE staff  (
		id BIGINT PRIMARY KEY NOT NULL,
		store_id BIGINT DEFAULT 0,
		staff_name NVARCHAR(150) NOT NULL,
		staff_username NVARCHAR(100) NOT NULL UNIQUE,
		staff_password NVARCHAR(200) NOT NULL,
		staff_role INT NOT NULL,
		staff_contact_hp_number NVARCHAR(50) NOT NULL UNIQUE,
		staff_contact_email VARCHAR(320) NOT NULL UNIQUE,
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
	tax_charge_name NVARCHAR(100) NOT NULL UNIQUE,
	rate INT DEFAULT 0,
	charge_type INT DEFAULT 1,
	is_active BIT DEFAULT 0,
	created_date DATETIME NOT NULL
);

CREATE TABLE charge_type_lookup
(
	charge_type_number INT UNIQUE NOT NULL, 
	charge_type_name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE menu_item_tax_charge
(
	menu_item_id BIGINT NOT NULL,
	tax_charge_id BIGINT NOT NULL
);

INSERT INTO charge_type_lookup VALUES (0, 'None'),(1, 'Tax'),(2, 'Charge');

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
	`check_number` bigint(20) NOT NULL,
    `device_type` bigint (20) NOT NULL,
    `staff_id` bigint (20) NULL,
	`order_type` bigint (20) NOT NULL,
    `table_number` int(20) NULL,
	`total_item_quantity` int(20) NOT NULL,
	`subtotal_amount` decimal(25, 4) NOT NULL,
	`total_tax_amount` decimal(25, 4) NULL,
	`total_service_charge_amount` decimal(25, 4) NULL,
	`total_amount` decimal(25, 4) NOT NULL,
	`total_amount_rounding_adjustment` decimal(25, 4) NOT NULL,
	`grand_total_amount` decimal(25, 4) NOT NULL,
	`deposit_amount` decimal(25, 4) NULL,
	`overdue_amount` decimal(25, 4) NOT NULL,
    `check_status` bigint(20) NOT NULL,
	`created_date` datetime NOT NULL,
    `updated_date` datetime NULL,
    PRIMARY KEY (`id`)
);

create table `transaction_status` (
	`id` bigint(20) NOT NULL,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

create table `transaction` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
    `check_id` bigint(20) NOT NULL,
	`check_number` bigint(20) NOT NULL,
    `transaction_currency` nvarchar(100) NOT NULL,
    `transaction_amount` decimal(25, 4) NOT NULL,
    `transaction_status` bigint(20) NOT NULL,
    `transaction_date` datetime NOT NULL,
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

create table `settlement` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
    `staff_id` bigint (20) NOT NULL,
    `batch_number` varchar(255) NOT NULL,
    `bank_mid` varchar(255) NOT NULL,
    `bank_tid` varchar(255) NOT NULL,
    `total_sale` int(20) NOT NULL,
    `total_sale_amount` decimal(25, 4) NOT NULL,
    `settlement_status` bigint(20) NOT NULL,
    `created_date` datetime NOT NULL,
    `response_code` varchar(255) NULL,
    `response_message` varchar(255) NULL,
    `updated_date` datetime NULL,
    `transaction_date` datetime NULL,
    `nii` varchar(255) NULL,
    `settlement_nii` varchar(255) NULL,
    PRIMARY KEY (`id`)
);

create table `transaction_detail` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
    `staff_id` bigint (20) NOT NULL,
    `transaction_id` bigint(20) NOT NULL,
    `check_id` bigint(20) NOT NULL,
    `transaction_type` bigint(20) NOT NULL,
    `payment_method` bigint(20) NOT NULL,
	`payment_type` bigint(20) NOT NULL,
    `transaction_currency` nvarchar(100) NOT NULL,
    `transaction_amount` decimal(25, 4) NOT NULL,
    `transaction_tips` decimal(25, 4) NOT NULL,
    `transaction_detail_status`bigint(20) NOT NULL,
    `created_date` datetime NOT NULL,
    `response_code` varchar(255) NULL,
    `response_message` varchar(255) NULL,
    `updated_date` datetime NULL,
    `trace_number` varchar(255) NULL,
    `batch_number` varchar(255) NULL,
    `bank_mid` varchar(255) NULL,
    `bank_tid` varchar(255) NULL,
    `approval_code` varchar(255) NULL,
    `rrn` varchar(255) NULL,
    `masked_card_number` varchar(255) NULL,
    `cardholder_name` varchar(255) NULL,
    `aid` varchar(255) NULL,
    `app_label` varchar(255) NULL,
    `tc` varchar(255) NULL,
    `terminal_verification_result` varchar(255) NULL,
    `transaction_date` datetime NULL,
    `original_trace_number` varchar(255) NULL,
    `settlement_status` bigint(20) NOT NULL,
    `settlement_id` bigint(20) NULL,
    PRIMARY KEY (`id`)
);

create table `check_detail` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
    `check_id` bigint(20) NOT NULL,
	`check_number` bigint(20) NOT NULL,
	`parent_check_detail_id` bigint(20) NULL,
    `menu_item_id` bigint(20) NOT NULL,
	`menu_item_code` nvarchar(50) NOT NULL,
	`menu_item_name` nvarchar(150) NOT NULL,
    `menu_item_price` decimal(25, 4) NOT NULL,
	`tax_rate` INT NULL,
	`service_charge_rate` INT NULL,
	`quantity` int(20) NOT NULL,
	`subtotal_amount` decimal(25, 4) NOT NULL,
	`total_tax_amount` decimal(25, 4) NULL,
	`total_service_charge_amount` decimal(25, 4) NULL,
	`total_amount` decimal(25, 4) NOT NULL,
    `check_detail_status` bigint(20) NOT NULL,
	`transaction_detail_id` bigint(20) NULL,
	`created_date` datetime NOT NULL,
    `updated_date` datetime NULL,
    PRIMARY KEY (`id`)
);

insert into `master` values
('check', 0, now());

insert into `check_status` values
(1, 'New'), (2, 'Pending'), (3, 'Closed'), (4, 'Cancelled');

insert into `device_type` values
(1, 'BYOD'), (2, 'ECPOS'), (3, 'Kiosk');

insert into `order_type` values
(1, 'table'), (2, 'take away');

insert into `transaction_status` values
(1, 'New'), (2, 'Pending'), (3, 'Approved'), (4, 'Declined'), (5, 'Voided'), (6, 'Refunded'), (7, 'Reversed');

insert into `transaction_type` values
(1, 'Sale'), (2, 'Void'), (3, 'Refund'), (4, 'Reversal');

insert into `payment_method` values
(1, 'Cash'), (2, 'Card'), (3, 'QR');

insert into `payment_type` values
(1, 'Full Payment'), (2, 'Split Item'), (3, 'Split Payment');

insert into `staff` values
(1, 1, 'admin', 'admin', 'admin', 1, '-', '-', 1, now(), now());

insert into `store` values
(1, 0, 0, '-', 'test', '-', '-', 0.00, 0.00, 'Malaysia', 'RM', 24, '07:00:00.000', '06:59:59.999', now(), 1, 1, now());



insert into category 
(`id`,`group_category_id`,`backend_id`,`category_name`,`category_description`,`category_image_path`,`category_sequence`,`is_active`,`created_date`)
values (1,1,'C0001','Lunch Set','-','-',1,1,now());

insert into menu_item
(`id`,`backend_id`,`modifier_group_id`,`menu_item_name`,`menu_item_description`,`menu_item_image_path`,`menu_item_base_price`,`menu_item_type`,`is_taxable`,`is_discountable`,`is_active`,`created_date`)
values (1,'Ala0001',null,'Fries','-','-','3.50',0,0,0,1,now()),
(2,'Com0001',null,'McChicken','-','-','9.95',1,0,0,1,now()),
(3,'Ala0002',null,'Pepsi','-','-','2.00',0,0,0,1,now()),
(4,'Ala0003',null,'Burger','-','-','5.00',0,0,0,1,now()),
(5,'Mod0001',1,'Small','-','-','0.00',2,0,0,1,now()),
(6,'Mod0002',1,'Medium','-','-','0.00',2,0,0,1,now()),
(7,'Mod0003',1,'Large','-','-','0.00',2,0,0,1,now()),
(8,'Ala0004',null,'Chicken Burger','-','-','4.50',0,0,0,1,now()),
(9,'Ala0005',null,'Fish Burger','-','-','5.50',0,0,0,1,now());

insert into category_menu_item
(`category_id`,`menu_item_id`,`category_menu_item_sequence`)
values (1,1,1),(1,2,2);

insert into combo_detail
(`id`,`menu_item_id`,`combo_detail_name`,`combo_detail_quantity`,`combo_detail_sequence`,`created_date`)
values (1,2,'Fries',2,2,now()),(2,2,'Drink',1,3,now()),(3,2,'Burger',1,1,now());

insert into combo_item_detail
(`id`,`combo_detail_id`,`menu_item_id`,`menu_item_group_id`,`combo_item_detail_sequence`,`created_date`)
values (1,3,null,2,2,now()),(2,3,8,null,1,now()),(3,1,1,null,1,now()),(4,2,null,1,1,now());

insert into menu_item_group
(`id`,`backend_id`,`menu_item_group_name`,`is_active`,`created_date`)
values (1,'MIG0001','Beverage',1,now()),(2,'MIG0002','Burger',1,now());

insert into menu_item_group_sequence
(`menu_item_group_id`,`menu_item_id`,`menu_item_group_sequence`)
values (2,9,1),(1,3,1),(2,4,2);

insert into menu_item_modifier_group
(menu_item_id,modifier_group_id,menu_item_modifier_group_sequence)
values (3,1,1),(3,2,2);

insert into modifier_group
(id,backend_id,modifier_group_name,is_active,created_date)
values (1,'MOD0001','Ice Level',1,now()),(2,'MOD0002','Sugar Level',1,now());

insert into menu_item
(`id`,`backend_id`,`modifier_group_id`,`menu_item_name`,`menu_item_description`,`menu_item_image_path`,`menu_item_base_price`,`menu_item_type`,`is_taxable`,`is_discountable`,`is_active`,`created_date`)
values (10,'Mod1001',1,'Less ice','-','-','0.00',2,0,0,1,now()),
(11,'Mod2001',2,'75%','-','-','0.00',2,0,0,1,now()),
(12,'Mod1002',1,'More ice','-','-','0.00',2,0,0,1,now()),
(13,'Mod2003',2,'25%','-','-','0.00',2,0,0,1,now()),
(14,'Mod2002',2,'50%','-','-','0.00',2,0,0,1,now());

insert into modifier_item_sequence
(modifier_group_id,menu_item_id,modifier_item_sequence)
values (2,11,3),(2,13,1),(2,14,2),(1,10,1),(1,12,2);