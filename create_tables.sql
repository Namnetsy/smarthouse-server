CREATE TABLE IF NOT EXISTS user_account (
  id VARCHAR (7) PRIMARY KEY,
  username VARCHAR (24),
  email VARCHAR (254) NOT NULL UNIQUE,
  pass_hash VARCHAR (64) NOT NULL,
  fcm_token VARCHAR (255) UNIQUE
);

CREATE TABLE IF NOT EXISTS hub (
  id VARCHAR (7) PRIMARY KEY,
  user_id VARCHAR (7) NOT NULL UNIQUE,
  pass_hash VARCHAR (64) NOT NULL,
  access_token VARCHAR (16) NOT NULL UNIQUE,
  FOREIGN KEY (user_id) REFERENCES user_account (id)
);

CREATE TABLE IF NOT EXISTS member (
  user_id VARCHAR (7) NOT NULL UNIQUE,
  hub_id VARCHAR (7) NOT NULL,
  is_admin BOOLEAN NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user_account (id),
  FOREIGN KEY (hub_id) REFERENCES hub (id)
);

