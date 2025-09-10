# events-project
This is my first microservices-based app. I preferred to use a monorepo because the project is not very large and therefore to give an overview of it. The project is nothing more than a simple music event booking application consisting of three microservices:
- authentication: handles both user management and authentication;
- events: handles everything related to events and the bands associated with them;
- booking: handles booking management.

In addition, the application contains the mysql-init folder, which contains all the initial scripts for creating databases and related folders, including some test data.

## Requirements
You need to create an .env file for the main repository and for each microservice. The .env file should look similar to the following:

```
MYSQL_USER=#your user mysql
MYSQL_USER_PASSWORD=#your user pwd
MYSQL_ROOT_PASSWORD=#your root psw
JWT_SECRET_KEY=#your secret key
JWT_TOKEN_EXPIRATION=#your token expiration
JWT_SECRET_REFRESH_KEY=#your refresh secret key
JWT_REFRESH_TOKEN_EXPIRATION=#your expiration secret key
```

## Start Project
I made sure that the project could be launched in two ways:
- Doker
- Locally

If you want to start the project with Docker, you need to run the **docker compose up** command. After a few minutes, the project will be ready to run.

If you want to start the project locally, you must run the **mvn clean install -DSkipTest** command for each microservice and then run each individual service.

## Ports
The ports used for this project are:
- 8080: events;
- 8081: booking;
- 8082: authentication;

Each microservice has its own swagger where you can view the APIs.