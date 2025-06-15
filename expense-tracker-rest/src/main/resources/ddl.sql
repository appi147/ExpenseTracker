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

CREATE TABLE category (
    cat_id BIGSERIAL PRIMARY KEY,
    label VARCHAR(100),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_category_created_by FOREIGN KEY (created_by) REFERENCES user(user_id)
);

CREATE TABLE sub_category (
    sub_cat_id BIGSERIAL PRIMARY KEY,
    label VARCHAR(100),
    cat_id BIGINT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_subcategory_cat_id FOREIGN KEY (cat_id) REFERENCES category(cat_id),
    CONSTRAINT fk_subcategory_created_by FOREIGN KEY (created_by) REFERENCES user(user_id)
);
