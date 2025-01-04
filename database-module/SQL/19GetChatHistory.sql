Use TeamLinkDB;
GO

Create Or Alter Procedure GetChatHistory
	@userId1 uniqueidentifier,
	@userId2 uniqueidentifier,
	@offset int,
	@limit int
AS
BEGIN

	IF @userId1 IS NULL OR @userId2 IS NULL OR @offset IS NULL OR @limit IS NULL
    BEGIN
        SELECT 'Bad Data' as Message;
        RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM Employees WHERE id = @userId1) 
        OR NOT EXISTS (SELECT 1 FROM Employees WHERE id = @userId2)
        OR @offset < 0 OR @limit <= 0
    BEGIN
        SELECT 'Bad Data' AS Message;
        RETURN;
    END

	if @limit > 50
	BEGIN
		SET @limit = 10;
	END

	IF NOT EXISTS (
		Select 1
		From GroupMembers gm1
			JOIN GroupMembers gm2 ON gm1.groupId = gm2.groupId
		Where gm1.memberId = @userId1 AND gm2.memberId = @userId2
	)
	BEGIN
		Select 'No Common Group' AS Message;
		RETURN;
	END

	Declare @hash char(64) = dbo.GetChatHash(@userId1, @userId2);
	Declare @tableName NVARCHAR(128);

	Select @tableName = tableName
	From ChatRegistry
	Where hash = @hash;

	IF @tableName IS NULL
		BEGIN
			exec CreateChat @userId1, @userId2, @hash;
		END
	ELSE
		BEGIN
			DECLARE @query NVARCHAR(MAX) = '
				SELECT id, senderId, receiverId, content, timestamp 
				FROM ' + QUOTENAME(@tableName) + ' 
				ORDER BY timestamp 
				OFFSET (@offset * @limit) ROWS
				FETCH NEXT @limit ROWS ONLY';

				DECLARE @params NVARCHAR(MAX) = N'@offset INT, @limit INT';
				EXEC sp_executesql @query, @params, @offset, @limit;
		END
END