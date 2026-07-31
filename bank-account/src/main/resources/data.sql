-- Données initiales pour la base de données H2 (optionnel)

-- Crée un client par défaut
INSERT INTO customers (id, name, email) VALUES 
('10000000-0000-0000-0000-000000000001', 'John Doe', 'john.doe@example.com');

-- Crée un compte par défaut
INSERT INTO accounts (id, customer_id, account_number, balance, currency, created_at, updated_at, is_active) VALUES 
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'FR7612345678901234567890123', 1000.00, 'EUR', NOW(), NOW(), TRUE);

-- Crée une transaction initiale
INSERT INTO transactions (id, account_id, amount, type, description, created_at, reference) VALUES 
('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 1000.00, 'DEPOSIT', 'Initial deposit', NOW(), 'REF-001');
