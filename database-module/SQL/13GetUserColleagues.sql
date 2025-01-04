Use TeamLinkDB;
GO

Create Or Alter Procedure GetUserColleagues
	@userId uniqueidentifier
AS
BEGIN

    IF NOT EXISTS(Select 1 From Employees Where id = @userId)
        BEGIN
            Select 'User Not Found' AS Message;
            RETURN;
        END

    Select distinct e.id, e.firstName, e.lastName, e.role
    FROM Employees e
        JOIN GroupMembers gm ON e.id = gm.memberId
	Where gm.groupId IN (
		Select groupId
		From GroupMembers
		Where memberId = @userId
	) AND e.id <> @userId
END