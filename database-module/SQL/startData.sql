Use TeamLinkDB;

Insert Into Roles Values (0, 'EMPLOYEE'), (1, 'MANAGER'), (2, 'ADMIN');
Insert Into StatusCodes Values 
(0, 'Success'), (1, 'User Not Found'),
(2, 'Invalid Role ID'), (3, 'Operation Failed'),
(4, 'Missing Required Parameters'),
(5, 'Duplicate Found'),
(6, 'Chat Not Found'),
(7, 'Task Not Found'),
(8, 'Group Not Found')

Insert Into TaskStatus (statusName) Values
('PENDING'),
('IN_PROGRESS'),
('COMPLETED'),
('CANCELLED')