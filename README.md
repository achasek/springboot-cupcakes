# SWE Crypto Cupcakes - Java

Welcome to SWE Crypto Cupcakes, Java Edition! The feature branches in this repo represent the evolution of a sample app. Each week we can demo a new branch, look at what has changed and why:

1. `cupcakes-api`
2. `security`
3. `jwt` - in progress
4. `oauth` - in progress

The `main` branch is the same as the finished `oauth` project branch after the 4 weeks of delivery, so get started at the first branch to see the app from the very beginning.

## Setup 
- Ensure that [java SE @ version 17 earliest](https://www.python.org/downloads/) is already installed on your machine. Verify with the `java -version` command in a terminal.
- Clone this repository.
- Open up this project in a Java-specific IDE such as IntelliJ or Eclipse. Depending on your IDE settings, you may need to specify that this project is a Maven project and import it that way. The external dependencies should be automatically resolved once Maven is recognized.
- Start the Spring Boot application by navigating to `src/main/java/api/multiverse/swecryptocupcakes/Application.java` in the IDE and clicking the 'Run' button. Alternatively, navigate to the `swe-crypto-cupcakes-java` project root in a terminal and run `mvn spring-boot:run`. **Note:** this second strategy requires [manually installing Maven](https://maven.apache.org/install.html) to run the mvn command.
