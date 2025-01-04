Use TeamLinkDB
GO

Create Or Alter Procedure CreateChat
	@userId1 uniqueidentifier,
	@userId2 uniqueidentifier,
	@hash char(64)
AS
BEGIN
	
	SET NOCOUNT ON;
	Declare @tableName NVARCHAR(128) = CONCAT('ChatSchema.Chat_', CONVERT(VARCHAR(36), @userId1), '_', CONVERT(VARCHAR(36), @userId2));

	Declare @createTableQuery NVARCHAR(MAX);

	Begin Tran
	Begin Try
		SET @createTableQuery = '
		CREATE TABLE ' + QUOTENAME(@tableName) + ' (
			id BIGINT IDENTITY(1, 1) PRIMARY KEY,
			senderId UNIQUEIDENTIFIER NOT NULL,
			receiverId UNIQUEIDENTIFIER NOT NULL,
			content NVARCHAR(MAX) NOT NULL,
			timestamp DATETIME NOT NULL
		);';
		EXEC sp_executesql @createTableQuery;
		Insert Into ChatRegistry(hash, tableName) VALUES(@hash, @tableName);
		COMMIT;
		
		Select 'Chat Created' AS Message
	END TRY
	BEGIN CATCH
		ROLLBACK;
	END CATCH
END