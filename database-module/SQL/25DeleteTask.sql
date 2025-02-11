Use TeamLinkDB;
GO

Create Or Alter Procedure DeleteTask
	@taskId uniqueidentifier,
	@userId uniqueidentifier,
	@statusCode int OUTPUT
AS
BEGIN
	
	Set NOCOUNT ON;

	IF NOT EXISTS (
        SELECT 1 FROM Tasks
        WHERE taskId = @taskId AND createdBy = @userId
    )
    BEGIN
        SET @statusCode = 7;
        RETURN;
    END;

	BEGIN TRY
		BEGIN TRAN

		Delete From Tasks
		Where taskId = @taskId;

		Set @statusCode = 0;
		COMMIT
	END TRY
	BEGIN CATCH
		ROLLBACK
		Set @statusCode = 3;
	END CATCH
END