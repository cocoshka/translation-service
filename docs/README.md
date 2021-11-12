# Translation Service Documentation

Source code can be found here: [Github](https://github.com/cocoshka/translation-service)

This is the documentation of translation service, which handles translations from multiple microservices, providing REST API for registering new translations and also abbility of adding custom translations outside of the microservice.

## Database

Database used in this project is [MongoDB](https://www.mongodb.com/), which stores data in flexible, JSON-like format. Example document of translation dictionary:
```json
{
    _id: ObjectId('618d90072e1ff0e1908256d3'),
    language: 'en',
    product: 'a',
    _class: 'com.ailleron.translation.model.Dictionary',
    custom: {
        label1: 'd'
    },
    version: 2,
    labels: {
        label1: 'a',
        label2: 'b',
        label3: 'c'
    }
}
```
That format is very easy to modify and query, even embedded objects like labels, so it can extract only specific keys from that object, for example returning only "label1" and "label2" without "label3". With comparison to SQL databases, handling such query will be more complicated, like storing JSON as string in column, writing advanced query to extract specific labels or doing it in the code.

Having such database makes our application high available, so if any dictionary will be added or updated it will be immediately available to other instances running our service.

## Code Design

Application structure consists of some layers called ***repositories***, ***services*** and ***controllers***. Each of them have different responsibility:
- ***controllers*** - handling data requests and calling services to handle that request
- ***services*** - gets request from controllers and processes them, getting data from repositories and returning response to controller
- ***repositories*** - connects with database and does the operations for saving and getting data

As you see data flow is something like that **response** > ***controller*** > ***service*** > ***repository*** > ***service*** > ***controller*** > **response**. This structure makes our application easier to maintain and extending our application with new features.

The main package of our application is `com.ailleron.translation`, which is split into several packages:

- ***config***
  - **MongoConfig** - configuration for [MongoDB](https://www.mongodb.com/) - database
  - **SwaggerConfig** - configuration for [Swagger](https://swagger.io/) - API specification
- ***controller***
  - **DictionaryController** - controller listening for HTTP requests which are asking for data
  - **DictionaryRegistrationController** - controller responsible for storing translations sent by other microservices through HTTP
- ***model***
  - **Dictionary** - class storing all data
- ***repository***
  - **DictionaryRepository** - interface that specifies methods, which can be used to save and query data
  - **DictionaryRepositoryImpl** - implementation of **DictionaryRepository** interface that provides logic responsible for requesting and storing data
- ***service***
  - **DictionaryService** - class responsible for exchanging data between controllers and repository, requesting data from repository, manipulating data, converting data to API specification and returning to controllers
- ***util***
  - **Utils** - class with helper methods responsible for reading and parsing property files, converting data to lowercase and checking if maps have changed and more
- **TranslationServiceApplication** - class starting whole application, contains `main` method which creates instance of Spring

## Testing

Tests written in this projects uses JUnit 5 and checks only **DictionaryService** methods, we don't have to start server in order to run tests, because all received data is transferred from controllers to service without any changes.

In order to run tests we need some database, so there is package `de.flapdoodle.embed.mongo`, which is embedded MongoDB database that stores data in memory and restarts before every test, so we don't have to drop database before every test.

## Build

Project contains two files in root directory of the project called `build.bat` and `build.sh`, which runs maven build process. Building process is based on [Jib](https://github.com/GoogleContainerTools/jib), which makes build and containerize a lot easier.

After running build process the application is being compiled and tested. If everything goes fine [Jib](https://github.com/GoogleContainerTools/jib) builds docker image, without having to create Dockerfiles, building or pushing manually to registry. This makes build process being done only by running one command.

Finally we only have to deploy our application using Docker Compose, which is described below.

## Deployment

In the project, there is file named `docker-compose.yml`, which is Docker Compose configuration file. This file contains list of services including [MongoDB](https://www.mongodb.com/) database, two instances of translation service called respectively `translation1` on port 8080 and `translation2` on port 8090. There is also [Mongo Express](https://github.com/mongo-express/mongo-express) and example translation client services for testing purposes, which has been commented out.

Before deploying project we have to build it first. After that we only have to run one command to start our translation service, just type `docker-compose up -d` or `docker compose up -d` in your terminal.

Docker Compose will create specified services in containers without necessity to install such services on our local machine and running them one by one. This also makes stopping and uninstalling faster because we only have to run `docker-compose down` or `docker compose down` and to uninstall we only have to remove related images and volumes.