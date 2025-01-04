Use TeamLinkDB;
GO

Create or alter procedure AddUser
	@UserData UserData READONLY,
	@statusCode INT OUTPUT
AS
BEGIN

	SET XACT_ABORT OFF;

	BEGIN TRAN
	BEGIN TRY

		Declare @InsertedIds TABLE (id uniqueidentifier, fName nvarchar(50), lName nvarchar(50));

		Insert Into Employees (firstName, lastName, role)
		OUTPUT inserted.id, inserted.firstName, inserted.lastName INTO @InsertedIds
		Select fName, lName, role
		From @UserData

		Insert Into EmployeesLoginData (id, email, password)
			Select i.id, u.email, u.password
			From @InsertedIds i
			JOIN @UserData u ON
				i.fName = u.fName AND
				i.lName = u.lName;
		
		COMMIT;
		SET @statusCode = 0;
		RETURN;
	END TRY
	BEGIN CATCH
		ROLLBACK;
		SET @statusCode = 3;
		RETURN;
	END CATCH
	
	SET XACT_ABORT ON;
END