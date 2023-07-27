# Beacon
A simple JavaFX app designed to send messages over a LAN server.

## Usage
### Client side
To use the client, extract the contents of the zip file to wherever you want. Navigate to the `bin` folder and run the batch file.
To connect to a server, specify the server hostname (IP address) and port number.
If it is your first time connecting to said server, you must register an account. Otherwise, you can log in with the username and password you specified during registration.

### Server side
Do the same as the client to run the server app. To change the port of the server, navigate to `bin/src/main/resources` and edit the `port` properties in the `server.properties` file.
Use `/help` to obtain a list of commands.

Unfortunately, there is only support for Windows as of now. Mac and Linux support will be rolled out soon.
