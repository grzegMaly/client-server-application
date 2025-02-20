Use TeamLinkDB;
GO

Create Or Alter Procedure DeleteGroupById
	@groupId uniqueidentifier,
	@statusCode int OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	IF NOT EXISTS (Select 1 From Groups Where id = @groupId)
	BEGIN
		Set @statusCode = 8;
		RETURN;
	END

	BEGIN TRY
		BEGIN TRAN

		Delete From Groups Where id = @groupId;
		Set @statusCode = 0;
		COMMIT;
	END TRY
	BEGIN CATCH
		ROLLBACK;	
		Set @statusCode = 3
	END CATCH
END