Use TeamLinkDB
GO

Create Or Alter Procedure GetGroupMembers
	@groupId uniqueidentifier,
	@offset int,
	@limit int
AS
BEGIN

	IF NOT EXISTS (Select 1 From Groups Where id = @groupId)
		BEGIN
			Select 'Group Not Found' as Message;
			Return;
		END
	
	IF @offset < 0 OR @limit <= 0
		BEGIN
			Select 'Bad Data' AS Message;
			RETURN;
		END

	if @limit > 50
	BEGIN
		SET @limit = 10;
	END

	Select e.id, 
		   e.firstName, 
		   e.lastName, 
		   e.role
	From Employees e
		JOIN GroupMembers gm ON e.id = gm.memberId
	Where gm.groupId = @groupId
	Order By e.id
	OFFSET (@offset * @limit) ROWS FETCH NEXT @limit ROWS ONLY;
END