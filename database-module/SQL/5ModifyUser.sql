Use TeamLinkDB;

GO

Create or Alter Procedure ModifyUserById
	@UserData UserData READONLY
AS
BEGIN

	SET NOCOUNT ON;
    SET XACT_ABORT ON;

	IF EXISTS (
            SELECT 1
            FROM @UserData
            WHERE id IS NULL
               OR fName IS NULL OR LEN(fName) = 0
			   OR lName IS NULL OR LEN(lName) = 0
        )
        BEGIN
            Select description AS Message
			From StatusCodes Where id = 3;
            RETURN;
        END
	
	IF EXISTS (Select 1 
		From @UserData u 
		where not exists
			(Select 1 From Employees e where e.id = u.id))
		BEGIN
            Select description AS Message
			From StatusCodes Where id = 1;
			RETURN;
		END

	IF EXISTS (
            SELECT 1
            FROM @UserData u
            WHERE NOT EXISTS (SELECT 1 FROM Roles r WHERE r.roleId = u.role)
        )
        BEGIN
            Select description AS Message
			From StatusCodes Where id = 2;
            ROLLBACK;
            RETURN;
        END

	BEGIN TRAN
	BEGIN TRY
		Update e
		Set
			e.firstName = u.fName,
			e.lastName = u.lName,
			e.role = u.role
		From Employees e
		Join @UserData u ON u.id = e.id;

		COMMIT;
        Select e.id,
			   e.firstName,
			   e.lastName,
			   e.role
		From Employees e
		JOIN @UserData ud ON e.id = ud.id;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		Select description AS Message
		From StatusCodes Where id = 3;
	END CATCH

	SET XACT_ABORT ON;
END