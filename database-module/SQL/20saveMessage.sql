Use TeamLinkDB;
GO

Create Or Alter Procedure SaveMessage
	@sId uniqueidentifier,
	@rId uniqueidentifier,
	@content nvarchar(255),
	@timestamp datetime,
	@statusCode int OUTPUT
AS
BEGIN
	
	IF @sId IS NULL OR @rId IS NULL OR @content IS NULL OR @timestamp IS NULL
		BEGIN
			Set @statusCode = 4;
			RETURN;
		END

	Declare @hash char(64) = dbo.GetChatHash(@sId, @rId);
	Declare @tableName NVARCHAR(128);

	Select @tableName = tableName
	From ChatRegistry
	Where hash = @hash

	IF @tableName IS NULL
		BEGIN
			Set @statusCode = 6;
			RETURN;
		END

	BEGIN TRAN
	BEGIN TRY
		Declare @query NVARCHAR(MAX) = '
			Insert Into ' + QUOTENAME(@tableName) + ' 
			(senderId, receiverId, content, timestamp)
			VALUES (@sId, @rId, @content, @timestamp)';

		Declare @params NVARCHAR(MAX) = N'@sId uniqueidentifier, @rId uniqueidentifier, 
			@content NVARCHAR(255), @timestamp datetime';

		EXEC sp_executesql @query, @params, @sId, @rId, @content, @timestamp;

		COMMIT;
		SET @statusCode = 0;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		Set @statusCode = 3;
	END CATCH
END