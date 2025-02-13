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