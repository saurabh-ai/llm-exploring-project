-- Sample data for inventory management system
-- Version: 1.0
-- Created: 2024

-- Insert sample users
INSERT INTO users (username, email, role) VALUES
('admin', 'admin@inventory.com', 'ADMIN'),
('manager1', 'manager1@inventory.com', 'MANAGER'),
('employee1', 'employee1@inventory.com', 'EMPLOYEE'),
('employee2', 'employee2@inventory.com', 'EMPLOYEE');

-- Insert sample categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and components'),
('Office Supplies', 'General office supplies and stationery'),
('Furniture', 'Office and home furniture'),
('Software', 'Software licenses and applications');

-- Insert sample suppliers
INSERT INTO suppliers (name, contact_person, email, phone, address) VALUES
('TechCorp Solutions', 'John Smith', 'john.smith@techcorp.com', '555-0101', '123 Tech Street, Silicon Valley, CA'),
('Office Plus Ltd', 'Sarah Johnson', 'sarah@officeplus.com', '555-0102', '456 Business Ave, New York, NY'),
('Furniture World', 'Mike Brown', 'mike@furnitureworld.com', '555-0103', '789 Furniture Blvd, Chicago, IL'),
('Software Systems Inc', 'Lisa Davis', 'lisa@softwaresys.com', '555-0104', '321 Software Lane, Austin, TX');

-- Insert sample products
INSERT INTO products (name, description, sku, category_id, supplier_id, unit_price, cost_price, quantity_in_stock, reorder_level) VALUES
('Laptop Computer', 'High-performance business laptop', 'TECH-LAP-001', 1, 1, 1299.99, 899.99, 15, 5),
('Wireless Mouse', 'Ergonomic wireless optical mouse', 'TECH-MOU-001', 1, 1, 29.99, 15.99, 50, 10),
('A4 Paper Pack', '500-sheet pack of A4 printing paper', 'OFF-PAP-001', 2, 2, 12.99, 7.99, 100, 20),
('Office Desk', 'Adjustable height office desk', 'FUR-DSK-001', 3, 3, 599.99, 399.99, 8, 3),
('Antivirus Software', 'Enterprise antivirus software license', 'SOF-ANT-001', 4, 4, 79.99, 49.99, 25, 5);

-- Insert sample inventory records
INSERT INTO inventory (product_id, quantity_on_hand, quantity_allocated, location) VALUES
(1, 15, 2, 'MAIN'),
(2, 50, 5, 'MAIN'),
(3, 100, 10, 'MAIN'),
(4, 8, 1, 'MAIN'),
(5, 25, 3, 'MAIN');

-- Insert sample inventory transactions
INSERT INTO inventory_transactions (product_id, transaction_type, quantity, reason, user_id) VALUES
(1, 'IN', 20, 'Initial stock purchase', 1),
(1, 'OUT', 5, 'Sales order fulfillment', 2),
(2, 'IN', 50, 'Initial stock purchase', 1),
(3, 'IN', 100, 'Bulk purchase for office supplies', 1),
(3, 'OUT', 10, 'Office usage', 3),
(4, 'IN', 10, 'New furniture delivery', 1),
(4, 'OUT', 2, 'Office setup', 2),
(5, 'IN', 30, 'Software license purchase', 1),
(5, 'OUT', 5, 'License deployment', 3);

-- Insert sample purchase orders
INSERT INTO purchase_orders (supplier_id, po_number, status, total_amount, order_date, expected_date) VALUES
(1, 'PO-001', 'RECEIVED', 21499.80, '2024-01-15', '2024-01-22'),
(2, 'PO-002', 'CONFIRMED', 1299.00, '2024-01-20', '2024-01-27'),
(3, 'PO-003', 'DRAFT', 5999.90, '2024-01-25', '2024-02-05');

-- Insert sample purchase order items
INSERT INTO purchase_order_items (po_id, product_id, quantity_ordered, quantity_received, unit_cost) VALUES
(1, 1, 15, 15, 899.99),
(1, 2, 30, 30, 15.99),
(2, 3, 100, 100, 7.99),
(3, 4, 10, 0, 399.99);