# Running load-balanced fn against a pool of runners

## Motivation

You can run a load-balanced setup for fn to route requests to a group of one or more runners.

## Starting the components (as regular processes)

### Persisted storage

For the particular example we'd use MySQL 5.7.22:

```bash
docker run --name fn-mysql \
        -p 3306:3306 \
        -e MYSQL_DATABASE=funcs \
        -e MYSQL_ROOT_PASSWORD=root \
        -d mysql:5.7.22
```

### DNS name requirement

For the following deployment there's a need in the following DNS names:

 - runner LB node DNS name: `lb.fn.local`
 
Please note, this DNS name is an example!


### API server

```bash
docker run --name fn-api \
        -d \
        -e FN_NODE_TYPE=api \
        -e FN_LOG_LEVEL=DEBUG \
        -e FN_PUBLIC_LB_URL="http://lb.fn.local:8181" \
        -e FN_DB_URL="mysql://root:root@tcp(fn-mysql:3306)/funcs" \
        --link fn-mysql:fn-mysql \
        -p 8080:8080 \
        fnproject/fnserver:latest
```

### Runners

```bash
docker run --name fn-runner-1 \
        --privileged \
        -d \
        -e FN_NODE_TYPE="pure-runner" \
        -e FN_LOG_LEVEL=DEBUG \
        -e FN_IOFS_DOCKER_PATH=${HOME}/.fn/iofs \
        -e FN_IOFS_PATH=/iofs \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v ${HOME}/.fn/iofs:/iofs \
        fnproject/fnserver:latest

docker run --name fn-runner-2 \
        --privileged \
        -d \
        -e FN_NODE_TYPE="pure-runner" \
        -e FN_LOG_LEVEL=DEBUG \
        -e FN_IOFS_DOCKER_PATH=${HOME}/.fn/iofs \
        -e FN_IOFS_PATH=/iofs \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v ${HOME}/.fn/iofs:/iofs \
        fnproject/fnserver:latest

docker run --name fn-runner-3 \
        --privileged \
        -d \
        -e FN_NODE_TYPE="pure-runner" \
        -e FN_IOFS_DOCKER_PATH=${HOME}/.fn/iofs \
        -e FN_IOFS_PATH=/iofs \
        -v /var/run/docker.sock:/var/run/docker.sock \
        -v ${HOME}/.fn/iofs:/iofs \
        -e FN_LOG_LEVEL=DEBUG \
        fnproject/fnserver:latest
```

### Runner load balancer

```bash
docker run --name fn-runner-lb \
        --privileged \
        -d \
        -e FN_NODE_TYPE=lb \
        -e FN_LOG_LEVEL=DEBUG \
        -e FN_RUNNER_API_URL="http://fn-api:8080" \
        -e FN_RUNNER_ADDRESSES="fn-runner-1:9190,fn-runner-2:9190,fn-runner-3:9190" \
        -p 8181:8080 \
        --link fn-runner-1:fn-runner-1 \
        --link fn-runner-2:fn-runner-2 \
        --link fn-runner-3:fn-runner-3 \
        --link fn-api:fn-api \
        fnproject/fnserver:latest
```

### DNS configuration

As we've mentioned above, we've used the DNS name (`lb.fn.local`) for runner LB node.
At this moment there's a need to figure out how the real IP address of the runner LB node:

```bash
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' fn-runner-lb
```

With the following IP please update Fn LB DNS resolve:

`xxx.xxx.xxx.xxx     lb.fn.local`

But there's also a need to define API DNS name for the sake of simple reference:

```bash
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' fn-api
```

With the following IP please update Fn API DNS resolve:

`xxx.xxx.xxx.xxx     api.fn.local`


#### Remote Docker

If you are working with Remote Docker API, the Docker API host is must be used instead:

```bash
echo ${DOCKER_HOST}
tcp://xxx.xxx.xxx.xxx:<port>
```

where `xxx.xxx.xxx.xxx` is an IP address that you should use instead of container IP address.

#### Local docker

If you run Docker on your machine (docker-4-mac, docker-4-win), 
you might use `127.0.0.1` instead of the container IP address.


## User-facing services

In the particular setup we have 3 roles and at least 5 nodes running.
There are only 2 roles that user must interact with:

 - API node (for apps/functions/triggers CRUD operations)
 - runner LB node (for triggering/invoking functions)

## Deploying first app

In first place please create a new Fn CLI context for the particular deployment:
```bash
fn create context distributed-fn --registry <container-registry> --api-url http://api.fn.local:8080
fn use context distributed-fn
```


```bash
fn init --runtime python3.7 --trigger http pyfn
cd pyfn
fn -v deploy --app testapp
```

```
Successfully created app:  testapp
Successfully created function: pyfn with <container-registry>/pyfn:0.0.2
Successfully created trigger: pyfn-trigger
Trigger Endpoint: http://lb.fn.local:8181/t/testapp/pyfn-trigger
```

Invoke your function:

```bash
fn invoke testapp pyfn
{"message":"Hello World"}
```

Trigger your function using an HTTP trigger:

```bash
fn list triggers testapp
FUNCTION        NAME            ID                              TYPE    SOURCE          ENDPOINT
pyfn            pyfn-trigger    01D1H0PNWANG8G00RZJ0000003      http    /pyfn-trigger   http://lb.fn.local:8181/t/testapp/pyfn-trigger
```

```bash
curl -X POST http://lb.fn.local:8181/t/testapp/pyfn-trigger
{"message":"Hello World"}
```
