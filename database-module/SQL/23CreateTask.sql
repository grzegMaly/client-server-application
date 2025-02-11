Use TeamLinkDB;
GO

Create Or Alter Procedure CreateTask
	@Task TaskData READONLY
AS
BEGIN
	SET NOCOUNT ON;

	IF NOT EXISTS(
		Select 1 From Employees e
		Join @Task t ON e.id = t.createdBy
		Where e.role IN (1, 2)
	)
	BEGIN
		Select description AS Message
		From StatusCodes
		Where id = 1;
		RETURN;
	END

	IF NOT EXISTS(
		Select 1 From Employees
		Where id = (Select assignedTo From @Task)
	)
	BEGIN
		Select description AS Message
		From StatusCodes
		Where id = 1;
		RETURN;
	END

	BEGIN TRY
		BEGIN TRAN

			Declare @newId uniqueidentifier = NEWID();

			Insert Into Tasks (taskId, title, description, createdBy, assignedTo, createdAt, deadline, statusId)
			Select @newId, title, description, createdBy, assignedTo, GETDATE(), deadline, statusId From @Task
			
			Select * From Tasks Where taskId = @newId;
		COMMIT;
	END TRY
	BEGIN CATCH
		Select description AS Message From StatusCodes Where id = 3;
		ROLLBACK
	END CATCH
END