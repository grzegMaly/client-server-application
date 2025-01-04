Use TeamLinkDB;
GO

Create Or Alter Procedure DeleteUserFromGroup
	@groupId uniqueidentifier,
	@memberId uniqueidentifier,
	@statusCode int OUTPUT
AS
BEGIN
	
	IF @groupId IS NULL OR @memberId IS NULL
		BEGIN
			SET @statusCode = 4;
			RETURN;
		END

	IF NOT EXISTS (
		SELECT 1
		FROM Employees e
		JOIN Groups g ON g.id = @groupId
		WHERE e.id = @memberId
	)
	BEGIN
		SET @statusCode = 1; 
		RETURN;
	END

	IF NOT EXISTS (
		Select 1
		From GroupMembers
		Where groupId = @groupId AND memberId = @memberId
	)
	BEGIN
		SET @statusCode = 1
		RETURN;
	END

	IF EXISTS (
		Select 1
		From Groups
		Where id = @groupId AND ownerId = @memberId
	)
	BEGIN
		SET @statusCode = 3;
		RETURN;
	END

	BEGIN TRAN
	BEGIN TRY
		Delete From GroupMembers
		Where groupId = @groupId AND memberId = @memberId
		
		COMMIT;
		SET @statusCode = 0;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		SET @statusCode = 3;
	END CATCH
END