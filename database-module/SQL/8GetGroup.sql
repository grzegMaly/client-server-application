Use TeamLinkDB;
GO

Create Or Alter Procedure GetGroupById
	@id uniqueidentifier
AS
BEGIN
	IF EXISTS (Select 1 From Groups where id = @id)
		BEGIN
			Select id, groupName, ownerId
			From Groups
			Where id = @id;
		END
	ELSE
	BEGIN
		Select 'Group Not Found' AS Message
	END
END

GO

Create Or Alter Procedure GetGroups
	@offset int,
	@limit int
AS
BEGIN
	IF @offset < 0 OR @limit <= 0
		BEGIN
			Select 'Bad Data' AS Message;
			RETURN;
		END

	if @limit > 50
	BEGIN
		SET @limit = 10;
	END

	Select id, groupName, ownerId
	From Groups
	Order By id
	OFFSET (@offset * @limit) ROW FETCH NEXT @limit ROWS ONLY
END
GO

Create Or Alter Procedure GetGroups
	@offset int,
	@limit int
AS
BEGIN
	
	IF @offset < 0 OR @limit <= 0
		BEGIN
			Select 'Bad Data' AS Message;
			RETURN;
		END

	IF @limit > 50
		BEGIN
			Set @limit = 10;
		END

	Select id, groupName, ownerId
	From Groups
	Order By id
	OFFSET (@offset * @limit) ROWS FETCH NEXT @limit ROWS ONLY;
END