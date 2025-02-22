Office Management Application

A fully custom-built office application developed in Java 17 and JavaFX 17, without any external frameworks, ensuring complete control over the architecture and implementation.

Overview  
This application is designed as a multi-module office management system, featuring:  
✅ Chat Panel – Real-time messaging using WebSocket communication.  
✅ Task Management – Organize and assign tasks efficiently.  
✅ Note Management – Create, store, and edit notes in a structured way.  
✅ Cloud Drive – A file storage system with a dedicated file server.  
✅ Admin Management Panel – Accessible only for ADMIN users, providing full control over users and groups.  

The application follows a client-server architecture, with a modular structure, consisting of the following servers:  
🔹 Gateway Server – Routes and proxies requests between the client and backend services.  
🔹 Authentication Server – Manages user authentication and authorization.  
🔹 Database Server – Handles structured data storage and retrieval.  
🔹 File Server – Provides storage functionality for user files.  
🔹 WebSocket Server – Enables real-time chat communication.  

The client application is entirely built with JavaFX, without the use of any third-party UI frameworks, to ensure maximum flexibility and full customizability.


⚙️ Technical Setup & Requirements
Database Server Configuration

1️⃣ Run all SQL scripts in the exact order they are listed in the project.  
2️⃣ User Configuration – The database connection user is defined in Script #2. You can either keep the default user or change it, but this will require updating the credentials in:
resources/config/db/connection.properties  
3️⃣ First ADMIN User – Script #26 contains an INSERT statement for creating the first user with ADMIN role. This is required to log in and start managing users and groups.  

File Server Configuration  
📂 A configuration directory must be created in the project root, containing a file named config.txt.
This file must define the path to user resources using the key "userResourcepath" for example:
userResourcePath=D:/Drive/Users

🚀 First-Time Setup for Storage   
After inserting the first ADMIN user into the database, follow these steps:  
1️⃣ Retrieve the user's ID from the database.  
2️⃣ Convert the ID to lowercase.  
3️⃣ Manually create a directory at the specified userResourcePath, using the lowercase ID as the folder name.   
4️⃣ Inside the user’s folder, create the following three directories:  
Drive/  
Notes/  
Thumbnail/  
This ensures that the Note Management and Cloud Drive modules work correctly.

🏗️ Server Startup Order

🔹 The order of starting individual servers is flexible, except for WebSocket.  
🔹 The WebSocket Server must be started before the Gateway Server to properly establish the connection.  
🔹 The Client Application should be launched last, after all backend services are running.  


🛠️ Why This Project?

Unlike many JavaFX projects, this application was built entirely from scratch – no Spring, no Hibernate, no external UI libraries.
This approach ensures:  
✔ Deeper understanding of JavaFX and Java 17 features.  
✔ Better control over architecture without relying on heavy frameworks.  
✔ Lightweight and customizable application behavior.  
