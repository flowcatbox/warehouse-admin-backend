-- 初始化用户数据
INSERT INTO users (username, email, role, department, phone, status, create_time) VALUES
                                                                                      ('admin', 'admin@example.com', 'admin', 'Technology', '13800138000', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('john_doe', 'john.doe@example.com', 'editor', 'Content', '13800138001', 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                      ('jane_smith', 'jane.smith@example.com', 'viewer', 'Marketing', '13800138002', 'ACTIVE', CURRENT_TIMESTAMP);

-- 初始化布草数据
INSERT INTO linen_items (item_id, description, on_hand, min_stock, max_stock, category, location, status, created_at, last_updated) VALUES
                                                                                                                                        ('LIN-001', 'Bed Sheet - King Size', 45, 50, 200, 'Bedding', 'Warehouse A', 'LOW_STOCK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                        ('LIN-002', 'Pillow Case - Standard', 120, 50, 300, 'Bedding', 'Warehouse A', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                        ('LIN-003', 'Bath Towel - Large', 85, 30, 150, 'Bath', 'Warehouse B', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);