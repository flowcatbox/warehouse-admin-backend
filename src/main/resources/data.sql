-- 初始化用户数据
INSERT INTO users (username, password, email, role, department, phone, status, create_time) VALUES
                                                                                      ('admin','123456', 'admin@example.com', 'admin', 'Technology', '13800138000', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('john_doe', '123456','john.doe@example.com', 'editor', 'Content', '13800138001', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('jane_smith', '123456','jane.smith@example.com', 'viewer', 'Marketing', '13800138002', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('alice_wang', '123456', 'alice.wang@example.com', 'editor', 'Design', '13800138003', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('bob_lee', '123456', 'bob.lee@example.com', 'viewer', 'Sales', '13800138004', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('charlie_chen', '123456', 'charlie.chen@example.com', 'admin', 'HR', '13800138005', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('david_zhao', '123456', 'david.zhao@example.com', 'editor', 'Finance', '13800138006', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('eva_li', '123456', 'eva.li@example.com', 'viewer', 'Support', '13800138007', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('emily_liu', '123456', 'emily.liu@example.com', 'viewer', 'Support', '13800138007', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('frank_wu', '123456', 'frank.wu@example.com', 'editor', 'Operations', '13800138008', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('grace_huang', '123456', 'grace.huang@example.com', 'admin', 'Technology', '13800138009', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('henry_lin', '123456', 'henry.lin@example.com', 'viewer', 'Content', '13800138010', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('isabel_zhang', '123456', 'isabel.zhang@example.com', 'editor', 'Marketing', '13800138011', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('jack_ma', '123456', 'jack.ma@example.com', 'viewer', 'Design', '13800138012', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('kelly_sun', '123456', 'kelly.sun@example.com', 'admin', 'Sales', '13800138013', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('leo_guo', '123456', 'leo.guo@example.com', 'editor', 'HR', '13800138014', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('mia_fan', '123456', 'mia.fan@example.com', 'viewer', 'Finance', '13800138015', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('nick_zhou', '123456', 'nick.zhou@example.com', 'admin', 'Support', '13800138016', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('olivia_he', '123456', 'olivia.he@example.com', 'editor', 'Operations', '13800138017', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('peter_xu', '123456', 'peter.xu@example.com', 'viewer', 'Technology', '13800138018', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('queen_li', '123456', 'queen.li@example.com', 'admin', 'Content', '13800138019', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('ryan_deng', '123456', 'ryan.deng@example.com', 'editor', 'Marketing', '13800138020', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('sophia_jin', '123456', 'sophia.jin@example.com', 'viewer', 'Design', '13800138021', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('tom_yang', '123456', 'tom.yang@example.com', 'admin', 'Sales', '13800138022', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('una_qi', '123456', 'una.qi@example.com', 'editor', 'HR', '13800138023', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('victor_pan', '123456', 'victor.pan@example.com', 'viewer', 'Finance', '13800138024', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('wendy_luo', '123456', 'wendy.luo@example.com', 'admin', 'Support', '13800138025', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('xavier_feng', '123456', 'xavier.feng@example.com', 'editor', 'Operations', '13800138026', 'ACTIVE', CURRENT_TIMESTAMP);


-- 初始化布草数据
INSERT INTO linen_items (item_id, description, on_hand, min_stock, max_stock, category, location, status, created_at, last_updated) VALUES
                                                                                                                                        ('LIN-001', 'Bed Sheet - King Size', 45, 50, 200, 'Bedding', 'Warehouse A', 'LOW_STOCK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                        ('LIN-002', 'Pillow Case - Standard', 120, 50, 300, 'Bedding', 'Warehouse A', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                        ('LIN-003', 'Bath Towel - Large', 85, 30, 150, 'Bath', 'Warehouse B', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);