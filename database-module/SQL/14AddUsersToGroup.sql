Use TeamLinkDb;
GO

Create Or Alter Procedure AddUserToGroup
	@UserGroup UserGroup READONLY,
	@statusCode int OUTPUT
AS
BEGIN
	
	Declare @ValidatedUserGroups Table (
		groupId uniqueidentifier,
		userId uniqueidentifier
	);

	Insert into @ValidatedUserGroups (groupId, userId)
	Select ug.groupId, ug.userId
	From @UserGroup ug
		JOIN Groups g ON g.id = ug.groupId
		JOIN Employees e ON e.id = ug.userId 
		
	Declare @InvalidUsers Table (userId uniqueidentifier);

	Insert Into @InvalidUsers (userId)
	Select distinct vug1.userId
	From @ValidatedUsergroups vug1
		JOIN @ValidatedUserGroups vug2 ON vug1.userId = vug2.userId
		JOIN Groups g1 ON g1.id = vug1.groupId
		JOIN Groups g2 ON g2.id = vug2.groupId
	Where g1.ownerId <> g2.ownerId
		AND g1.id <> g2.id;

	Insert Into @InvalidUsers (userId)
	Select distinct vug.userId
	From @ValidatedUsergroups vug
		JOIN Groups g ON g.id = vug.groupId
		JOIN Employees o ON o.id = g.ownerId
		JOIN Employees u ON u.id = vug.userId
	Where u.role >= o.role;

	Delete From @ValidatedUserGroups
	Where userId IN (select userId from @InvalidUsers);

	BEGIN TRAN
	BEGIN TRY
		Insert Into GroupMembers (groupId, memberId)
		Select groupId, userId 
		From @ValidatedUserGroups

		COMMIT;
		Set @statusCode = 0;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		Set @statusCode = 3;
	END CATCH;
END