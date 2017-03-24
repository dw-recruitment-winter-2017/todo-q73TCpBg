CREATE TABLE todos (
    id UUID PRIMARY KEY NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT false,
    description VARCHAR(256) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL
);
