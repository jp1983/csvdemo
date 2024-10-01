CREATE TABLE csv_file_metadata (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  file_name VARCHAR(255),
  accepted_date DATE,
  processed_date DATE,
  rows_processed BIGINT,
  rows_accepted BIGINT,
  rows_rejected BIGINT
);

CREATE TABLE employee (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  csv_file_metadata_id BIGINT,
  employee_id VARCHAR(255) not null,
  first_name VARCHAR(255) not null,
  last_name VARCHAR(255) not null,
  email VARCHAR(255) not null,
  aadhar_number VARCHAR(12) not null,
  designation VARCHAR(255),
  phone_number VARCHAR(10),
  created_date DATE,
  FOREIGN KEY (csv_file_metadata_id) REFERENCES csv_file_metadata(id)
);

CREATE TABLE csv_rejected_row (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  csv_file_metadata_id BIGINT,
  rows_number BIGINT,
  rejected_reason VARCHAR(255),
  FOREIGN KEY (csv_file_metadata_id) REFERENCES csv_file_metadata(id)
);

CREATE TABLE csv_file_status (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  csv_file_metadata_id BIGINT,
  status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') NOT NULL,
  FOREIGN KEY (csv_file_metadata_id) REFERENCES csv_file_metadata(id)
);

