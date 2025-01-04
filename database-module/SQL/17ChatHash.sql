Use TeamLinkDB;
GO

Create Or Alter Function GetChatHash (
	@userId1 uniqueidentifier,
	@userId2 uniqueidentifier
) RETURNS CHAR(64)
AS
BEGIN
	Declare @sortedIds NVARCHAR(72);
	Set @sortedIds = CASE
						WHEN @userId1 < @userId2 THEN CONCAT(@userId1, @userId2)
						ELSE CONCAT(@userId2, @userId1)
					END;

	Return CONVERT(char(64), HASHBYTES('SHA2_256', @sortedIds), 2);
END