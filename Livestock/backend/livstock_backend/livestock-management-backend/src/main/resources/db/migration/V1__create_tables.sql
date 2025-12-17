-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Animals Table
CREATE TABLE animals (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         tag_id VARCHAR(100) UNIQUE NOT NULL,
                         type VARCHAR(50) NOT NULL CHECK (type IN ('cow', 'calf', 'goat', 'kid')),
                         breed VARCHAR(100) NOT NULL,
                         gender VARCHAR(10) NOT NULL CHECK (gender IN ('male', 'female')),
                         date_of_birth DATE NOT NULL,
                         owner_id UUID NOT NULL,
                         status VARCHAR(50) NOT NULL DEFAULT 'healthy' CHECK (status IN ('healthy', 'sick', 'sold', 'dead')),
                         milk DECIMAL(10, 2),
                         photo TEXT,
                         parent_id UUID,
                         sale_price DECIMAL(10, 2),
                         notes TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP
);

-- Owners Table
CREATE TABLE owners (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        phone VARCHAR(50) NOT NULL,
                        address TEXT,
                        avatar TEXT,
                        national_id VARCHAR(100),
                        bank_account VARCHAR(100),
                        emergency_contact VARCHAR(255),
                        notes TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        deleted_at TIMESTAMP
);

-- Activities Table
CREATE TABLE activities (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            type VARCHAR(50) NOT NULL CHECK (type IN ('milking', 'birth', 'death', 'illness', 'treatment', 'sale', 'vaccination')),
                            description TEXT NOT NULL,
                            date TIMESTAMP NOT NULL,
                            amount DECIMAL(10, 2),
                            cost DECIMAL(10, 2),
                            notes TEXT,
                            created_by VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            deleted_at TIMESTAMP
);

-- Activity-Animal Junction Table
CREATE TABLE activity_animals (
                                  activity_id UUID NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
                                  animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
                                  PRIMARY KEY (activity_id, animal_id)
);

-- Financial Records Table
CREATE TABLE financial_records (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   type VARCHAR(50) NOT NULL CHECK (type IN ('income', 'expense')),
                                   category VARCHAR(100) NOT NULL,
                                   amount DECIMAL(10, 2) NOT NULL,
                                   description TEXT NOT NULL,
                                   date TIMESTAMP NOT NULL,
                                   owner_id UUID,
                                   animal_id UUID,
                                   payment_method VARCHAR(50) CHECK (payment_method IN ('cash', 'bank', 'mobile')),
                                   receipt_number VARCHAR(100),
                                   receipt_image TEXT,
                                   created_by VARCHAR(255) NOT NULL,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   deleted_at TIMESTAMP,
                                   FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE SET NULL,
                                   FOREIGN KEY (animal_id) REFERENCES animals(id) ON DELETE SET NULL
);

-- Users Table
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL CHECK (role IN ('admin', 'manager')),
                       avatar TEXT,
                       phone VARCHAR(50),
                       status VARCHAR(50) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'inactive')),
                       last_login TIMESTAMP,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP
);

-- Reports Table
CREATE TABLE reports (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         title VARCHAR(255) NOT NULL,
                         type VARCHAR(100) NOT NULL CHECK (type IN ('livestock', 'financial', 'health', 'production', 'owner')),
                         description TEXT,
                         date_from TIMESTAMP NOT NULL,
                         date_to TIMESTAMP NOT NULL,
                         status VARCHAR(50) NOT NULL DEFAULT 'generated' CHECK (status IN ('generated', 'pending', 'error')),
                         data JSONB,
                         filters JSONB,
                         generated_by VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications Table
CREATE TABLE notifications (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               title VARCHAR(255) NOT NULL,
                               message TEXT NOT NULL,
                               type VARCHAR(50) NOT NULL CHECK (type IN ('alert', 'warning', 'info', 'success')),
                               priority VARCHAR(50) NOT NULL DEFAULT 'medium' CHECK (priority IN ('low', 'medium', 'high', 'urgent')),
                               category VARCHAR(50) NOT NULL CHECK (category IN ('health', 'finance', 'system', 'reminder')),
                               is_read BOOLEAN DEFAULT FALSE,
                               action_required BOOLEAN DEFAULT FALSE,
                               related_entity_id UUID,
                               related_entity_type VARCHAR(50) CHECK (related_entity_type IN ('animal', 'owner', 'activity', 'financial')),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               read_at TIMESTAMP
);

-- System Settings Table
CREATE TABLE system_settings (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 key VARCHAR(100) UNIQUE NOT NULL,
                                 value TEXT NOT NULL,
                                 description TEXT,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Foreign keys and indexes
ALTER TABLE animals ADD CONSTRAINT fk_animals_owner FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE CASCADE;
ALTER TABLE animals ADD CONSTRAINT fk_animals_parent FOREIGN KEY (parent_id) REFERENCES animals(id) ON DELETE SET NULL;

CREATE INDEX idx_animals_owner_id ON animals(owner_id);
CREATE INDEX idx_animals_status ON animals(status);
CREATE INDEX idx_animals_type ON animals(type);
CREATE INDEX idx_animals_deleted_at ON animals(deleted_at);

CREATE INDEX idx_activities_type ON activities(type);
CREATE INDEX idx_activities_date ON activities(date);
CREATE INDEX idx_activities_deleted_at ON activities(deleted_at);

-- Update trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers
CREATE TRIGGER update_animals_updated_at BEFORE UPDATE ON animals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_owners_updated_at BEFORE UPDATE ON owners
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_financial_records_updated_at BEFORE UPDATE ON financial_records
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reports_updated_at BEFORE UPDATE ON reports
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_system_settings_updated_at BEFORE UPDATE ON system_settings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();