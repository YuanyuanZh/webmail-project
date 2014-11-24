-- 10-27
alter table emails add column `MESSAGE_ID` varchar(255);
alter table emails add column `CONTENT_TYPE` varchar(100);

-- 10-28
alter table emails add column `UID` varchar(100);
alter table emails add column `FLAGS` integer default 0;

-- 10-29
alter table emails add column `FLAG_NEW` integer default 1;
alter table emails add column `FLAG_UNREAD` integer default 1;
alter table emails add column `FLAG_FAV` integer default 0;


-- Nov 10, 2014
alter table emails add column `FLAG_DEL` integer default 0;

--NOV 13,2014

alter table accounts add column 'POP_SERVER' varchar;
alter table accounts add column 'POP_SERVER_PORT' int;
alter table accounts add column 'ENABLE_SSL' boolean;
-- Nov 19, 2014
alter table accounts add column 'SMTP_SERVER' varchar(200);
alter table accounts add column 'SMTP_SERVER_PORT' int;
alter table accounts add column 'ENABLE_SMTP_SSL' boolean;
alter table accounts add column 'DISPLAY_NAME' varchar(200);
alter table accounts add column 'MAIL_SIGNATURE' varchar(250);


-- Nov 21, 2014

alter table emails add column 'OWNER_ADDRESS' VARCHAR(200);