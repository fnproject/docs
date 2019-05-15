#!/bin/bash
set -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
STORAGE_DIR="/tmp/example-storage-server-files"

COMPLETER_IMAGE=fnproject/flow
FUNCTIONS_IMAGE=fnproject/fnserver
MINIO_IMAGE=minio/minio

# Checks
if docker ps &>/dev/null ; then
    echo "Docker is present."
else
    echo "error: docker is not available."
    exit 1
fi
if fn --help &>/dev/null ; then
    echo "Fn is present."
else
    echo "error: fn is not available."
    exit 1
fi
if type mc &>/dev/null ; then
    echo "mc is present."
else
    echo "error: mc is not available, please install it from https://github.com/minio/mc"
    echo "mac os: brew install minio/stable/mc"
    echo "linux: sudo curl -sL https://dl.minio.io/client/mc/release/linux-amd64/mc -o /usr/local/bin/mc && sudo chmod +x /usr/local/bin/mc"
    exit 1
fi

docker pull "$COMPLETER_IMAGE"
docker pull "$FUNCTIONS_IMAGE"
docker pull "$MINIO_IMAGE"

# Set up the storage server and upload directory
if [[ -z `docker ps | grep "example-storage-server"` ]]; then
    mkdir -p "$STORAGE_DIR"
    docker run -d --name example-storage-server \
        -e "MINIO_ACCESS_KEY=alpha" \
        -e "MINIO_SECRET_KEY=betabetabetabeta" \
        -v "$STORAGE_DIR":/export \
        -p 9000:9000 \
        "$MINIO_IMAGE" server /export
    # Give it time to start up
    sleep 3
else
    echo "Storage server is already up."
fi

STORAGE_SERVER_IP=`docker inspect --type container -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' example-storage-server`

# Start functions server if not there
if [[ -z `docker ps | grep "fnserver"` ]]; then
    fn start -d
    sleep 3
else
    echo "Functions server is already up."
fi
# Get its IP
FUNCTIONS_SERVER_IP=`docker inspect --type container -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' fnserver`

# Start flow service if not there
if [[ -z `docker ps | grep "flow-service"` ]]; then
    docker run -d --name flow-service \
        -e LOG_LEVEL=debug \
        -e NO_PROXY="$FUNCTIONS_SERVER_IP:$NO_PROXY" \
        -e API_URL=http://$FUNCTIONS_SERVER_IP:8080/invoke \
        -p 8081:8081 \
        "$COMPLETER_IMAGE"
    # Give it time to start up
    sleep 3
else
    echo "Flow Completer server is already up."
fi
# Get its IP
COMPLETER_SERVER_IP=`docker inspect --type container -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' flow-service`

# Create app and routes
if [[ `fn list apps` == *"myapp"* ]]; then
    echo "App myapp is already there."
else
    fn create app myapp
fi


fn config app myapp COMPLETER_BASE_URL http://${COMPLETER_SERVER_IP}:8081
fn config app myapp OBJECT_STORAGE_URL http://${STORAGE_SERVER_IP}:9000
fn config app myapp OBJECT_STORAGE_ACCESS alpha
fn config app myapp OBJECT_STORAGE_SECRET betabetabetabeta

(
  cd ${SCRIPT_DIR}/resize128
  fn deploy --app myapp --local
)

fn config app myapp RESIZE_128_FN_ID $(fn list functions myapp  | grep resize128 | awk '{print $3}')

(
  cd ${SCRIPT_DIR}/resize256
  fn deploy --app myapp --local
)

fn config app myapp RESIZE_256_FN_ID $(fn list functions myapp  | grep resize256 | awk '{print $3}')


(
  cd ${SCRIPT_DIR}/resize512
  fn deploy --app myapp --local
)

fn config app myapp RESIZE_512_FN_ID $(fn list functions myapp  | grep resize512 | awk '{print $3}')




if mc config host list | grep example-storage-server &>/dev/null ; then
    echo "mc example-storage-server configuration is already present"
else
    echo "configuring mc example-storage-server host"
    mc config host add example-storage-server http://localhost:9000 alpha betabetabetabeta
fi
