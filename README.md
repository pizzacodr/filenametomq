# Purpose

The purpose of this project is to showcase the easy of setup of [RabbitMQ](https://www.rabbitmq.com/) with Java for a presentation at the [Linux user group in Richmond, VA](https://www.meetup.com/RVALUG/). 

 

# What it does

A java process watches a directory on the filesystem.  When a file a created, it writes the filename to a RabbitMQ Queue.



# IDE Setup

This project was developed using [Eclipse](https://www.eclipse.org/), but all the build information is available on the [Apache Maven](https://maven.apache.org/) build file, so it should be easily setup on any IDE that supports that.  Any other build information can be gleaned from the Maven [pom.xml](./pom.xml) build file.  On my run command on Eclipse, I added `-Djava.util.logging.SimpleFormatter.format="[%1$tF %1$tT] [%4$-7s] %5$s %n"` to the java options on the run command to see the log messages in one line.

 
# Maven Command to Build the Binary

To build a binary, run the maven command `mvn clean package`

An executable will be created on the target directory.


# Running the Binary

The java binary can be setup with a properties file, or the defaults will be used.  A sample properties file is available on the [src/test/resources/configFilenameToMQ.properties](./src/test/resources/configFilenameToMQ.properties) . The defaults can be seen on the class [ConfigFile.java](./src/main/java/com/github/pizzacodr/filenametomq/ConfigFile.java) with the DefaultValue notation.

A sample logging file was provided on the [src/main/resources/logging.properties](./src/main/resources/logging.properties) .  To use your logging file, add a ` -Djava.util.logging.config.file=logging.properties` to the java command below.

The command to run the binary with Java on Linux is `java –jar filenametomq-0.0.1-SNAPSHOT.jar configFilenameToMQ.properties` or just `java –jar filenametomq-0.0.1-SNAPSHOT.jar` if you are going to use the defaults.

# Creating Sample Files for Testing

To generate sample files for testing.  Once the java binary is running, execute the shell script [createTestFiles.sh](./src/test/resources/createTestFiles.sh) located on the src/test/resources folder inside the folder that is being watched.
