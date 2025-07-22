-- Initialize Database for Distributed Task Scheduler System
-- This script creates the database schemas as specified in the problem statement

-- Create databases for different services
CREATE DATABASE user_db;
CREATE DATABASE scheduler_db;
CREATE DATABASE executor_db;
CREATE DATABASE notification_db;

-- Connect to user_db to create users table
\c user_db;

-- Users table for User Service
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Connect to scheduler_db to create scheduled tasks table
\c scheduler_db;

-- Scheduled Tasks table for Scheduler Service
CREATE TABLE scheduled_tasks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    cron_expression VARCHAR(50) NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    payload JSONB,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    priority VARCHAR(10) DEFAULT 'MEDIUM',
    max_retries INTEGER DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_scheduled_tasks_user_id ON scheduled_tasks(user_id);
CREATE INDEX idx_scheduled_tasks_is_active ON scheduled_tasks(is_active);
CREATE INDEX idx_scheduled_tasks_task_type ON scheduled_tasks(task_type);
CREATE INDEX idx_scheduled_tasks_priority ON scheduled_tasks(priority);

-- Connect to executor_db to create job executions table
\c executor_db;

-- Job Executions table for Executor Service
CREATE TABLE job_executions (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    execution_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, RUNNING, COMPLETED, FAILED
    result TEXT,
    error_message TEXT,
    duration_ms BIGINT,
    retry_count INTEGER DEFAULT 0,
    executor_instance VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_job_executions_task_id ON job_executions(task_id);
CREATE INDEX idx_job_executions_status ON job_executions(status);
CREATE INDEX idx_job_executions_execution_time ON job_executions(execution_time);
CREATE INDEX idx_job_executions_created_at ON job_executions(created_at);

-- Connect to notification_db to create notifications table
\c notification_db;

-- Notifications table for Notification Service
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL, -- EMAIL, SMS, PUSH
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    priority VARCHAR(10) DEFAULT 'NORMAL',
    scheduled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_recipient ON notifications(recipient);
CREATE INDEX idx_notifications_scheduled_at ON notifications(scheduled_at);
CREATE INDEX idx_notifications_priority ON notifications(priority);

-- Insert some sample data for testing
\c user_db;

-- Sample users (passwords are bcrypt hashed for "password123")
INSERT INTO users (username, email, password_hash, role) VALUES 
('admin', 'admin@example.com', '$2a$10$9SbUgPnRMSkgwdCWJF3vHOdGb9WGYOlTCT3qPQKJCqb7OP3E3p/z6', 'ADMIN'),
('user1', 'user1@example.com', '$2a$10$9SbUgPnRMSkgwdCWJF3vHOdGb9WGYOlTCT3qPQKJCqb7OP3E3p/z6', 'USER'),
('user2', 'user2@example.com', '$2a$10$9SbUgPnRMSkgwdCWJF3vHOdGb9WGYOlTCT3qPQKJCqb7OP3E3p/z6', 'USER');

\c scheduler_db;

-- Sample scheduled tasks
INSERT INTO scheduled_tasks (name, description, cron_expression, task_type, user_id, priority) VALUES 
('Daily Report', 'Generate daily system report', '0 0 9 * * ?', 'REPORT_GENERATION', 1, 'HIGH'),
('Email Cleanup', 'Clean up old email records', '0 0 2 * * ?', 'DATA_CLEANUP', 1, 'LOW'),
('Backup Task', 'Perform system backup', '0 0 1 * * ?', 'SYSTEM_BACKUP', 1, 'HIGH'),
('Health Check', 'System health monitoring', '*/15 * * * * ?', 'HEALTH_CHECK', 2, 'MEDIUM');

-- Grant permissions (in a real production setup, use more restrictive permissions)
-- These are just for development/testing purposes