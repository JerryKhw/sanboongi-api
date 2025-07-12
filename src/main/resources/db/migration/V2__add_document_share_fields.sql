ALTER TABLE documents
ADD COLUMN download_file_uploaded_at TIMESTAMPTZ,
ADD COLUMN share_file_uploaded_at TIMESTAMPTZ,
ADD COLUMN shared BOOLEAN NOT NULL DEFAULT false;

CREATE INDEX idx_shared ON documents(shared);
