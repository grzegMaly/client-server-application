Use TeamLinkDB
GO

Create Or Alter Procedure AddGroup
	@GroupData GroupData READONLY
AS
BEGIN

	SET NOCOUNT ON;
	SET XACT_ABORT OFF;

	BEGIN TRAN
		BEGIN TRY

		    DECLARE @ValidGroups TABLE
		    (
		        groupName NVARCHAR(50),
                ownerId UNIQUEIDENTIFIER
            );

		    Insert Into @ValidGroups (groupName, ownerId)
		    SELECT g.groupName, g.ownerId
		    From @GroupData g
		    LEFT JOIN Groups gr
		        ON gr.groupName = g.groupName AND gr.ownerId = g.ownerId
		    Where gr.id IS NULL;

		    IF NOT EXISTS(Select 1 From @ValidGroups)
		        BEGIN
					Select description AS Message
					From StatusCodes
					Where id = 5;
                    RETURN;
                end

			Declare @InsertedIds TABLE
				(id uniqueidentifier, ownerId uniqueidentifier)

			Insert Into Groups (groupName, ownerId)
				OUTPUT inserted.id, inserted.ownerId Into @InsertedIds
			Select	groupName, ownerId
			From @ValidGroups

			Insert Into GroupMembers (groupId, memberId)
			Select id, ownerId
			From @InsertedIds

			COMMIT;
			SET XACT_ABORT ON;
			Select g.id,
				   g.groupName,
				   g.ownerId
			From Groups g
			Join @InsertedIds iid ON g.id = iid.id;
			RETURN;
		END TRY
		BEGIN CATCH
			ROLLBACK;
			Select description AS Message
			From StatusCodes
			Where id = 3;
		END CATCH;
	SET XACT_ABORT ON;
END