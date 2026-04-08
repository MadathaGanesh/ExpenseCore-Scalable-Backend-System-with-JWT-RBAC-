
use expense_tracker_application;

select * from tbl_blacklisted_tokens;

select * from tbl_category;

select * from tbl_expenses;

select * from tbl_permission;

select * from tbl_role;

select * from tbl_role_permissions;

select * from tbl_users;

select * from user_roles;

select * from tbl_auditlogs;

select * from tbl_refresh_token;

INSERT INTO tbl_role (role_id, name) VALUES
(1, 'ROLE_USER');

INSERT INTO tbl_permission (permission_id, name) VALUES

-- Expense Permissions
(1, 'READ_EXPENSE'),
(2, 'CREATE_EXPENSE'),
(3, 'UPDATE_EXPENSE'),
(4, 'DELETE_EXPENSE'),

-- Category Permissions
(5, 'READ_CATEGORY'),
(6, 'CREATE_CATEGORY'),
(7, 'UPDATE_CATEGORY'),
(8, 'DELETE_CATEGORY'),

-- User/Profile Permissions
(9, 'READ_USER'),
(10, 'UPDATE_USER'),
(11, 'DELETE_USER'),

-- Admin Level Permissions
(12, 'READ_ALL_USERS'),
(13, 'READ_ALL_EXPENSES'),
(14, 'MANAGE_ROLES');

-- ---------------------------------------------
-- for USER (Role and permission Mapping)
INSERT INTO tbl_role_permissions (role_role_id, permissions_permission_id) VALUES

-- Expense
(1,1),(1,2),(1,3),(1,4),

-- Category
(1,5),(1,6),(1,7),(1,8),

-- User (own profile)
(1,9),(1,10);
-- ---------------------------------------------

-- For ADMIN (Providing full access)
INSERT INTO tbl_role_permissions (role_role_id, permissions_permission_id) VALUES

-- Expense
(2,1),(2,2),(2,3),(2,4),

-- Category
(2,5),(2,6),(2,7),(2,8),

-- User
(2,9),(2,10),(2,11),

-- Admin special
(2,12),(2,13),(2,14);

-- --------------------
-- Query to check Mapping for (User and Permission) and (Admin and Permission)
SELECT r.name AS role, p.name AS permission
FROM tbl_role r
JOIN tbl_role_permissions rp ON r.role_id = rp.role_role_id
JOIN tbl_permission p ON p.permission_id = rp.permissions_permission_id
ORDER BY r.name;
