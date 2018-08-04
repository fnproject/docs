# Manage an Fn Server
The first step to using Fn it to start the Fn server. The Fn Command Line Interface (CLI) provides a number of commands for doing this.

### Start and Stopping the Fn Server
To start the Fn server, open a terminal window and issue the following command:

    fn start

This starts the server on the default listening port of `8080`. The server starts in current window and displays server information in that window. Your output should look similar to this:

```ini
...
time="2018-07-12T20:46:22Z" level=info msg="available cpu" availCPU=4000 totalCPU=4000
time="2018-07-12T20:46:22Z" level=info msg="sync and async cpu reservations" cpuAsync=3200 cpuAsyncHWMark=2560 cpuSync=800

        ______
       / ____/___
      / /_  / __ \
     / __/ / / / /
    /_/   /_/ /_/
        v0.3.482

time="2018-07-12T20:46:22Z" level=info msg="Fn serving on `:8080`" type=full
```

To stop the Fn server, open a new terminal window and type:

    fn stop

The server you started in the original window stops.

#### Server Data
When the server starts, data the Fn server needs to persist while running is stored in the `.fn/data` directory found in your home directory. 

#### Starting the the Background
To start the Fn server in the background, use the following command:

    fn start -d

### Change the Fn Server Listening Port
By default, the Fn server starts listening on port `8080`. If you need the Fn server to listen on a different port, make the following changes.

For example, to start the Fn server on port 8081, issue the following start command:

    fn start -p 8081

Additionally, you need to set an Fn environment variable to point the Fn client to the correct port. Set the `FN_API_URL` variable to `http://127.0.0.1:8081`, for example:

    export FN_API_URL=http://127.0.0.1:8081

