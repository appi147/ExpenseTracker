CREATE TABLE "user" (
  user_id VARCHAR(255) PRIMARY KEY,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  full_name VARCHAR(255),
  email VARCHAR(255),
  picture_url VARCHAR(2048),
  role VARCHAR(10) DEFAULT 'user',
  last_login TIMESTAMPTZ,
  created_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ
);
