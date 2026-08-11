--- tạo Database ------------------------------------
CREATE DATABASE SmartHelpdeskDB
ON PRIMARY 
(
    NAME = SmartHelpdesk_Data,
    FILENAME = 'D:\Trung tâm PTIT-KITS\BZCom\Fullstack\Database\SmartHelpdeskDB.mdf', -- File chứa dữ liệu
    SIZE = 10MB,
    MAXSIZE = 100MB,
    FILEGROWTH = 5MB
)
LOG ON
(
    NAME = SmartHelpdesk_Log,
    FILENAME = 'D:\Trung tâm PTIT-KITS\BZCom\Fullstack\Database\SmartHelpdeskDB.ldf', -- File nhật ký (log)
    SIZE = 5MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
);
GO

-- Tạo bảng Danh sách Công ty (Companies)------------------------------------------------
CREATE TABLE Companies (
    -- Mã công ty: Khóa chính, người dùng tự nhập/mặc định (không tự tăng), tối đa 20 ký tự
    company_id VARCHAR(20) PRIMARY KEY, 
    -- Tên công ty: Bắt buộc phải nhập (NOT NULL), hỗ trợ tiếng Việt có dấu (NVARCHAR)
    company_name NVARCHAR(200) NOT NULL, 
    -- Địa chỉ công ty: Tùy chọn, hỗ trợ tiếng Việt có dấu
    address NVARCHAR(500), 
    -- Số điện thoại: Tùy chọn, chỉ lưu chuỗi số
    phone VARCHAR(20)
);
GO

-- Thêm 3 công ty khách hàng/đối tác Hàn Quốc của Bzcom
INSERT INTO Companies (company_id, company_name, address, phone)
VALUES 
    ('KR_CLIENT_Ss', N'Samsung C&T Corporation', N'Seoul, South Korea', '+82-2145-1114'),
    ('KR_CLIENT_Nv', N'Naver Financial Corp.', N'Seongnam-si, Gyeonggi-do, South Korea', '+82-1588-3820'),
    ('KR_CLIENT_Kk', N'Kakao Mobility Corp.', N'Seongnam-si, Gyeonggi-do, South Korea', '+82-1599-9400');
GO

-- Tạo bảng Người dùng / Thành viên (Members)---------------------------------------------
CREATE TABLE Members (
    -- Tự đặt ID, không tự tăng, KHÔNG TRÙNG
    member_id VARCHAR(20) PRIMARY KEY, -- Tự đặt ID, không tự tăng, KHÔNG TRÙNG
    -- Mã công ty: Khóa ngoại liên kết tới bảng Companies (có thể NULL nếu là DEV/Admin nội bộ)
    company_id VARCHAR(20),
    -- Thông tin cá nhân
    name NVARCHAR(100) NOT NULL,            -- Họ tên, hỗ trợ tiếng Việt
    email VARCHAR(100) NOT NULL UNIQUE,     -- Email dùng để đăng nhập, không được trùng
    phone VARCHAR(20),                      -- Số điện thoại
    password VARCHAR(255) NOT NULL,         -- Mật khẩu
    
    -- Phân quyền & Trạng thái
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'client', 'dev')), -- Ràng buộc chỉ nhận 1 trong 3 quyền
    status VARCHAR(20) DEFAULT 'active',    -- Trạng thái tài khoản (active, inactive, pending...)
    
    -- Thời gian hệ thống
    created_at DATETIME DEFAULT GETDATE(),  -- Thời điểm tạo
    updated_at DATETIME DEFAULT GETDATE(),  -- Thời điểm cập nhật gần nhất
    deleted_at DATETIME NULL,               -- Thời điểm xóa mềm (nếu có)
    is_deleted BIT DEFAULT 0,               -- Đánh dấu xóa mềm: 0 = chưa xóa, 1 = đã xóa (tương ứng 0/1)
    
    -- Tạo liên kết Khóa ngoại tới bảng Companies
    CONSTRAINT FK_Members_Companies FOREIGN KEY (company_id) REFERENCES Companies(company_id) 
        -- Nếu xóa 1 trong 3 cty thì tất cả nhân viên thì tất cả id cty chuyển về NULL
        ON DELETE SET NULL 
        -- Nếu thay đổi mã cty thì lập tức thay đổi theo
        ON UPDATE CASCADE
);
GO

-- Thêm dữ liệu mẫu ban đầu cho bảng Members (Gồm Admin, Dev, Client các công ty)
INSERT INTO Members (member_id, company_id, name, email, phone, password, role, status)
VALUES 
    ('MB_ADM01', NULL, N'Bùi Văn Admin', 'admin@bzcom.com', '0901234567', '$2a$10$765.1zZpG1t0bZz9gK.H.uX.jH1jF3f.q9gq7a.g1k9', 'admin', 'active'),
    ('MB_DEV01', NULL, N'Nguyễn Văn Dev 1', 'dev1@bzcom.com', '0902345678', '$2a$10$765.1zZpG1t0bZz9gK.H.uX.jH1jF3f.q9gq7a.g1k9', 'dev', 'active'),
    ('MB_DEV02', NULL, N'Trần Thị Dev 2', 'dev2@bzcom.com', '0903456789', '$2a$10$765.1zZpG1t0bZz9gK.H.uX.jH1jF3f.q9gq7a.g1k9', 'dev', 'active'),
    ('MB_CLI01', 'KR_CLIENT_Ss', N'Kim Min-jun', 'client.samsung@samsung.com', '+82-10-1234-5678', '$2a$10$765.1zZpG1t0bZz9gK.H.uX.jH1jF3f.q9gq7a.g1k9', 'client', 'active'),
    ('MB_CLI02', 'KR_CLIENT_Nv', N'Park Ji-won', 'client.naver@navercorp.com', '+82-10-2345-6789', '$2a$10$765.1zZpG1t0bZz9gK.H.uX.jH1jF3f.q9gq7a.g1k9', 'client', 'active'),
    ('MB_CLI03', 'KR_CLIENT_Kk', N'Lee Seo-yeon', 'client.kakao@kakaocorp.com', '+82-10-3456-7890', '$2a$10$765.1zZpG1t0bZz9gK.H.uX.jH1jF3f.q9gq7a.g1k9', 'client', 'active');
GO

-- Tạo bảng Yêu cầu / Request chính (Requests)-----------------------------------------
CREATE TABLE Requests (
    -- Mã yêu cầu: Khóa chính
    request_id VARCHAR(20) PRIMARY KEY,
    -- Mã công ty gửi request: Khóa ngoại liên kết tới bảng Companies
    company_id VARCHAR(20) NOT NULL,
    -- Nội dung yêu cầu
    title NVARCHAR(255) NOT NULL,                  -- Tiêu đề request, hỗ trợ tiếng Việt
    description NVARCHAR(MAX),                     -- Mô tả chi tiết 
    
    -- Phân loại & Mức độ ưu tiên & Trạng thái (Ràng buộc bằng CHECK)
    category VARCHAR(20) NOT NULL CHECK (category IN ('BUG', 'FEATURE', 'INQUIRY')),                    -- Loại
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),       -- Ưu tiên
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'DONE')), -- Trạng thái
    
    -- Người tạo và Dev xử lý (đều tham chiếu tới member_id (kểu INT) của bảng Members)
    client_id VARCHAR(20) NOT NULL,                        -- ID khách hàng tạo request
    assigned_developer_id VARCHAR(20) NULL,                -- ID dev được giao xử lý (có thể NULL khi chưa có ai nhận)
    
    -- Thời gian hệ thống 
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    
    -- --- CÁC RÀNG BUỘC KHÓA NGOẠI (FOREIGN KEYS) ---
    -- CONSTRAINT: kiểm tra dữ liệu hợp lệ tương ứng bảng Companies ? và cập nhật nếu thay đổi
    -- 1. Nối tới bảng Companies
    CONSTRAINT FK_Requests_Companies FOREIGN KEY (company_id) 
        REFERENCES Companies(company_id) ON UPDATE CASCADE,
        
    -- 2. Nối client_id tới member_id trong bảng Members
    CONSTRAINT FK_Requests_Client FOREIGN KEY (client_id) 
        REFERENCES Members(member_id),
        
    -- 3. Nối assigned_developer_id tới member_id trong bảng Members
    CONSTRAINT FK_Requests_Developer FOREIGN KEY (assigned_developer_id) 
        REFERENCES Members(member_id)
);
GO

-- Tạo bảng Lưu lịch sử Request (RequestHistories)----------------------------------
CREATE TABLE RequestHistories (
    -- Mã lịch sử: Khóa chính
    history_id VARCHAR(20) PRIMARY KEY,
    
    -- Mã request và Mã người thực hiện thay đổi
    request_id VARCHAR(20) NOT NULL,                        -- Khóa ngoại nối tới bảng Requests
    changed_by VARCHAR(20) NOT NULL,                        -- Khóa ngoại nối tới member_id (bảng Members)
    
    -- Hành động thực hiện (Ràng buộc)
    action VARCHAR(20) NOT NULL CHECK (action IN ('CREATE', 'ASSIGN', 'UPDATE', 'CLOSE', 'REOPEN')),
    
    -- Trạng thái cũ và trạng thái mới (Có thể NULL nếu hành động không đổi status, ví dụ: cập nhật nội dung)
    from_status VARCHAR(20) NULL,
    to_status VARCHAR(20) NULL,
    
    -- Ghi chú / Lý do thay đổi
    memo NVARCHAR(MAX) NULL,
    
    -- Thời gian thực hiện thay đổi (thời gian hiện tại)
    changed_at DATETIME DEFAULT GETDATE(),
    
    -- --- CÁC RÀNG BUỘC KHÓA NGOẠI (FOREIGN KEYS) ---
    
    -- 1. Nối tới bảng Requests (Nếu xóa Request thì tự động xóa hết Lịch sử liên quan)
    CONSTRAINT FK_Histories_Requests FOREIGN KEY (request_id) 
        REFERENCES Requests(request_id) ON DELETE CASCADE,
        
    -- 2. Nối tới bảng Members (Biết ai là người vừa bấm sửa/thay đổi)
    CONSTRAINT FK_Histories_Members FOREIGN KEY (changed_by) 
        REFERENCES Members(member_id)
);
GO

-- Tạo bảng Thông báo (Alerts)--------------------------------------------
CREATE TABLE Alerts (
    -- Mã thông báo: Khóa chính 
    alert_id VARCHAR(20) PRIMARY KEY,
    
    -- Mã request liên quan và Người nhận thông báo
    request_id VARCHAR(20) NOT NULL,                        -- Khóa ngoại nối tới bảng Requests
    target_member_id VARCHAR(20) NOT NULL,                  -- Khóa ngoại nối tới member_id (người nhận thông báo)
    
    -- Loại thông báo (Ràng buộc bằng CHECK)
    alert_type VARCHAR(30) NOT NULL CHECK (alert_type IN ('ASSIGNED', 'STATUS_CHANGED', 'HIGH_PRIORITY_REGISTERED')),
    
    -- Nội dung thông báo & Trạng thái đã đọc
    message NVARCHAR(MAX) NOT NULL,                 -- Nội dung hiển thị thông báo
    is_read BIT DEFAULT 0,                          -- 0 = Chưa đọc, 1 = Đã đọc 
    
    -- Thời gian tạo thông báo
    created_at DATETIME DEFAULT GETDATE(),
    
    -- --- CÁC RÀNG BUỘC KHÓA NGOẠI (FOREIGN KEYS) ---
    
    -- 1. Nối tới bảng Requests (Nếu xóa Request thì xóa luôn các thông báo liên quan)
    CONSTRAINT FK_Alerts_Requests FOREIGN KEY (request_id) 
        REFERENCES Requests(request_id) ON DELETE CASCADE,
        
    -- 2. Nối target_member_id tới member_id trong bảng Members
    CONSTRAINT FK_Alerts_Members FOREIGN KEY (target_member_id) 
        REFERENCES Members(member_id)
);
GO

-- Tạo bảng Lịch sử Chat / Tin nhắn (ChatMessages) --------------------------
CREATE TABLE ChatMessages (
    -- Mã tin nhắn: Khóa chính 
    chat_id VARCHAR(20) PRIMARY KEY,
    
    -- Mã request liên quan và Người gửi tin nhắn (sửa reqest_id bị viết sai chính tả thành request_id)
    request_id VARCHAR(20) NOT NULL,                        -- Khóa ngoại nối tới bảng Requests
    sender_id VARCHAR(20) NOT NULL,                         -- Khóa ngoại nối tới member_id (người gửi tin nhắn)
    
    -- Nội dung tin nhắn & Loại tin nhắn
    message NVARCHAR(MAX) NOT NULL,                 -- Nội dung tin nhắn
    message_type VARCHAR(20) DEFAULT 'TEXT',        -- Loại tin nhắn: TEXT, FILE, IMAGE... (mặc định là TEXT)
    
    -- Trạng thái & Thời gian
    is_read BIT DEFAULT 0,                          -- 0 = Chưa đọc, 1 = Đã đọc
    created_at DATETIME DEFAULT GETDATE(),          -- Thời điểm gửi tin nhắn
    
    -- --- CÁC RÀNG BUỘC KHÓA NGOẠI (FOREIGN KEYS) ---
    
    -- 1. Nối tới bảng Requests (Nếu xóa Request thì xóa luôn lịch sử chat thuộc về Request đó)
    CONSTRAINT FK_ChatMessages_Requests FOREIGN KEY (request_id) 
        REFERENCES Requests(request_id) ON DELETE CASCADE,
        
    -- 2. Nối sender_id tới member_id trong bảng Members
    CONSTRAINT FK_ChatMessages_Members FOREIGN KEY (sender_id) 
        REFERENCES Members(member_id)
);
GO