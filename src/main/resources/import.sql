-- =============================================================================
-- OCC Dummy Data
-- Password for all users: admin123
-- BCrypt hash: $2a$10$wT8m9o3/XvQzj5YvD5176ea3Zk.g9XN15TInwXG3HInR5DXYKOhH2
-- TransportCategory valid values: INTERCITY, LOCAL, COMMUTER, LRT, WHOOSH
-- Directions valid values: EAST, WEST
-- ActiveStatus valid values: ACTIVE, INACTIVE
-- =============================================================================

-- ---------------------------------------------------------------------------
-- USERS (column names are camelCase-quoted as per the entity definition)
-- ---------------------------------------------------------------------------
INSERT INTO "user" ("userId", "nrp", "fullName", "password", "role", "createdAt", "updatedAt", "createdBy", "updatedBy") VALUES
('a3f1e2d4-bc56-4a78-9012-3c4d5e6f7a8b', 'OCC001', 'Budi OCC',     '$2a$10$wT8m9o3/XvQzj5YvD5176ea3Zk.g9XN15TInwXG3HInR5DXYKOhH2', 'PETUGAS_OCC', NOW(), NOW(), NULL, NULL),
('b4e2f3c5-cd67-4b89-a123-4d5e6f7a8b9c', 'JPL001', 'Joko JPL',     '$2a$10$wT8m9o3/XvQzj5YvD5176ea3Zk.g9XN15TInwXG3HInR5DXYKOhH2', 'PETUGAS_JPL', NOW(), NOW(), NULL, NULL),
('c5f3a4d6-de78-4c9a-b234-5e6f7a8b9c0d', 'MAS001', 'Andi Masinis', '$2a$10$wT8m9o3/XvQzj5YvD5176ea3Zk.g9XN15TInwXG3HInR5DXYKOhH2', 'MASINIS',     NOW(), NOW(), NULL, NULL)
ON CONFLICT ("nrp") DO NOTHING;

-- ---------------------------------------------------------------------------
-- OCC (Operations Control Centre)
-- ---------------------------------------------------------------------------
INSERT INTO occ (id, occ_name, occ_latitude, occ_longitude, created_at, updated_at) VALUES
('d7a4b5e6-ef89-4dab-c345-6f7a8b9c0d1e', 'OCC Bandung', -6.9175, 107.6191, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- STATION
-- ---------------------------------------------------------------------------
INSERT INTO station (id, station_name, station_code, station_address, station_latitude, station_longitude, station_status, heading, occ_id, station_index, created_at, updated_at) VALUES
('e8b5c6f7-fa9a-4ebc-d456-7a8b9c0d1e2f', 'Stasiun Bandung',      'BD',  'Jl. Stasiun Selatan No. 25, Bandung',  -6.9135, 107.6003, 'ACTIVE',   0,   'd7a4b5e6-ef89-4dab-c345-6f7a8b9c0d1e', 1, NOW(), NOW()),
('f9c6d7a8-ab0b-4fcd-e567-8b9c0d1e2f3a', 'Stasiun Cimekar',      'CMK', 'Jl. Raya Cibiru No. 1, Bandung',       -6.9242, 107.7215, 'ACTIVE',   90,  'd7a4b5e6-ef89-4dab-c345-6f7a8b9c0d1e', 2, NOW(), NOW()),
('1ad7e8b9-bc1c-4ade-f678-9c0d1e2f3a4b', 'Stasiun Gedebage',     'GDB', 'Jl. Soekarno-Hatta No. 800, Bandung',  -6.9352, 107.6980, 'ACTIVE',   90,  'd7a4b5e6-ef89-4dab-c345-6f7a8b9c0d1e', 3, NOW(), NOW()),
('2be8f9ca-cd2d-4bef-a789-0d1e2f3a4b5c', 'Stasiun Kiaracondong', 'KAC', 'Jl. Babakan Sari No. 1, Bandung',      -6.9248, 107.6410, 'ACTIVE',   270, 'd7a4b5e6-ef89-4dab-c345-6f7a8b9c0d1e', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- JPL (Jalan Perlintasan Liar / Level Crossing)
-- ---------------------------------------------------------------------------
INSERT INTO jpl (id, jpl_name, jpl_address, jpl_status, station_id, jpl_latitude, jpl_longitude, heading, created_at, updated_at) VALUES
('3cf9a0db-de3e-4cf0-b89a-1e2f3a4b5c6d', 'JPL 180 Cimekar',      'Jl. Cibiru Raya KM 12, Bandung',      'ACTIVE',   'f9c6d7a8-ab0b-4fcd-e567-8b9c0d1e2f3a', -6.9240, 107.7210, 90,  NOW(), NOW()),
('4da0b1ec-ef4f-4da1-c9ab-2f3a4b5c6d7e', 'JPL 181 Gedebage',     'Jl. Soekarno-Hatta KM 5, Bandung',   'ACTIVE',   '1ad7e8b9-bc1c-4ade-f678-9c0d1e2f3a4b', -6.9350, 107.6975, 90,  NOW(), NOW()),
('5eb1c2fd-fa5a-4eb2-dabc-3a4b5c6d7e8f', 'JPL 182 Kiaracondong', 'Jl. Babakan Sari KM 2, Bandung',     'INACTIVE', '2be8f9ca-cd2d-4bef-a789-0d1e2f3a4b5c', -6.9245, 107.6405, 270, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- ROUTE (category: INTERCITY | LOCAL | COMMUTER | LRT | WHOOSH)
-- ---------------------------------------------------------------------------
INSERT INTO route (id, route_code, route_distance, category, is_active, from_station_name, to_station_name, created_at, updated_at) VALUES
('6fc2d3ae-ab6b-4fc3-ebcd-4b5c6d7e8f9a', 'COMMUTER-BD-CMK', 25.5, 'COMMUTER', true, 'Stasiun Bandung', 'Stasiun Cimekar',  NOW(), NOW()),
('7ad3e4bf-bc7c-4ad4-fcde-5c6d7e8f9a0b', 'COMMUTER-BD-GDB', 18.0, 'COMMUTER', true, 'Stasiun Bandung', 'Stasiun Gedebage', NOW(), NOW())
ON CONFLICT (route_code) DO NOTHING;

-- ---------------------------------------------------------------------------
-- ROUTE SEGMENT
-- ---------------------------------------------------------------------------
INSERT INTO route_segment (id, route_segment_code, from_station_id, to_station_id, route_duration, route_distance, route_status, created_at, updated_at) VALUES
('8be4f5ca-cd8d-4be5-adef-6d7e8f9a0b1c', 'SEG-BD-KAC',  'e8b5c6f7-fa9a-4ebc-d456-7a8b9c0d1e2f', '2be8f9ca-cd2d-4bef-a789-0d1e2f3a4b5c', 8,  7.5,  'ACTIVE', NOW(), NOW()),
('9cf5a6db-de9e-4cf6-bef0-7e8f9a0b1c2d', 'SEG-KAC-CMK', '2be8f9ca-cd2d-4bef-a789-0d1e2f3a4b5c', 'f9c6d7a8-ab0b-4fcd-e567-8b9c0d1e2f3a', 12, 10.5, 'ACTIVE', NOW(), NOW()),
('0da6b7ec-efa0-4da7-cf01-8f9a0b1c2d3e', 'SEG-BD-GDB',  'e8b5c6f7-fa9a-4ebc-d456-7a8b9c0d1e2f', '1ad7e8b9-bc1c-4ade-f678-9c0d1e2f3a4b', 15, 18.0, 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- ROUTE SEGMENT ORDER (links segments to routes with ordering)
-- ---------------------------------------------------------------------------
INSERT INTO route_segment_order (route_id, route_segment_id, segment_index) VALUES
('6fc2d3ae-ab6b-4fc3-ebcd-4b5c6d7e8f9a', '8be4f5ca-cd8d-4be5-adef-6d7e8f9a0b1c', 1),
('6fc2d3ae-ab6b-4fc3-ebcd-4b5c6d7e8f9a', '9cf5a6db-de9e-4cf6-bef0-7e8f9a0b1c2d', 2),
('7ad3e4bf-bc7c-4ad4-fcde-5c6d7e8f9a0b', '0da6b7ec-efa0-4da7-cf01-8f9a0b1c2d3e', 1)
ON CONFLICT (route_id, route_segment_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- ROUTE SEGMENT JPL (many-to-many: which JPLs lie along each segment)
-- ---------------------------------------------------------------------------
INSERT INTO route_segment_jpl (route_segment_id, jpl_id) VALUES
('9cf5a6db-de9e-4cf6-bef0-7e8f9a0b1c2d', '3cf9a0db-de3e-4cf0-b89a-1e2f3a4b5c6d'),
('0da6b7ec-efa0-4da7-cf01-8f9a0b1c2d3e', '4da0b1ec-ef4f-4da1-c9ab-2f3a4b5c6d7e')
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- TRAIN (category: INTERCITY | LOCAL | COMMUTER | LRT | WHOOSH)
-- ---------------------------------------------------------------------------
INSERT INTO train (id, train_name, train_code, train_network_ip, train_status, train_online, category, train_last_known_latitude, train_last_known_longitude, route_id, created_at, updated_at) VALUES
('1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', 'KA Bandung Raya 1', 'KBR-01', '192.168.10.11', 'ACTIVE', true,  'COMMUTER', -6.9135, 107.6003, '6fc2d3ae-ab6b-4fc3-ebcd-4b5c6d7e8f9a', NOW(), NOW()),
('2fc8d9ae-ab2c-4fc9-e123-0b1c2d3e4f5a', 'KA Bandung Raya 2', 'KBR-02', '192.168.10.12', 'ACTIVE', false, 'COMMUTER', -6.9248, 107.6410, '6fc2d3ae-ab6b-4fc3-ebcd-4b5c6d7e8f9a', NOW(), NOW()),
('3ad9eabf-bc3d-4ada-f234-1c2d3e4f5a6b', 'KA Commuter Timur', 'KCT-01', '192.168.10.13', 'ACTIVE', true,  'COMMUTER', -6.9180, 107.6800, '7ad3e4bf-bc7c-4ad4-fcde-5c6d7e8f9a0b', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- TRIP
-- ---------------------------------------------------------------------------
INSERT INTO trip (id, train_id, route_id, is_flow, start_time, end_time, created_at) VALUES
('4beafbc0-cd4e-4beb-a345-2d3e4f5a6b7c', '1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', '6fc2d3ae-ab6b-4fc3-ebcd-4b5c6d7e8f9a', true,  NOW() - INTERVAL '2 hours', NOW() - INTERVAL '30 minutes', NOW() - INTERVAL '2 hours'),
('5cfbfcd1-de5f-4cfc-b456-3e4f5a6b7c8d', '2fc8d9ae-ab2c-4fc9-e123-0b1c2d3e4f5a', '6fc2d3ae-ab6b-4fc3-ebcd-4b5c6d7e8f9a', false, NOW() - INTERVAL '1 hour',  NULL,                           NOW() - INTERVAL '1 hour'),
('6dacade2-ef6a-4dad-c567-4f5a6b7c8d9e', '3ad9eabf-bc3d-4ada-f234-1c2d3e4f5a6b', '7ad3e4bf-bc7c-4ad4-fcde-5c6d7e8f9a0b', true,  NOW() - INTERVAL '3 hours', NOW() - INTERVAL '1 hour',       NOW() - INTERVAL '3 hours')
ON CONFLICT (id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- STATION SCHEDULE PLAN (direction: EAST | WEST)
-- ---------------------------------------------------------------------------
INSERT INTO station_schedule_plan (plan_id, train_id, station_id, arrival_plan, departure_plan, description, direction, created_at, updated_at) VALUES
('7ebdbef3-fa7b-4ebe-d678-5a6b7c8d9e0f', '1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', 'e8b5c6f7-fa9a-4ebc-d456-7a8b9c0d1e2f', NULL,                        NOW() + INTERVAL '10 minutes', 'Keberangkatan dari Bandung', 'EAST', NOW(), NOW()),
('8fcecfa4-ab8c-4fcf-e789-6b7c8d9e0f1a', '1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', '2be8f9ca-cd2d-4bef-a789-0d1e2f3a4b5c', NOW() + INTERVAL '18 minutes', NOW() + INTERVAL '20 minutes', 'Transit Kiaracondong',       'EAST', NOW(), NOW()),
('9adfd0b5-bc9d-4ad0-f89a-7c8d9e0f1a2b', '1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', 'f9c6d7a8-ab0b-4fcd-e567-8b9c0d1e2f3a', NOW() + INTERVAL '32 minutes', NOW() + INTERVAL '33 minutes', 'Tiba di Cimekar',            'EAST', NOW(), NOW()),
('0beae1c6-cdae-4be1-a9ab-8d9e0f1a2b3c', '2fc8d9ae-ab2c-4fc9-e123-0b1c2d3e4f5a', 'f9c6d7a8-ab0b-4fcd-e567-8b9c0d1e2f3a', NULL,                        NOW() + INTERVAL '5 minutes',  'Keberangkatan dari Cimekar', 'WEST', NOW(), NOW()),
('1cfbf2d7-debf-4cf2-babc-9e0f1a2b3c4d', '2fc8d9ae-ab2c-4fc9-e123-0b1c2d3e4f5a', 'e8b5c6f7-fa9a-4ebc-d456-7a8b9c0d1e2f', NOW() + INTERVAL '30 minutes', NOW() + INTERVAL '31 minutes', 'Tiba di Bandung',            'WEST', NOW(), NOW())
ON CONFLICT (plan_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- JPL SCHEDULE PLAN (direction: EAST | WEST)
-- ---------------------------------------------------------------------------
INSERT INTO jpl_schedule_plan (plan_id, train_id, jpl_id, estimated_pass_time, direction, created_at, updated_at) VALUES
('2dacf3e8-efca-4da3-cbcd-af1a2b3c4d5e', '1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', '3cf9a0db-de3e-4cf0-b89a-1e2f3a4b5c6d', NOW() + INTERVAL '25 minutes', 'EAST', NOW(), NOW()),
('3ebda4f9-fadb-4eb4-dcde-ba2b3c4d5e6f', '1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', '4da0b1ec-ef4f-4da1-c9ab-2f3a4b5c6d7e', NOW() + INTERVAL '28 minutes', 'EAST', NOW(), NOW()),
('4fceb50a-abec-4fc5-edfe-cb3c4d5e6f7a', '3ad9eabf-bc3d-4ada-f234-1c2d3e4f5a6b', '4da0b1ec-ef4f-4da1-c9ab-2f3a4b5c6d7e', NOW() + INTERVAL '45 minutes', 'EAST', NOW(), NOW())
ON CONFLICT (plan_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- REPORTS
-- ---------------------------------------------------------------------------
INSERT INTO reports (id, type, title, description, file_paths, latitude, longitude, train_id, jpl_id, is_read, created_at, updated_at) VALUES
('5adfc61b-bcfd-4af6-fe0f-dc4d5e6f7a8b', 'JPL',  'Orang Menyeberang',    'Terdeteksi 2 orang menyeberang saat palang tertutup.',   '', -6.9240, 107.7210, NULL,                               '3cf9a0db-de3e-4cf0-b89a-1e2f3a4b5c6d', false, NOW() - INTERVAL '1 hour',  NOW() - INTERVAL '1 hour'),
('6bead72c-cdae-4ba7-af1a-ed5e6f7a8b9c', 'LOCO', 'Rem Kurang Responsif', 'Masinis melaporkan respons rem terasa lebih lambat.',     '', -6.9180, 107.6800, '1eb7c8fd-fa1b-4eb8-d012-9a0b1c2d3e4f', NULL,                                   false, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours'),
('7cfbe83d-debf-4cb8-ba2b-fe6f7a8b9c0d', 'JPL',  'Kendaraan Mogok',      'Motor mogok di perlintasan, palang tidak bisa ditutup.', '', -6.9350, 107.6975, NULL,                               '4da0b1ec-ef4f-4da1-c9ab-2f3a4b5c6d7e', true,  NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;