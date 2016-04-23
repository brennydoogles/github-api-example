# github-api-example

## Setup Instructions
 * Clone from Github
 * Navigate to /src/main/resources
 * Copy github.properties.example to github.properties
 * Choose an auth type (token or password)
 * Fill in the appropriate credentials for Github
 * Fill in the owner and name of the repository to validate (for example "ruby/ruby")
 * Enter the name of the file to validate as existing in the root of all branches and tags (for example ".travis.yml")

## Build Requirements
 * Java 8
 * Maven

## Build Instructions
From the project root, run the following commands in order to:

### Build
`mvn clean compile`
### Test
`mvn clean compile test`
### Test and Run
`mvn clean compile prepare-package`
### Run Without Tests
`mvn clean compile prepare-package -DskipTests=true`
