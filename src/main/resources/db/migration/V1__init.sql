
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    social_id VARCHAR(255) NOT NULL,
    social_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    title VARCHAR(20) NOT NULL,
    affiliation VARCHAR(20) NOT NULL,
    enrollment_date DATE NOT NULL,
    name VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    verifier VARCHAR(20) NOT NULL,
    records JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE INDEX idx_documents_user_id ON documents(user_id);
