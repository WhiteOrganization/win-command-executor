# Windows Commands Executor
This application will serve as a tool to help you execute sequential commmands in a Windows OS console.

## 1) What is this repository for?

### 1.1) Quick summary
Version: `0.1-SNAPSHOT`

#### 1.1.1) How to use
You can call the jar and send arguments with the following format:

    [command, TTS_success_message, failed_message]+

The second parameter is a Text to Speech. 
The application will send voice messages to inform the user of where the process is at in every step.

#### 1.1.2) Examples
`java.exe -jar win-command-executor.jar  "del directory" "directory removed successfully" "The directory was not found, please verify the name of the folder"`

no limits:

`java -jar win-command-executor.jar  "mvn clean" "project cleaned validating..." "Are files from target directory being used?" "mvn validate" "Code format is valid, taking down docker container" "Any code formatting errors found?" "docker-compose down" "Docker container is down, clearing cache" "Have you installed docker yet?" "docker system prune -a -f" "Docker image cleared, deploying application" "Is docker running?" "docker-compose up" "Docker container is up and running" "Is docker running?"`

## 2) How to Set-up the application
Download the latest release edit the `.bat` file with your commands and execute the file.

## 3) How can I help the project?

### 3.1) Summary of set up
This is a simple project and it is very easy to fork and add functionality to it.

#### 3.1.1) Development environment
- [JDK](https://openjdk.org/) version: 	`1.19`
- [Maven](https://maven.apache.org/download.cgi)

#### 3.1.2) Project Dependencies
This library uses:
- **lombok** to log errors and general logs.
    - **Slf4j**
- **Spring boot**
  - **Spring 6** to format logs


### 3.2) Configuration Steps
#### 3.2.1) Environment Configuration
  You will require all the Development elements on your environment. 
  An IDE with maven support is suggested for you to make any modifications to the code.
#### 3.2.2) Database configuration

You don't need to set up a DataBase for the project to connect to.

## 4) How to Deploy?
use [maven](https://spring.io/guides/gs/maven/) to compile and run the app.

`mvn clean package`

## 5) What are the Contribution guidelines?

#### 5.1) Writing tests.

_No tests at the moment, but please implement or let us know if you think one is required._

#### 5.2) Code review.

_Request if needed._

#### 5.3) Other guidelines.

_Please ask for the code standard to use as guideline and reflect it in the project._

## 6) Who do I talk to?

<table>
<thead><tr><th><b>Role</b></th> <th><b>Contact</b></th></tr></thead>
<tr><td>Owner/admin</td><td>current main developer: <a href='mailto:obed.vazquez@gmail.com'>obed.vazquez@gmail.com</a></td></tr>
<!-- <tr><td>Supporters</td><td>we have supporters with knowledge on the setup process of the project only</td></tr> -->
<tr><td>Community</td><td> send us a message in <a href='http://discord.whiteweb.tech'> our Discord Server</a></td></tr>
</table>

>Please contact me if you want to help, I'm developing and maintaining and supporting in general this project 
on my own with no help or support of anyone and any tip, comment, change or help in general is well-received.

