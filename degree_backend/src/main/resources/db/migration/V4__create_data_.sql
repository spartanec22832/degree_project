CREATE TABLE data (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(50),
    category VARCHAR(100),
    description TEXT,
    address VARCHAR(255),
    work_time VARCHAR(100),
    price NUMERIC(10,2),
    rating NUMERIC(3,1),
    contact_link TEXT,
    contact_phone VARCHAR(100),
    contact_email VARCHAR(100),
    cluster_element VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);
