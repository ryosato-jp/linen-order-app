USE linen_order_app;

-- 1) 施設
INSERT INTO facility (name) VALUES
('病院1');

-- 2) アイテムマスタ
INSERT INTO linen_item (name) VALUES
('布団'),
('枕'),
('ベッドパッド'),
('シーツ'),
('布団カバー'),
('枕カバー');

-- 3) ユーザー
INSERT IGNORE INTO users (login_id, password, facility_id) VALUES
('hospital1', 'pass1', 1);

-- 4) 施設×アイテム（基準在庫=定数）
INSERT INTO facility_linen (facility_id, linen_item_id, base_stock) VALUES
(1, 1, 50),
(1, 2, 100),
(1, 3, 10),
(1, 4, 1000),
(1, 5, 800),
(1, 6, 800);