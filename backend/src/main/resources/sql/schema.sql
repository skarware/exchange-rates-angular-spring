create table CURRENCIES
(
	NUMERIC_CODE SMALLINT not null primary key,
	ALPHABETIC_CODE CHAR(3) not null
);

create table FX_RATES
(
	ID BIGINT not null primary key,
	EFFECTIVE_DATE DATE not null,
	EXCHANGE_RATE DECIMAL(20,10) not null,
	SOURCE_CURRENCY SMALLINT not null,
	TARGET_CURRENCY SMALLINT not null,
	constraint UKCHSWYSEH75B32V8EU6KONOU54
		unique (SOURCE_CURRENCY, TARGET_CURRENCY, EFFECTIVE_DATE),
	constraint FK2SJKP9PGEBQJTBDLH149415XR
		foreign key (SOURCE_CURRENCY) references CURRENCIES (NUMERIC_CODE),
	constraint FKL68ID7YCUPR0NMN3WFHPI40PY
		foreign key (TARGET_CURRENCY) references CURRENCIES (NUMERIC_CODE)
);

-- auto-generated definition
create sequence FX_RATES_SEQ start with 1261 increment by 1;




