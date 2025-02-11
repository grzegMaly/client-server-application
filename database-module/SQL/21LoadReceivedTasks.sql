Use TeamLinkDB;
GO

Create Or Alter Procedure LoadReceivedTasks
	@userId uniqueidentifier
AS
BEGIN
	Set Nocount ON;

	Select 
		t.taskId, 
		t.title, 
		t.description, 
		t.createdBy, 
		t.assignedTo,
		t.createdAt,
		t.deadline,
		ts.statusId
	From Tasks t
	Join TaskStatus ts ON t.statusId = ts.statusId
	Where t.assignedTo = @userId;
END

Select * From Employees e
JOIN Roles r ON e.role = r.roleId
Where r.roleId = 1;

Select * From Employees e
JOIN Roles r ON e.role = r.roleId
Where r.roleId = 0;

Insert Into Tasks (title, description, createdBy, assignedTo, createdAt, deadline, statusId) values 
('Cosik', 'testowe', '915C6CE8-945F-41BC-8CF8-19CB556EF946', 'B4B4562D-065A-4AB9-A9D3-173F3EE8B858', GETDATE(), '2025-12-11', 1)

select * From Tasks