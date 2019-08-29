# How-to: Run Fn Client on Windows
You can run the Fn client on Windows 10 Professional. This document provides the steps to make that happen.

## Fn System Requirements
To install Fn on Windows, you need the following software:

* Windows 10 Professional 64 bit Enterprise, or Education (Build 15063 or later)
    * This is a requirement for Docker
* Docker Desktop for Windows (2.1.0.1 +)
* Microsoft Hyper-V and Containers support installed

Links to the get all this installed is provided below.


## Install Docker Desktop for Windows
Fn client requires Docker Desktop for Windows to run. Here are some links to for installing Docker.

### Docker System Requirements
To install Docker, you have to have all of the following.

* Virtualization enabled in your laptop's BIOS. See the documentation from your manufacturer for this step.
* Windows 10 64-bit: Pro, Enterprise, or Education (Build 15063 or later).
* Enable the Hyper-V and Containers features for Windows 10.

### Install Docker
These are the steps to install Docker.

1. Make sure Virtualization is enabled on your laptop.
2. Enable Hyper-V and Containers features for Windows 10. See: <https://docs.microsoft.com/en-us/virtualization/hyper-v-on-windows/quick-start/enable-hyper-v>
3. Perform the Docker install. See:  <https://docs.docker.com/docker-for-windows/install/>.
4. Setup a [DockerHub account](https://hub.docker.com/signup). This is required to store Docker images on the net.

With Docker installed, you are ready to install the Fn client.

## Install Fn Client
There are three ways to install the Fn client on Windows.

### (1) Windows 10 Quick and Dirty
* Go to: <https://github.com/fnproject/cli/releases>
* Download the latest `fn.exe`.
* Copy the file into the `C:\Windows` directory.

The Fn client is now in your `PATH` and ready to go.

### (2) Windows 10 without Access to System Directories
If you IT department has locked you out of your system directories, then there are more steps for you to do.

1. Go to: <https://github.com/fnproject/cli/releases>
2. Download the latest `fn.exe`.
3. Create a directory for Fn.
    1. `cd c:\`
    2. `mkdir fn`
4. Copy `fn.exe` to that directory.
5. Add your new directory to the path.
    1. **Right-click** the **Start menu**.
    2. Select **System**.
    3. Scroll down in the main window and **Click** on the link **System Info**.
    4. In the System dialog box, click **Advanced system settings**.
    5. On the Advanced tab of the System Properties dialog box, click **Environment Variables**.
    6. In the System Variables box of the Environment Variables dialog box, scroll to **Path** and select it.
    7. **Click** the **lower of the two Edit buttons** in the dialog box.
    8. **Click** the **New** button.
    9. Enter in your directory name `c:\fn`.
    10. **Click** the **OK** button.
    11. Click **OK** in three successive dialog boxes, and the the System dialog box closes.

Now when you open a Windows **Command Prompt**, the Fn client should be in the path.

### (3) Install the Linux Subsystem for Windows
The Linux subsystem for Windows is now a standard feature of Windows as of build 16215. The Linux Fn client runs fine in the subsystem. To install the Fn client do the following:

1. Install the Linux subsystem for Windows. See: <https://docs.microsoft.com/en-us/windows/wsl/install-win10>
2. After your distribution is installed, [perform the Fn installation as normal](https://fnproject.io/tutorials/install/).

Fn should work just like it does on Linux or MacOS.


## Setup an Fn Server on Linux or MacOS
Next you need to setup an Fn server on another machine or virtual machine. See the [fn tutorials](https://fnproject.io/tutorials/) for information on starting the server. The machine must accessible on the network from your client machine.

## Configure your Fn Client
Next you need to configure you Fn client to connect to the machine running Fn server.

### Change your context
You need to configure you context to point at DockerHub and your Fn server.

(1) Look at your context:  `fn list context`
```
CURRENT NAME    PROVIDER        API URL                 REGISTRY
        default default         http://localhost:8080
```

(2) Select the default context: `fn use context default`

Response: `Now using context: default`

(3) Check your context: `fn list context`

```
CURRENT NAME    PROVIDER        API URL                 REGISTRY
*       default default         http://localhost:8080
```

(4) Change your API context to point to your sever machine:  
`fn update context api-url http://host.example.com:8080`  

Response: `Current context updated api-url with http://host.example.com:8080`

(5) Check your context: `fn list context`
```
CURRENT NAME    PROVIDER        API URL                       REGISTRY
*       default default         http://host.example.com:8080
```

(6) Set your DockerHub user id: `fn update context registry <your-dockerhub-id>`  

Response: `Current context updated registry with <your-dockerhub-id>`

(7) Check your context: `fn list context`

```
CURRENT NAME    PROVIDER        API URL                       REGISTRY
*       default default         http://host.example.com:8080  <your-dockerhub-id>
```

(8) You are all Set. You should be able to make apps, and invoke and deploy functions just like normal. Just open a **Command Prompt** window rather than a Terminal window. Just follow the tutorial of you choice at: <https://fnproject.io/tutorials/>.

### Note
Text quoting is different in Windows. For example, from a **Command Prompt** window to pass JSON data to your function the command looks like this:

```sh
echo {"name":"bob"} | fn invoke nodeapp nodefn
```

## Summary
You have installed Docker on Windows and the Fn client. You can now connect your Windows machine to an Fn server running on a different machine.
