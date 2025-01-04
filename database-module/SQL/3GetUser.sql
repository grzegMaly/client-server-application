Use TeamLinkDB;
GO

Create or Alter Procedure GetUserByParams
	@email nvarchar(50),
	@password nvarchar(50)
AS
BEGIN
	  
	  Declare @id uniqueidentifier;

	  Select @id = id
	  From EmployeesLoginData
	  Where email = @email
		AND password = @password;

	IF @id is null
	BEGIN
		SELECT 'User not found' AS Message;
        RETURN;
	END

	Select e.id, e.firstName, e.lastName, e.role
	From Employees e
		Inner Join Roles r
			ON e.role = r.roleId
	Where id = @id
END
GO

Create or Alter procedure GetUserById
	@userId uniqueIdentifier
AS
BEGIN

	IF EXISTS (SELECT 1 FROM Employees WHERE id = @userId)
    BEGIN
        SELECT id, firstName, lastName, role
        FROM Employees
        WHERE id = @userId;
    END
    ELSE
    BEGIN
        SELECT 'User Not Found' AS Message;
    END
END;
GO

Create Or Alter Procedure GetUsers
	@offset int,
	@limit int
AS
BEGIN
	
	IF @offset < 0 OR @limit <= 0
		BEGIN
			Select 'Bad Data' AS Message;
			RETURN;
		END

	if @limit > 50
	BEGIN
		SET @limit = 10;
	END

	Select
	id, firstName, lastName, role
	From Employees
	Order By id
	OFFSET (@offset * @limit) ROWS FETCH NEXT @limit ROWS ONLY;
END