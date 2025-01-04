Use TeamLinkDB;
GO

Create Or Alter PROCEDURE MoveUserToGroup
    @moveData MoveData READONLY,
    @statusCode int OUTPUT
AS
BEGIN

	Declare @ValidatedUsers MoveData;
	Insert Into @ValidatedUsers
	Select md.*
	From @moveData md
		JOIN Employees e ON e.id = md.userId
		JOIN GroupMembers gm ON gm.memberId = e.id AND gm.groupId = md.fromGroup
		JOIN Groups gFrom ON gFrom.id = md.toGroup
		JOIN Groups gTo ON gTo.id = md.toGroup;
	
	Declare @InvalidUsers Table (userId uniqueidentifier);
	Insert Into @InvalidUsers
	Select distinct vd.userId
	From @ValidatedUsers vd
		JOIN Groups g1 ON g1.id = vd.fromGroup
		JOIN Groups g2 ON g2.id = vd.toGroup
	Where g1.ownerId <> g2.ownerId
		AND g1.id <> g2.id;

	Insert Into @InvalidUsers
	Select distinct vd.userId
	From @ValidatedUsers vd
		JOIN Groups g ON g.id = vd.toGroup
		JOIN Employees nOwner ON nOwner.id = g.ownerId
		JOIN Employees member ON member.id = vd.userId
	Where member.role >= nOwner.role;

	Delete From @ValidatedUsers
	Where userId IN (select userId from @InvalidUsers);

	BEGIN TRAN
	BEGIN TRY
		
		Delete From GroupMembers
		Where EXISTS (
			Select 1
			From @ValidatedUsers v
			Where GroupMembers.groupId = v.fromGroup
				AND GroupMembers.memberId = v.userId
		)

		Insert Into GroupMembers
		Select toGroup, userId
		From @ValidatedUsers

		COMMIT;
		Set @statusCode = 0;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		Set @statusCode = 3;
	END CATCH;
END

Delete From Employees
Select * From Employees