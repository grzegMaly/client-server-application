Use TeamLinkDB;
GO

Create or alter procedure AddUser
	@UserData UserData READONLY
AS
BEGIN

	SET NOCOUNT ON;
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
        Select e.id,
			   e.firstName,
			   e.lastName,
			   e.role
		From Employees e
		JOIN @InsertedIds iid ON e.id = iid.id
	END TRY
	BEGIN CATCH
		ROLLBACK;
        Select description From StatusCodes Where id = 3;
		RETURN;
	END CATCH
	
	SET XACT_ABORT ON;
END