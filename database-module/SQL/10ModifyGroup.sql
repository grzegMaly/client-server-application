Use TeamLinkDB
GO

Create Or Alter Procedure ModifyGroupById
	@GroupData GroupData READONLY
AS
BEGIN
	
	SET NOCOUNT ON;
	SET XACT_ABORT OFF;

	Declare @ToUpdate Table (
		id uniqueidentifier primary key,
		newGroupName NVARCHAR(50),
		oldOwnerId uniqueidentifier,
		newOwnerId uniqueidentifier
	);

	BEGIN TRAN
	BEGIN TRY

		INSERT INTO @ToUpdate (id, newGroupName, oldOwnerId, newOwnerId)
        SELECT d.id,
               COALESCE(d.groupName, g.groupName),
			   g.ownerId,
			   COALESCE(d.ownerId, g.ownerId)
        FROM @GroupData d
			LEFT JOIN Groups g ON g.id = d.id
		Where g.id IS NOT NULL;

		IF EXISTS (
			Select 1
			From Groups g
				 JOIN @ToUpdate t ON
					g.groupName	= t.newGroupName AND
					g.ownerId = t.newOwnerId
			Where g.id <> t.id
		)
		BEGIN
			Select description
			From StatusCodes
			Where id = 5;
			ROLLBACK;
			RETURN;
		END

		Update g
		Set g.groupName = t.newGroupName,
			g.ownerId = t.newOwnerId
		From Groups g
		JOIN @ToUpdate t on g.id = t.id;

		Update g
		Set g.memberId = t.newOwnerId
		From GroupMembers g
		Join @ToUpdate t ON g.groupId = t.id AND t.oldOwnerId = g.memberId;

		COMMIT;
		SET XACT_ABORT ON;
		Select g.id,
			   g.groupName,
			   g.ownerId
		From Groups g
		JOIN @ToUpdate tu ON g.id = tu.id
	END TRY
	BEGIN CATCH
		ROLLBACK;
		SET XACT_ABORT ON;
		Select description
			From StatusCodes
			Where id = 3;
	END CATCH
END