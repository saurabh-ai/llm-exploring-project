-- Sample data for the Task Management API

-- Insert sample users
INSERT INTO users (username, email, first_name, last_name, created_at, updated_at) VALUES
('john_doe', 'john.doe@example.com', 'John', 'Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane_smith', 'jane.smith@example.com', 'Jane', 'Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bob_wilson', 'bob.wilson@example.com', 'Bob', 'Wilson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample categories
INSERT INTO categories (name, description, color_code, created_at, updated_at) VALUES
('Work', 'Work-related tasks', '#FF6B6B', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Personal', 'Personal tasks and activities', '#4ECDC4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Learning', 'Educational and learning tasks', '#45B7D1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Health', 'Health and fitness related tasks', '#96CEB4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Shopping', 'Shopping and errands', '#FFEAA7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample tasks
INSERT INTO tasks (title, description, status, due_date, priority, user_id, category_id, created_at, updated_at) VALUES
('Complete Project Proposal', 'Finish the Q1 project proposal for client presentation', 'IN_PROGRESS', DATEADD('DAY', 7, CURRENT_TIMESTAMP), 3, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Review Code Changes', 'Review pull requests from team members', 'TODO', DATEADD('DAY', 3, CURRENT_TIMESTAMP), 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Team Meeting', 'Attend weekly team standup meeting', 'COMPLETED', DATEADD('DAY', -1, CURRENT_TIMESTAMP), 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Grocery Shopping', 'Buy groceries for the week', 'TODO', DATEADD('DAY', 2, CURRENT_TIMESTAMP), 1, 2, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Doctor Appointment', 'Annual health checkup appointment', 'TODO', DATEADD('DAY', 10, CURRENT_TIMESTAMP), 3, 2, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Read Java Book', 'Continue reading Effective Java book', 'IN_PROGRESS', DATEADD('DAY', 14, CURRENT_TIMESTAMP), 2, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Fix Database Bug', 'Investigate and fix the database connection issue', 'TODO', DATEADD('DAY', 5, CURRENT_TIMESTAMP), 3, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Prepare Presentation', 'Prepare slides for upcoming conference', 'IN_PROGRESS', DATEADD('DAY', 21, CURRENT_TIMESTAMP), 2, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Workout Session', 'Complete the weekly workout routine', 'COMPLETED', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1, 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Some overdue tasks for testing
('Update Documentation', 'Update API documentation with new endpoints', 'TODO', DATEADD('DAY', -3, CURRENT_TIMESTAMP), 2, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('File Tax Returns', 'Complete and file annual tax returns', 'IN_PROGRESS', DATEADD('DAY', -5, CURRENT_TIMESTAMP), 3, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);