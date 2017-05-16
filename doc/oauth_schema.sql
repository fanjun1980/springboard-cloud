--  Oauth MYSQL
CREATE TABLE IF NOT EXISTS oauth_client_details (
  client_id VARCHAR(128) PRIMARY KEY,
  resource_ids VARCHAR(128),
  client_secret VARCHAR(128),
  scope VARCHAR(128),
  authorized_grant_types VARCHAR(128),
  web_server_redirect_uri VARCHAR(128),
  authorities VARCHAR(128),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(128)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- create table oauth_client_token (
--  token_id VARCHAR(128),
--  token LONGVARBINARY,
--  authentication_id VARCHAR(128) PRIMARY KEY,
--  user_name VARCHAR(128),
--  client_id VARCHAR(128)
-- );

CREATE TABLE IF NOT EXISTS oauth_access_token (
  token_id VARCHAR(128),
  token BLOB,
  authentication_id VARCHAR(128) PRIMARY KEY,
  user_name VARCHAR(128),
  client_id VARCHAR(128),
  authentication BLOB,
  refresh_token VARCHAR(128)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS oauth_refresh_token (
  token_id VARCHAR(128),
  token BLOB,
  authentication BLOB
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS oauth_code (
  code VARCHAR(128), 
  authentication BLOB
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- create table oauth_approvals (
--	userId VARCHAR(128),
--	clientId VARCHAR(128),
--	scope VARCHAR(128),
--	status VARCHAR(10),
--	expiresAt TIMESTAMP,
--	lastModifiedAt TIMESTAMP
-- );


-- customized oauth_client_details table
-- create table ClientDetails (
--  appId VARCHAR(128) PRIMARY KEY,
--  resourceIds VARCHAR(128),
--  appSecret VARCHAR(128),
--  scope VARCHAR(128),
--  grantTypes VARCHAR(128),
--  redirectUrl VARCHAR(128),
--  authorities VARCHAR(128),
--  access_token_validity INTEGER,
--  refresh_token_validity INTEGER,
--  additionalInformation VARCHAR(4096),
--  autoApproveScopes VARCHAR(128)
-- );

-- Add indexes
create index token_id_index on oauth_access_token (token_id);
create index user_name_index on oauth_access_token (user_name);
create index client_id_index on oauth_access_token (client_id);
create index refresh_token_index on oauth_access_token (refresh_token);

create index token_id_index on oauth_refresh_token (token_id);

create index code_index on oauth_code (code);
