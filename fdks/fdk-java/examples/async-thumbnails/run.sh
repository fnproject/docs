#!/bin/bash
set -e

fn --verbose  deploy   --app myapp --local


echo "Calling function"
curl -v -X POST --data-binary @test-image.png -H "Content-type: application/octet-stream" "http://localhost:8080/t/myapp/async-thumbnails"

echo "Contents of bucket"
mc ls -r example-storage-server
echo "Sleeping for 5 seconds to allow flows to complete"
sleep 5
echo "Contents of bucket"
mc ls -r example-storage-server
