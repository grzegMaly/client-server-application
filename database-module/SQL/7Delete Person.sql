Use TeamLinkDB;
GO

Create Or Alter Procedure DeleteUserByID
	@id uniqueidentifier,
	@statusCode int OUTPUT
AS
BEGIN
	
	IF NOT EXISTS (Select 1 From Employees Where id = @id)
		BEGIN
			SET @statusCode = 1;
			RETURN;
		END

	BEGIN TRAN

		Delete From Tasks
		Where assignedTo = @id OR createdBy = @id

		DELETE FROM Employees
		Where id = @id

		IF @@ERROR <> 0
			BEGIN
				ROLLBACK;
				SET @statusCode = 3;
				RETURN;
			END
		ELSE
			BEGIN
				COMMIT
				SET @statusCode = 0;
			END
END