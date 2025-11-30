-- Create tables (paste all from guide here)
CREATE TABLE animals (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         tag_id VARCHAR(100) UNIQUE NOT NULL,
                         type VARCHAR(50) NOT NULL CHECK (type IN ('cow', 'calf', 'goat', 'kid')),
                         breed VARCHAR(100) NOT NULL,
                         gender VARCHAR(10) NOT NULL CHECK (gender IN ('male', 'female')),
                         date_of_birth DATE NOT NULL,
                         owner_id UUID NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
                         status VARCHAR(50) NOT NULL DEFAULT 'healthy' CHECK (status IN ('healthy', 'sick', 'sold', 'dead')),
                         milk DECIMAL(10, 2),
                         photo TEXT,
                         parent_id UUID REFERENCES animals(id) ON DELETE SET NULL,
                         sale_price DECIMAL(10, 2),
                         notes TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP
);

CREATE INDEX idx_animals_owner_id ON animals(owner_id);
CREATE INDEX idx_animals_status ON animals(status);
CREATE INDEX idx_animals_type ON animals(type);
CREATE INDEX idx_animals_deleted_at ON animals(deleted_at);

-- (Add all other tables similarly: Owners, Activities, Activity_Animals, Financial_Records, Users, Reports, Notifications, System_Settings)

-- Trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
  $$ language 'plpgsql';

-- Triggers (add for all tables with updated_at)
CREATE TRIGGER update_animals_updated_at BEFORE UPDATE ON animals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- (Add triggers for Owners, Financial_Records, Users, Reports, System_Settings)