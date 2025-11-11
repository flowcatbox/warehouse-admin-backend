-- 初始化用户数据
INSERT INTO users (username, password, email, role, department, phone, status, create_time) VALUES
                                                                                      ('admin','123456', 'admin@example.com', 'admin', 'Technology', '13800138000', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('john_doe', '123456','john.doe@example.com', 'editor', 'Content', '13800138001', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('jane_smith', '123456','jane.smith@example.com', 'viewer', 'Marketing', '13800138002', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('alice_wang', '123456', 'alice.wang@example.com', 'editor', 'Design', '13800138003', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('bob_lee', '123456', 'bob.lee@example.com', 'viewer', 'Sales', '13800138004', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('charlie_chen', '123456', 'charlie.chen@example.com', 'admin', 'HR', '13800138005', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('david_zhao', '123456', 'david.zhao@example.com', 'editor', 'Finance', '13800138006', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('eva_li', '123456', 'eva.li@example.com', 'viewer', 'Support', '13800138007', 'ACTIVE', CURRENT_TIMESTAMP);

-- 初始化布草数据
INSERT INTO linen_items (item_id, description, on_hand, min_stock, max_stock, category, location, status, created_at, last_updated) VALUES
                                                                                                                                        ('LIN-001', 'Bed Sheet - King Size', 45, 50, 200, 'Bedding', 'Warehouse A', 'LOW_STOCK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                        ('LIN-002', 'Pillow Case - Standard', 120, 50, 300, 'Bedding', 'Warehouse A', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                        ('LIN-003', 'Bath Towel - Large', 85, 30, 150, 'Bath', 'Warehouse B', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);