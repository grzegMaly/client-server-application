Use TeamLinkDB;
GO

Create Or Alter Procedure UpdateTask
	@Task TaskData READONLY
AS
BEGIN
	SET NOCOUNT ON;

	IF NOT EXISTS (
		Select 1 From Tasks t
		Join @Task newT ON t.taskId = newT.taskId
		Where t.createdBy = newT.createdBy
	)
	BEGIN
		Select description AS Message
		From StatusCodes
		Where id = 1;
		RETURN;
	END

	BEGIN TRY
		BEGIN TRAN
		
			Update t
			SET t.title = newT.title,
				t.description = newT.description,
				t.deadline = newT.deadline,
				t.statusId = newT.statusId
			From Tasks t
			Join @Task newT ON t.taskId = newT.taskId
		
		Select * From Tasks t
		Join @Task nT ON t.taskId = nT.taskId;
		COMMIT
	END TRY
	BEGIN CATCH
		ROLLBACK;
		Select description as Message
		From StatusCodes
		Where id = 3;
	END CATCH
END