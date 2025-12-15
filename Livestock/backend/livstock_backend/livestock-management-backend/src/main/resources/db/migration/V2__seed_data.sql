INSERT INTO system_settings (key, value, description) VALUES ('organizationName', 'My Farm', 'Organization name');
-- Add more seed data as needed, e.g., a default admin user (hash password first).

INSERT INTO users (id, name, email, password, role, status, created_at, updated_at)
VALUES (uuid_generate_v4(), 'Admin User', 'admin@farm.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- BCrypt for "password"
        'admin', 'active', now(), now());