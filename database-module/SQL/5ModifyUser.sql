Use TeamLinkDB;

GO

Create or Alter Procedure ModifyUserById
	@UserData UserData READONLY,
	@statusCode int OUTPUT
AS
BEGIN

	IF EXISTS (
            SELECT 1
            FROM @UserData
            WHERE id IS NULL
               OR fName IS NULL OR TRIM(fName) = ''
               OR lName IS NULL OR TRIM(lName) = ''
        )
        BEGIN
            SET @statusCode = 3;
            ROLLBACK;
            RETURN;
        END
	
	IF EXISTS (Select 1 
		From @UserData u 
		where not exists
			(Select 1 From Employees e where e.id = u.id))
		BEGIN
			Set @statusCode = 1;
			RETURN;
		END

	IF EXISTS (
            SELECT 1
            FROM @UserData u
            WHERE NOT EXISTS (SELECT 1 FROM Roles r WHERE r.roleId = u.role)
        )
        BEGIN
            SET @statusCode = 2;
            ROLLBACK;
            RETURN;
        END
	
	SET XACT_ABORT OFF;

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
		SET @statusCode = 0;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		Set @statusCode = 3;
	END CATCH

	SET XACT_ABORT ON;
END