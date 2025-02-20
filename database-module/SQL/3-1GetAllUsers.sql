Use TeamLinkDB;
GO

Create or Alter Procedure GetAllUsers
AS
BEGIN
	Select
	id, firstName, lastName, role
	From Employees
END