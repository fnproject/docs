# Running on Windows

Windows doesn't support Docker in Docker so you'll change the run command to the following:

```sh
docker run --privileged --rm --name functions -it -v /var/run/docker.sock:/var/run/docker.sock -v ./data:/app/data -p 8080:8080 fnproject/fnserver
```

Then everything should work as normal.
