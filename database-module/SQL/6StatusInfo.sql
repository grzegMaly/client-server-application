Use TeamLinkDB;
GO

Create or Alter Procedure GetStatusInfo
	@id int
AS
BEGIN
	
	IF EXISTS (Select 1 From StatusCodes Where id = @id)
		BEGIN
			Select description From StatusCodes where id = @id;
		END
	ELSE
		BEGIN
			Select 'Status Unknown' AS Message;
		END
END