Use TeamLinkDB
GO

Create Or Alter Procedure ModifyGroupById
	@GroupData GroupData READONLY,
	@StatusCode int OUTPUT
AS
BEGIN
	
	SET XACT_ABORT OFF;

	Declare @ToUpdate Table (
		id uniqueidentifier primary key,
		newGroupName NVARCHAR(50),
		newOwnerId uniqueidentifier
	);

	BEGIN TRAN
	BEGIN TRY

		INSERT INTO @ToUpdate (id, newGroupName, newOwnerId)
        SELECT d.id,
               COALESCE(d.groupName, g.groupName),
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
			SET @StatusCode = 5;
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
		Join @ToUpdate t ON g.groupId = t.id;

		COMMIT;
		SET @StatusCode = 0;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		Set @StatusCode = 3;
	END CATCH
	
	SET XACT_ABORT ON;
END