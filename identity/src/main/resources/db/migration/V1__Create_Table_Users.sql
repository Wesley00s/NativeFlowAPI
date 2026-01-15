CREATE TABLE user_tb
(
    id         UUID                        NOT NULL,
    avatar     VARCHAR(255),
    first_name VARCHAR(255)                NOT NULL,
    last_name  VARCHAR(255)                NOT NULL,
    role       VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_active  BOOLEAN                     NOT NULL,
    email      VARCHAR(255),
    password   VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);