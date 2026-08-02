-- Données initiales pour la base de données H2 (optionnel)
CREATE TABLE IF NOT EXISTS "accounts" (
                          "id" VARCHAR(255) NOT NULL,
                          "customer_id" VARCHAR(255) NOT NULL,
                          "account_number" VARCHAR(255) NOT NULL ,
                          "balance" float,
                          "currency" VARCHAR(3) NOT NULL,
                          "created_at" date,
                          "updated_at" date,
                          "is_active" boolean
);
CREATE TABLE IF NOT EXISTS "transactions" (
                          "id" VARCHAR(255) NOT NULL,
                          "account_id" VARCHAR(255) NOT NULL,
                          "amount" float,
                          "type" VARCHAR(255) NOT NULL,
                          "description" VARCHAR(255) NOT NULL,
                          "created_at" date,
                          "reference" VARCHAR(255) NOT NULL
);


-- Crée un compte par défaut
INSERT INTO "accounts" ("id", "customer_id", "account_number", "balance", "currency", "created_at", "updated_at", "is_active") VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'FR7612345678901234567890123', 1000.00, 'EUR', NOW(), NOW(), TRUE);

-- Crée une transaction initiale
INSERT INTO "transactions" ("id", "account_id", "amount", "type", "description", "created_at", "reference") VALUES
('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 1000.00, 'DEPOSIT', 'Initial deposit', NOW(), 'REF-001');
