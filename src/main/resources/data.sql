-- Seed Techniciens
INSERT INTO technicien (id, nom, specialite) VALUES (1, 'Alice Martin', 'Électromécanique')
    ON DUPLICATE KEY UPDATE nom=VALUES(nom), specialite=VALUES(specialite);
INSERT INTO technicien (id, nom, specialite) VALUES (2, 'Bilal Saïd', 'Automatisme')
    ON DUPLICATE KEY UPDATE nom=VALUES(nom), specialite=VALUES(specialite);
INSERT INTO technicien (id, nom, specialite) VALUES (3, 'Chloé Dupont', 'Hydraulique')
    ON DUPLICATE KEY UPDATE nom=VALUES(nom), specialite=VALUES(specialite);

-- Seed Equipements
INSERT INTO equipement (id, code, type, criticite, site) VALUES (10, 'EQ-PR-100', 'Presse', 'HAUTE', 'Paris')
    ON DUPLICATE KEY UPDATE code=VALUES(code), type=VALUES(type), criticite=VALUES(criticite), site=VALUES(site);
INSERT INTO equipement (id, code, type, criticite, site) VALUES (11, 'EQ-CNV-200', 'Convoyeur', 'MOYENNE', 'Lyon')
    ON DUPLICATE KEY UPDATE code=VALUES(code), type=VALUES(type), criticite=VALUES(criticite), site=VALUES(site);
INSERT INTO equipement (id, code, type, criticite, site) VALUES (12, 'EQ-PMP-300', 'Pompe', 'BASSE', 'Paris')
    ON DUPLICATE KEY UPDATE code=VALUES(code), type=VALUES(type), criticite=VALUES(criticite), site=VALUES(site);

-- Seed Interventions (composite PK: equipement_id, technicien_id, date_ouverture)
-- Résolues
INSERT INTO intervention (equipement_id, technicien_id, date_ouverture, date_cloture, priorite, statut)
VALUES (10, 1, DATE('2025-01-10'), DATE('2025-01-12'), 'HAUTE', 'RESOLU')
ON DUPLICATE KEY UPDATE date_cloture=VALUES(date_cloture), priorite=VALUES(priorite), statut=VALUES(statut);

INSERT INTO intervention (equipement_id, technicien_id, date_ouverture, date_cloture, priorite, statut)
VALUES (11, 2, DATE('2025-02-05'), DATE('2025-02-06'), 'MOYENNE', 'RESOLU')
ON DUPLICATE KEY UPDATE date_cloture=VALUES(date_cloture), priorite=VALUES(priorite), statut=VALUES(statut);

INSERT INTO intervention (equipement_id, technicien_id, date_ouverture, date_cloture, priorite, statut)
VALUES (12, 3, DATE('2025-03-15'), DATE('2025-03-20'), 'BASSE', 'RESOLU')
ON DUPLICATE KEY UPDATE date_cloture=VALUES(date_cloture), priorite=VALUES(priorite), statut=VALUES(statut);

-- Ouvertes / En cours
INSERT INTO intervention (equipement_id, technicien_id, date_ouverture, date_cloture, priorite, statut)
VALUES (10, 2, DATE('2025-10-01'), NULL, 'HAUTE', 'EN_COURS')
ON DUPLICATE KEY UPDATE date_cloture=VALUES(date_cloture), priorite=VALUES(priorite), statut=VALUES(statut);

INSERT INTO intervention (equipement_id, technicien_id, date_ouverture, date_cloture, priorite, statut)
VALUES (11, 1, DATE('2025-11-01'), NULL, 'MOYENNE', 'OUVERT')
ON DUPLICATE KEY UPDATE date_cloture=VALUES(date_cloture), priorite=VALUES(priorite), statut=VALUES(statut);
