CREATE TABLE "user" (
  user_id VARCHAR(255) PRIMARY KEY,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  full_name VARCHAR(255),
  email VARCHAR(255),
  picture_url VARCHAR(2048),
  role VARCHAR(10) DEFAULT 'USER',
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

CREATE TABLE payment_type (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    label VARCHAR(100),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_payment_type_created_by FOREIGN KEY (created_by) REFERENCES user(user_id)
);

CREATE TABLE expense (
    expense_id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    date DATE NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    payment_type_id BIGINT NOT NULL,
    comments VARCHAR(500),
    sub_category_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES user(user_id),
    CONSTRAINT fk_expense_payment_type FOREIGN KEY (payment_type_id) REFERENCES payment_type(id),
    CONSTRAINT fk_expense_sub_category FOREIGN KEY (sub_category_id) REFERENCES sub_category(sub_cat_id)
);
