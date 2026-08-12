-- =============================================================================
-- SMART HELPDESK SYSTEM - POSTGRESQL TABLE CREATION & SEED DATA SCRIPT (NEON.TECH)
-- =============================================================================

-- 1. Drop existing tables if they exist (in reverse order of dependency)
DROP TABLE IF EXISTS ChatMessages CASCADE;
DROP TABLE IF EXISTS Alerts CASCADE;
DROP TABLE IF EXISTS RequestHistories CASCADE;
DROP TABLE IF EXISTS Requests CASCADE;
DROP TABLE IF EXISTS Members CASCADE;
DROP TABLE IF EXISTS Companies CASCADE;

-- 2. Create Companies Table
CREATE TABLE Companies (
    company_id VARCHAR(20) PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(20)
);

-- Insert Sample Companies
INSERT INTO Companies (company_id, company_name, address, phone) VALUES 
    ('KR_CLIENT_Ss', 'Samsung C&T Corporation', 'Seoul, South Korea', '+82-2145-1114'),
    ('KR_CLIENT_Nv', 'Naver Financial Corp.', 'Seongnam-si, Gyeonggi-do, South Korea', '+82-1588-3820'),
    ('KR_CLIENT_Kk', 'Kakao Mobility Corp.', 'Seongnam-si, Gyeonggi-do, South Korea', '+82-1599-9400');

-- 3. Create Members Table (Fully matched with Spring Boot JPA Member entity)
CREATE TABLE Members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id VARCHAR(20) UNIQUE,
    company_id VARCHAR(20),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CLIENT', 'DEVELOPER', 'admin', 'client', 'dev')),
    status VARCHAR(20) DEFAULT 'active',
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    CONSTRAINT FK_Members_Companies FOREIGN KEY (company_id) REFERENCES Companies(company_id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Insert Sample Members Data (Password default: 'password123' hashed with BCrypt)
INSERT INTO Members (id, member_id, company_id, name, email, phone, password, role, status) VALUES 
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'MB_ADM01', NULL, 'Bùi Văn Admin', 'admin@bzcom.com', '0901234567', '$2a$10$e8W/hE2U6G6g2uHlqW0zje3xGjS.K1K4A/rO5E5lP0zje3xGjS.K1', 'ADMIN', 'active'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'MB_DEV01', NULL, 'Nguyễn Văn Dev 1', 'dev1@bzcom.com', '0902345678', '$2a$10$e8W/hE2U6G6g2uHlqW0zje3xGjS.K1K4A/rO5E5lP0zje3xGjS.K1', 'DEVELOPER', 'active'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'MB_DEV02', NULL, 'Trần Thị Dev 2', 'dev2@bzcom.com', '0903456789', '$2a$10$e8W/hE2U6G6g2uHlqW0zje3xGjS.K1K4A/rO5E5lP0zje3xGjS.K1', 'DEVELOPER', 'active'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'MB_CLI01', 'KR_CLIENT_Ss', 'Kim Min-jun', 'client.samsung@samsung.com', '+82-10-1234-5678', '$2a$10$e8W/hE2U6G6g2uHlqW0zje3xGjS.K1K4A/rO5E5lP0zje3xGjS.K1', 'CLIENT', 'active'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'MB_CLI02', 'KR_CLIENT_Nv', 'Park Ji-won', 'client.naver@navercorp.com', '+82-10-2345-6789', '$2a$10$e8W/hE2U6G6g2uHlqW0zje3xGjS.K1K4A/rO5E5lP0zje3xGjS.K1', 'CLIENT', 'active'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'MB_CLI03', 'KR_CLIENT_Kk', 'Lee Seo-yeon', 'client.kakao@kakaocorp.com', '+82-10-3456-7890', '$2a$10$e8W/hE2U6G6g2uHlqW0zje3xGjS.K1K4A/rO5E5lP0zje3xGjS.K1', 'CLIENT', 'active');

-- 4. Create Requests Table
CREATE TABLE Requests (
    request_id VARCHAR(20) PRIMARY KEY,
    company_id VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(20) NOT NULL CHECK (category IN ('BUG', 'FEATURE', 'INQUIRY')),
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'DONE')),
    client_id VARCHAR(20) NOT NULL,
    assigned_developer_id VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Requests_Companies FOREIGN KEY (company_id) REFERENCES Companies(company_id) ON UPDATE CASCADE,
    CONSTRAINT FK_Requests_Client FOREIGN KEY (client_id) REFERENCES Members(member_id),
    CONSTRAINT FK_Requests_Developer FOREIGN KEY (assigned_developer_id) REFERENCES Members(member_id)
);

-- Insert Sample Requests
INSERT INTO Requests (request_id, company_id, title, description, category, priority, status, client_id, assigned_developer_id) VALUES
    ('REQ_001', 'KR_CLIENT_Ss', 'Lỗi timeout API VNPay', 'Hệ thống thường xảy ra timeout khi kết nối cổng thanh toán VNPay', 'BUG', 'HIGH', 'IN_PROGRESS', 'MB_CLI01', 'MB_DEV01'),
    ('REQ_002', 'KR_CLIENT_Nv', 'Yêu cầu thêm xuất PDF', 'Cần thêm nút xuất file báo cáo định dạng PDF', 'FEATURE', 'MEDIUM', 'PENDING', 'MB_CLI02', NULL),
    ('REQ_003', 'KR_CLIENT_Kk', 'Thắc mắc bảo trì hệ thống', 'Hỏi thời gian cụ thể đợt bảo trì server cuối tuần', 'INQUIRY', 'LOW', 'DONE', 'MB_CLI03', 'MB_DEV02');

-- 5. Create RequestHistories Table
CREATE TABLE RequestHistories (
    history_id VARCHAR(20) PRIMARY KEY,
    request_id VARCHAR(20) NOT NULL,
    changed_by VARCHAR(20) NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('CREATE', 'ASSIGN', 'UPDATE', 'CLOSE', 'REOPEN')),
    from_status VARCHAR(20),
    to_status VARCHAR(20),
    memo TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Histories_Requests FOREIGN KEY (request_id) REFERENCES Requests(request_id) ON DELETE CASCADE,
    CONSTRAINT FK_Histories_Members FOREIGN KEY (changed_by) REFERENCES Members(member_id)
);

-- Insert Sample RequestHistories
INSERT INTO RequestHistories (history_id, request_id, changed_by, action, from_status, to_status, memo) VALUES
    ('HIS_001', 'REQ_001', 'MB_CLI01', 'CREATE', NULL, 'PENDING', 'Khách hàng tạo mới yêu cầu sự cố'),
    ('HIS_002', 'REQ_001', 'MB_ADM01', 'ASSIGN', 'PENDING', 'IN_PROGRESS', 'Phân công Nguyễn Văn Dev 1 xử lý');

-- 6. Create Alerts Table
CREATE TABLE Alerts (
    alert_id VARCHAR(20) PRIMARY KEY,
    request_id VARCHAR(20) NOT NULL,
    target_member_id VARCHAR(20) NOT NULL,
    alert_type VARCHAR(30) NOT NULL CHECK (alert_type IN ('ASSIGNED', 'STATUS_CHANGED', 'HIGH_PRIORITY_REGISTERED')),
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Alerts_Requests FOREIGN KEY (request_id) REFERENCES Requests(request_id) ON DELETE CASCADE,
    CONSTRAINT FK_Alerts_Members FOREIGN KEY (target_member_id) REFERENCES Members(member_id)
);

-- Insert Sample Alerts
INSERT INTO Alerts (alert_id, request_id, target_member_id, alert_type, message, is_read) VALUES
    ('ALT_001', 'REQ_001', 'MB_ADM01', 'HIGH_PRIORITY_REGISTERED', 'Có yêu cầu mức ưu tiên HIGH vừa tạo!', true),
    ('ALT_002', 'REQ_001', 'MB_DEV01', 'ASSIGNED', 'Bạn được phân công xử lý yêu cầu REQ_001', false);

-- 7. Create ChatMessages Table
CREATE TABLE ChatMessages (
    chat_id VARCHAR(20) PRIMARY KEY,
    request_id VARCHAR(20) NOT NULL,
    sender_id VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_ChatMessages_Requests FOREIGN KEY (request_id) REFERENCES Requests(request_id) ON DELETE CASCADE,
    CONSTRAINT FK_ChatMessages_Members FOREIGN KEY (sender_id) REFERENCES Members(member_id)
);

-- Insert Sample ChatMessages
INSERT INTO ChatMessages (chat_id, request_id, sender_id, message, message_type, is_read) VALUES
    ('MSG_001', 'REQ_001', 'MB_CLI01', 'Dạ chào Dev team, bên em bị lỗi timeout từ sáng nay.', 'TEXT', true),
    ('MSG_002', 'REQ_001', 'MB_DEV01', 'Chào anh Kim, em đang kiểm tra log gateway VNPay ạ.', 'TEXT', true);
