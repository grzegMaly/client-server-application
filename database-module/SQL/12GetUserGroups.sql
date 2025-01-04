Use TeamLinkDB;
Go

Create Or Alter Procedure GetUserGroups
	@userId uniqueidentifier
AS
BEGIN
	
	IF NOT EXISTS (Select 1 From Employees Where id = @userId)
		BEGIN
			Select 'User Not Found' AS Message;
			RETURN;
		END

	Select g.id, g.groupName, g.ownerId
	From Groups g
		JOIN GroupMembers gm ON g.id = gm.groupId
	Where gm.memberId = @userId
END