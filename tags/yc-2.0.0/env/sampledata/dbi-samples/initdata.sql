-- Igor Azarny iazarny@yahoo.com.
-- Initial data.
--

-- SET character_set_client=utf8;
-- SET character_set_connection=utf8;


INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION, GUID)  VALUES (1, 'accessories' , 'Accessories' , 'Product accessories', '1_TASSOCIATION');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION, GUID)  VALUES (2, 'up' , 'Up sell' , 'Up sell', '2_TASSOCIATION');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION, GUID)  VALUES (3, 'cross' , 'Cross sell' , 'Cross sell', '3_TASSOCIATION');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION, GUID)  VALUES (4, 'buywiththis' , 'Buy with this products' , 'Shoppers also buy with this product', '4_TASSOCIATION');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, NAME, DESCRIPTION, GUID)  VALUES (5, 'expendable' , 'Expendable materials' , 'Expendable materials. Example inc for printer', , '5_TASSOCIATION');


INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1000, 'java.lang.String', 'String', 'String');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1001, 'java.lang.String', 'URI', 'URI');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1002, 'java.lang.String', 'URL', 'URL');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1003, 'java.lang.String', 'Image', 'Image');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1004, 'java.lang.String', 'CommaSeparatedList', 'CommaSeparatedList');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1005, 'java.lang.Float', 'Float', 'Float');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1006, 'java.lang.Integer', 'Integer', 'Integer');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1007, 'java.lang.String', 'Phone', 'Phone');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1008, 'java.lang.Boolean', 'Boolean', 'Boolean');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1009, 'java.util.Date', 'Date', 'Date');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1010, 'java.lang.String', 'Email', 'Email');



INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1000, 'SYSTEM', 'System settings.', 'System wide settings');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1001, 'SHOP', 'Shop settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1002, 'CATEGORY', 'Category settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1003, 'PRODUCT', 'Product settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1005, 'BRAND', 'Brand settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, CODE, NAME, DESCRIPTION) VALUES (1006, 'CUSTOMER', 'Customer settings.', '');



INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, GUID)
  VALUES (  10998,  'PRICE_NAVIGATION_STRATEGY',  0,  NULL,  'Price navigation strategy',  '[STATIC] or [DYNAMIC]. DYNAMIC allow create price tier dynamically for not default currency',  1004, 1001, 'PRICE_NAVIGATION_STRATEGY');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, GUID)
  VALUES (  10800,  'SHOP_ADMIN_EMAIL',  1,  NULL,  'Shop admin email',  'Shop admin email',  1010, 1001, 'SHOP_ADMIN_EMAIL');


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  999,  'CURRENCY',  0,  NULL,  'Currencies',  'Supported currencies by shop. First one is the default',  1004, 1001);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1000,  'SYSTEM_DEFAULT_SHOP',  1,  NULL,  'System. Default shop',
  'This value will be used for redirections when shop can not be resolved by http request', 1002,  1000);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1040,  'SHOP_B2B',  1,  NULL,  'Is B2B profile for this shop',  'Is B2B profile for this shop',  1000, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1007,  'SYSTEM_IMAGE_VAULT',  1,  NULL,  'Root directory for image repository',
  'Root directory for image repository', 1000,  1000);



INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1001,  'BRAND_IMAGE',  1,  NULL,  'Brand image',  null,  1003, 1005);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1002,  'CATEGORY_ITEMS_PER_PAGE',  0,  NULL,  'Category item per page settings',
   'Category item per page settings with fail over',  1004, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1003,  'MATERIAL',  0,  NULL,  'Material',  'Material',   1000, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1004,  'CATEGORY_IMAGE',  0,  NULL,  'Category image',   'Category image',  1003, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1005,  'CATEGORY_IMAGE_RETRIEVE_STRATEGY',  0,  NULL,  'Strategy to retrieve image',
  'Strategy to retrieve images. Allowed values: [ATTRIBUTE] i.e. use CATEGORY_IMAGE attribute or [RANDOM_PRODUCT] i.e. random product image will be used',  1000, 1002);



INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  999,  'CURRENCY',  0,  NULL,  'Currencies',  'Supported currencies by shop. First one is default',  1004, 1001);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  2000,  'PRODUCT_IMAGE_WIDTH',  0,  NULL,  'Product image width in category',   'Product image width in category',  1006, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  2001,  'PRODUCT_IMAGE_HEIGHT',  0,  NULL,  'Product image height in category',   'Product image height in category',  1006, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  2050,  'PRODUCT_IMAGE_TUMB_WIDTH',  0,  NULL,  'Product thumbnail image width',   'Product thumbnail image width',  1006, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  2051,  'PRODUCT_IMAGE_TUMB_HEIGHT',  0,  NULL,  'Product thumbnail image height',   'Product thumbnail image height',  1006, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1998,  'CATEGORY_IMAGE_WIDTH',  0,  NULL,  'Category image  width ',   'Category image width thumbnail ',  1006, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  1999,  'CATEGORY_IMAGE_HEIGHT',  0,  NULL,  'Category image   height',   'Category image height thumbnail ',  1006, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  2000,  'PRODUCT_IMAGE_WIDTH',  0,  NULL,  'Product image width in category',   'Product image width in category',  1006, 1002);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)  VALUES (  2001,  'PRODUCT_IMAGE_HEIGHT',  0,  NULL,  'Product image height in category',   'Product image height in category',  1006, 1002);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1008,  'IMAGE0',  1,  NULL,  'Product default image',  'Product default image',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1009,  'IMAGE1',  0,  NULL,  'Product alternative image 1',  'Product alternative image 1',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1010,  'IMAGE2',  0,  NULL,  'Product alternative image 2',  'Product alternative image 2',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1011,  'IMAGE3',  0,  NULL,  'Product alternative image 3',  'Product alternative image 3',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1012,  'IMAGE4',  0,  NULL,  'Product alternative image 4',  'Product alternative image 4',  1003, 1003);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1013,  'IMAGE5',  0,  NULL,  'Product alternative image 5',  'Product alternative image 5',  1003, 1003);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1050,  'CUSTOMER_PHONE',  1,  NULL,  'Phone',  'Phone', 1007,  1006);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID)
  VALUES (  1051,  'MARKETING_OPT_IN',  0,  NULL,  'Marketing Opt in',  'If true then customer opted in for marketing contact', 1007,  1006);

INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPPABLE)
  VALUES (500,'Default Product','Default Product','default', 0,0,1);
INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPPABLE)
  VALUES (501,'Default Accessory','Default Accessory','default', 0,0,1);


INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPPABLE)  VALUES (500,'Laptop','Laptop','default', 0,0,1);
INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID , NAME, DESCRIPTION, UISEARCHTEMPLATE, SERVICE, ENSEMBLE, SHIPPABLE)  VALUES (501,'Laptop accessories','Laptop accessories','default', 0,0,1);

INSERT INTO TPRODUCTTYPEATTR(PRODTYPEATTR_ID, CODE, PRODUCTTYPE_ID, RANK, VISIBLE, SIMILARITY, NAV, NAV_TYPE, RANGE_NAV)  VALUES (50000,          'WEIGHT',          500,      70 ,   1,         1,        1, 'R' ,
'<range-list><ranges><range><from>0.10</from><to>1.00</to></range><range><from>1.00</from><to>2.00</to></range><range><from>2.00</from><to>3.00</to></range><range><from>3.00</from><to>4.00</to></range><range><from>4.00</from><to>5.00</to></range><range><from>5.00</from><to>6.00</to></range><range><from>6.00</from><to>7.00</to></range><range><from>7.00</from><to>8.00</to></range><range><from>8.00</from><to>10.00</to></range><range><from>10.00</from><to>20.00</to></range></range-list>'
);


INSERT INTO TSYSTEM (SYSTEM_ID, CODE, NAME, DESCRIPTION)  VALUES (100,'SYSTEM','Yes e-commerce platform', 'System table');

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID)   VALUES (1000,'http://testdevshop.yes-cart.org:8080/yes-shop','SYSTEM_DEFAULT_SHOP',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID)   VALUES (1002,'10,20,40','SEARCH_ITEMS_PER_PAGE',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID)  VALUES (1003,'common/imagevault','SYSTEM_IMAGE_VAULT',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID)  VALUES (1011,'basePaymentModule,cappPaymentModule,gswmPaymentModule','SYSTEM_PAYMENT_MODULES_URLS',100);
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID)  VALUES (1012,'testPaymentGatewayLabel,courierPaymentGatewayLabel,cyberSourcePaymentGatewayLabel,authorizeNetAimPaymentGatewayLabel,authorizeNetSimPaymentGatewayLabel,payflowPaymentGatewayLabel,payPalNvpPaymentGatewayLabel,payPalExpressPaymentGatewayLabel,liqPayPaymentGatewayLabel','SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL',100);


INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, IMGVAULT, CODE)  VALUES (10, 'YesCart shop', 'YesCart shop', 'default', '/default/imagevault/', 'SHOP10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (10, 'USD,EUR,UAH', 'CURRENCY', 10, 'USD,EUR,UAH');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (11, 'DYNAMIC', 'PRICE_NAVIGATION_STRATEGY', 10, '123123-123123');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (12, 'admin@at-shop-10.com', 'SHOP_ADMIN_EMAIL', 10, 'admin-email-guid');

INSERT INTO TSTOREEXCHANGERATE (SHOPEXCHANGERATE_ID, FROMCURRENCY, TOCURRENCY, SHOP_ID, RATE)  VALUES(1,'EUR','UAH',10, 11.38);


INSERT INTO TWAREHOUSE (WAREHOUSE_ID, CODE, NAME, DESCRIPTION) VALUES (1, 'Main', 'Main warehouse', null);
INSERT INTO TSHOPWAREHOUSE (SHOPWAREHOUSE_ID, SHOP_ID, WAREHOUSE_ID, RANK )
  VALUES (10, 10, 1, 10 );


INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, IMGVAULT, CODE)  VALUES (10, 'Gadget universe', 'Gadget universe shop', 'default', '/default/imagevault/', 'SHOP10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID)  VALUES (10, 'USD,EUR,UAH', 'CURRENCY', 10);
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID)  VALUES (11, 'DYNAMIC', 'PRICE_NAVIGATION_STRATEGY', 10);

INSERT INTO TSTOREEXCHANGERATE (SHOPEXCHANGERATE_ID, FROMCURRENCY, TOCURRENCY, SHOP_ID, RATE)  VALUES(1,'EUR','UAH',10, 11.38);


INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (10, 10, 'testdevshop.yes-cart.org');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (11, 10, 'www.testdevshop.yes-cart.org');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL )  VALUES (12, 10, 'localhost');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (100, 100, 0, 'root', 'The root category','default');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE) VALUES (100, 100, 0, 'root', 'Master category','default');

INSERT INTO TROLE (ROLE_ID, CODE, DESCRIPTION) VALUES (1, 'ROLE_SMADMIN', 'System admin');
INSERT INTO TROLE (ROLE_ID, CODE, DESCRIPTION) VALUES (2, 'ROLE_SMSHOPADMIN', 'Shop manager');
INSERT INTO TROLE (ROLE_ID, CODE, DESCRIPTION) VALUES (3, 'ROLE_SMWAREHOUSEADMIN', 'Inventory manager');
INSERT INTO TROLE (ROLE_ID, CODE, DESCRIPTION) VALUES (4, 'ROLE_SMCALLCENTER', 'Call centre operator');

-- default admin password 1234567
INSERT INTO TMANAGER (MANAGER_ID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD) VALUES (1, 'admin@yes-cart.com', 'Yes', 'Admin', 'fcea920f7412b5da7be0cf42b8c93759');

INSERT INTO TMANAGERROLE (MANAGERROLE_ID, EMAIL, CODE) VALUES (1, 'admin@yes-cart.com', 'ROLE_SMADMIN');



INSERT INTO TCOUNTRY (COUNTRY_ID, COUNTRY_CODE, ISO_CODE, NAME, GUID) VALUES ( 218 ,'UA', '804' ,'Ukraine', 'Ukraine');

INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 80 , 'UA', 'OD', 'Odessa');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 81 , 'UA', 'DN', 'Dnipropetrovsk');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 82 , 'UA', 'CH', 'Chernihiv');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 83 , 'UA', 'KH', 'Kharkiv');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 84 , 'UA', 'ZH', 'Zhytomyr');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 85 , 'UA', 'PO', 'Poltava');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 86 , 'UA', 'KE', 'Kherson');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME, GUID) VALUES ( 87 , 'UA', 'KI', 'Kiev', 'Kiev');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 88 , 'UA', 'ZP', 'Zaporizhia');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 89 , 'UA', 'LU', 'Luhansk');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 90 , 'UA', 'DO', 'Donetsk');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 91 , 'UA', 'VI', 'Vinnytsia');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 92 , 'UA', 'CR', 'Crimea (Autonomous Republic)');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 93 , 'UA', 'MY', 'Mykolaiv');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 94 , 'UA', 'KI', 'Kirovohrad');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 95 , 'UA', 'SU', 'Sumy');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 96 , 'UA', 'LV', 'Lviv');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 97 , 'UA', 'CH', 'Cherkasy');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 98 , 'UA', 'HM', 'Khmelnytskyi');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 99 , 'UA', 'VO', 'Volyn');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 100 , 'UA', 'RI', 'Rivne');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 101 , 'UA', 'IV', 'Ivano-Frankivsk');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 102 , 'UA', 'TE', 'Ternopil');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 103 , 'UA', 'ZA', 'Zakarpattia');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 104 , 'UA', 'CH', 'Chernivtsi');
INSERT INTO TSTATE (STATE_ID, COUNTRY_CODE, STATE_CODE, NAME) VALUES ( 105 , 'UA', 'SE', 'Sevastopol (Municipality)');



INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL, GUID)    VALUES (1, 'UPS', 'World wide', 1, 1, 1, 1, 'UPS');
INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL)    VALUES (2, 'LCC', 'Local country carrier', 0, 1, 1, 1);
INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL)    VALUES (3, 'LRC', 'Local area carrier', 0, 0, 1, 1);
INSERT INTO TCARRIER (CARRIER_ID, NAME, DESCRIPTION, WORLDWIDE, COUNTRY, STATE, LOCAL)    VALUES (4, 'City carrier', 'City carrier', 0, 0, 0, 1);


INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (9, 1, 'UPS. World wide. Max 5 days. RUB',    'RUB', 5, 'F', 900);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (2, 1, 'UPS. Country wide delivery. Max 4 days. RUB',  'RUB', 4, 'F', 700);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (3, 1, 'UPS. Local area delivery. Max 3 days. RUB', 'RUB', 3, 'F', 600);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (4, 1, 'UPS. City delivery. Max 1 day. RUB',  'RUB', 1, 'F', 300);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (5, 1, 'UPS. World wide. Max 5 days. UAH',    'UAH', 5, 'F', 240);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (6, 1, 'UPS. Country wide delivery. Max 4 days. UAH',  'UAH', 4, 'F', 160);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (7, 1, 'UPS. Local area delivery. Max 3 days. UAH', 'UAH', 3, 'F', 140);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (8, 1, 'UPS. City delivery. Max 1 day. UAH',  'UAH', 1, 'F', 25);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE, GUID)   VALUES (1, 1, 'UPS. World wide. Max 5 days. USD',     'USD', 5, 'F', 30, 'UPS_USD_GUID');
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (10, 1, 'UPS. Country wide delivery. Max 5 days. USD',  'USD', 4, 'F', 25);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (11, 1, 'UPS. Local area delivery. Max 5 days. USD', 'USD', 3, 'F', 20);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (12, 1, 'UPS. City delivery. Max 5 days. USD',  'USD', 1, 'F', 10);

INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (13, 2, 'LCC. Country wide delivery. Max 5 days. RUB',  'RUB', 4, 'F', 650);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (14, 2, 'LCC. Local area delivery. Max 4 days. RUB', 'RUB', 2, 'F', 550);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (15, 2, 'LCC. City delivery. Max 1 day. RUB',  'RUB', 1, 'F', 290);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (16, 2, 'LCC. Country wide delivery. Max 4 days. UAH',  'UAH', 4, 'F', 150);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (17, 2, 'LCC. Local area delivery. Max 2 days. UAH', 'UAH', 2, 'F', 135);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (18, 2, 'LCC. City delivery. Max 1 days. UAH',  'UAH', 1, 'F', 25);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (19, 2, 'LCC. Country wide delivery. Max 4 days. USD',  'USD', 4, 'F', 20);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (20, 2, 'LCC. Local area delivery. Max 2 days. USD', 'USD', 2, 'F', 15);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (21, 2, 'LCC. City delivery. Max 1 day. USD',  'USD', 1, 'F', 10);

INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (22, 3, 'LRC. Local area delivery. Max 3 days. RUB', 'RUB', 3, 'F', 540);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (23, 3, 'LRC. City delivery. Max 1 days. RUB',  'RUB', 1, 'F', 280);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (24, 3, 'LRC. Local area delivery. Max 3 days. UAH', 'UAH', 3, 'F', 160);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (25, 3, 'LRC. City delivery. Max 1 day. UAH',  'UAH', 1, 'F', 40);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (26, 3, 'LRC. Local area delivery. Max 3 days. USD', 'USD', 3, 'F', 10);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (27, 3, 'LRC. City delivery. Max 1 day. USD',  'USD', 1, 'F', 8);

INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (28, 4, 'CD. City delivery. Max 2 days. RUB',  'RUB', 2, 'F', 220);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (29, 4, 'CD. City delivery. Max 2 days. UAH',  'UAH', 2, 'F', 35);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (30, 4, 'CD. City delivery. Max 2 days. USD',  'USD', 2, 'F', 7);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (31, 4, 'CD. City delivery. Max 1 days. USD',  'USD', 1, 'F', 10);
INSERT INTO TCARRIERSLA (CARRIERSLA_ID, CARRIER_ID, NAME, CURRENCY, MAX_DAYS, SLA_TYPE, PRICE)   VALUES (32, 4, 'CD. Cheap City delivery. Max 2 days. UAH',  'UAH', 2, 'F', 0.10);



INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (9000,'PRODUCT_IMAGE_WIDTH','280',100);
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (9001,'PRODUCT_IMAGE_HEIGHT','280',100);
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (9002,'PRODUCT_IMAGE_TUMB_WIDTH','80',100);
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (9003,'PRODUCT_IMAGE_TUMB_HEIGHT','80',100);

INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (9004,'CATEGORY_IMAGE_WIDTH','80',100);
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID) VALUES (9005,'CATEGORY_IMAGE_HEIGHT','80',100);



COMMIT;