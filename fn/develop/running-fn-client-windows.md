# How-to: Run Fn client on Windows and connect to a remote Fn server 
You can run the Fn client on Windows 10 and connect to a remote Fn server. This document provides the steps to make that happen.

## Fn System Requirements
To install Fn on Windows, you need the following software/hardware:

* Windows 10 64-bit: Pro 2004 (build 19041) or higher, or Enterprise or Education 1909 (build 18363) or higher.
    * This is a requirement for Docker.
* Docker Desktop for Windows (3.3.3 +)
* Microsoft Hyper-V and Containers support installed 

Links to the get all this installed are provided below.


## Install Docker Desktop for Windows
Fn client requires Docker Desktop for Windows to run. Here are some links to for installing Docker.

### Docker System Requirements
To install Docker, you have to have all of the following.

1. Windows 10 64-bit: Pro 2004 (build 19041) or higher, or Enterprise or Education 1909 (build 18363) or higher.
2. The following hardware prerequisites are required to successfully run Client Hyper-V on Windows 10
   - 64 bit processor with Second Level Address Translation (SLAT)
   - 4GB system RAM
   - BIOS-level hardware virtualisation support must be enabled in the BIOS settings. See the documentation from your manufacturer for this step. For more information, see Virtualization: <https://docs.docker.com/docker-for-windows/troubleshoot/#virtualization-must-be-enabled>
3. Enabled ```Hyper-V and Containers features```for Windows 10.

**Note:** *Above system need can change with newer docker desktop versions.More on requirements, check: <https://docs.docker.com/desktop/windows/install/>.*

 *You can also download the script:<https://raw.githubusercontent.com/fnproject/cli/cli_fn_windows_install/fn_windows_installer.ps1> locally and execute in below way to see the current system state and see whether it is meeting the above need, otherwise Docker desktop will not work.*
-  Open PowerShell as Administrator
   ```
   click Start > Windows PowerShell > Run as Administrator
   ```
-  Execute command in opened PowerShell and note down the outcome as it is needed in subsequent steps.
   ```
   Get-ExecutionPolicy
   ```
-  Execute command in opened PowerShell
   ```
   Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy Unrestricted -Force;
   ```

-  Execute command in opened PowerShell. Outcome of command will show your system state.
   ```
   <<downloaded-script-full-path>>\fn_installer.ps1 "get-system-state"
   ```

    ***<<downloaded-script-full-path>>*** is fully-qualified path of  downloaded script. *For example:*
    ```C:\Users\<<current_user>>\Downloads\fn_install\```


- If just above step is showing that ```Hyper-V``` is not enabled then you can run command in opened PowerShell. This will enable the ```Hyper-V```. 
  This step may result in system restart, please do follow the steps below post restart. 

    ```
   <<downloaded-script-full-path>>\fn_installer.ps1 "enable-hyperviser"
   ```
    ***<<downloaded-script-full-path>>*** is fully-qualified path of  downloaded script. *For example:*
    ```C:\Users\<<current_user>>\Downloads\fn_install\```


- Once done with above steps, execute command in opened PowerShell
  ```
  Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy <<old_value>> -Force;
  ```

    ***<<old_value>>*** this is same as the outcome you noted down after execution of command ```Get-ExecutionPolicy``` above.
    

- close the opened Powershell

### Install Docker
These are the steps to install Docker.

1. Make sure Virtualization is enabled on your laptop.
2. Enable Hyper-V and Containers features for Windows 10. See: <https://docs.microsoft.com/en-us/virtualization/hyper-v-on-windows/quick-start/enable-hyper-v>
3. Perform the Docker install. See:  <https://docs.docker.com/docker-for-windows/install/>.
4. Setup a [DockerHub account](https://hub.docker.com/signup). This is required to store Docker images on the net.
   
 
With Docker installed, you are ready to install the Fn client.

## Install Fn Client
Follow the steps below to install the Fn client on Windows.

-  Download the script:<https://raw.githubusercontent.com/fnproject/cli/cli_fn_windows_install/fn_windows_installer.ps1> locally and execute in below way.
   

-  Open PowerShell as Administrator
   ```
   click Start > Windows PowerShell > Run as Administrator
   ```
-  Execute command in opened PowerShell and note down the outcome as it is needed in subsequent steps.
   ```
   Get-ExecutionPolicy
   ```
   
-  Execute command in opened PowerShell
   ```
   Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy Unrestricted -Force;
   ```
-  Execute command in opened PowerShell. This will install the latest fn client. More details: <https://github.com/fnproject/cli/releases>
   ```
   <<downloaded-script-full-path>>\fn_installer.ps1 "fn-client-install"
   ```
    ***<<downloaded-script-full-path>>*** is fully-qualified path of downloaded script. *For example:*
      ```C:\Users\<<current_user>>\Downloads\fn_install\```
 
  
- Once done with above steps, execute command in opened PowerShell
  ```
  Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy <<old_value>> -Force;
  ```

    ***<<old_value>>*** this is same as the outcome you noted down after execution of command ```Get-ExecutionPolicy``` above.
    

- Close the opened PowerShell

Now when you open a Windows **Command Prompt**, and execute command ```fn version```. It should show the fn version.

### (3) Install the Linux Subsystem for Windows
The Linux subsystem for Windows is now a standard feature of Windows as of build 16215. The Linux Fn client runs fine in the subsystem. To install the Fn client do the following:

1. Install the Linux subsystem for Windows. See: <https://docs.microsoft.com/en-us/windows/wsl/install-win10>
2. After your distribution is installed, [perform the Fn installation as normal](https://fnproject.io/tutorials/install/).

Fn should work just like it does on Linux or MacOS.


## Setup an Fn Server on Linux or MacOS
Next you need to setup an Fn server on another machine or virtual machine. See the [fn tutorials](https://fnproject.io/tutorials/) for information on starting the server. The machine must be accessible on the network from your client machine.

## Configure your Fn Client
Next you need to configure your Fn client to connect to the machine running Fn server.

### Change your context
You need to configure you context to point at DockerHub and your Fn server.

(1) Look at your context:  `fn list context`
```
CURRENT NAME    PROVIDER        API URL                 REGISTRY
        default default         http://localhost:8080
```

(2) Select the default context: `fn use context default`

Response: `Now using context: default`

(3) Change your API context to point to your sever machine:  
`fn update context api-url http://host.example.com:8080`  

Response: `Current context updated api-url with http://host.example.com:8080`

(4) Set your DockerHub user id: `fn update context registry <your-dockerhub-id>`  

Response: `Current context updated registry with <your-dockerhub-id>`

(5) Check your context: `fn list context`

```
CURRENT NAME    PROVIDER        API URL                       REGISTRY
*       default default         http://host.example.com:8080  <your-dockerhub-id>
```

(6) You are all Set. You should be able to make apps, and invoke and deploy functions just like normal. Just open a **Command Prompt** window rather than a Terminal window. Just follow the tutorial of your choice at: <https://fnproject.io/tutorials/>.

### Note
Text quoting is different in Windows. For example, from a **Command Prompt** window to pass JSON data to your function the command looks like this:

```sh
echo {"name":"bob"} | fn invoke nodeapp nodefn
```

## Summary
You have installed Docker on Windows and the Fn client. You can now connect your Windows machine to an Fn server running on a different machine.
