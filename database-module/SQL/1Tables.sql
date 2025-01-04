Use master;
Drop Database if exists TeamLinkDB;
GO

create database TeamLinkDB;
GO

use TeamLinkDB;
GO

Create Table Roles
(
    roleId   tinyint Primary Key,
    roleName nvarchar(50) not null,
    Constraint UQ_Name UNIQUE (roleName)
);


Create Table Employees
(
    id        uniqueidentifier DEFAULT NEWID() Primary Key,
    firstName nvarchar(50) not null,
    lastName  nvarchar(50) not null,
    role      tinyInt,
    Constraint FK_Employee_Role Foreign Key (role) references Roles (roleId) ON DELETE SET null
);


Create Table EmployeesLoginData
(
    id       uniqueidentifier,
    email    nvarchar(50),
    password nvarchar(50),
    Constraint FK_Data_Employee Foreign Key (id) references Employees (id) on delete cascade,
    Constraint UQ_EmployeeLogin UNIQUE (id)
);


Create Table Groups
(
    id        uniqueidentifier default NEWID() Primary Key,
    groupName nvarchar(50) not null,
    ownerId   uniqueidentifier,
    Constraint FK_Group_Owner Foreign Key (ownerId) references Employees (id),
    Constraint UQ_GroupName_OwnerId UNIQUE (groupName, ownerId)
);

Create Table GroupMembers
(
    groupId  uniqueidentifier not null,
    memberId uniqueidentifier not null,
    Constraint FK_GroupMember_Group Foreign Key (groupId) references Groups (id) ON DELETE CASCADE,
    Constraint FK_GroupMember_Employee Foreign Key (memberId) references Employees (id) ON DELETE CASCADE,
    Constraint PK_GroupMember Primary Key (groupId, memberId)
);

Create Table StatusCodes
(
    id          int Primary Key,
    description nvarchar(200) not null,
    Constraint UQ_Description UNIQUE (description)
);

GO
Create Schema ChatSchema;
GO
Create Table ChatRegistry
(
	id BIGINT IDENTITY(1, 1) Primary Key,
	hash CHAR(64) NOT NULL UNIQUE,
	tableName NVARCHAR(255) NOT NULL,
	createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
)

Create Type UserData AS Table
(
    id       uniqueidentifier,
    fName    varchar(50),
    lName    varchar(50),
    role     TINYINT,
    email    NVARCHAR(50),
    password nvarchar(50)
);

Create TYPE GroupData AS TABLE
(
    id        uniqueidentifier,
    groupName nvarchar(50),
    ownerId   uniqueidentifier
);

Create TYPE UserGroup AS TABLE
(
    groupId uniqueidentifier,
    userId  uniqueidentifier
);

Create Type MoveData AS TABLE
(
    userId uniqueidentifier,
    fromGroup uniqueidentifier,
    toGroup uniqueidentifier
);