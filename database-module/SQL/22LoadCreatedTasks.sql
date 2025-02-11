Use TeamLinkDB;
GO


Create or Alter Procedure LoadCreatedTasks
	@UserId uniqueidentifier
AS
BEGIN
	SET NOCOUNT ON;

	Select t.taskId,
		   t.title,
		   t.description,
		   t.createdBy,
		   t.assignedTo,
		   t.createdAt,
		   t.deadline,
		   ts.statusId
	From Tasks t
	Join TaskStatus ts ON t.statusId = ts.statusId
	Where t.createdBy = @UserId
END