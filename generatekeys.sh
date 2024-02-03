#!/bin/bash

if [ "$#" -ne 2 ]; then
  echo "Usage: $0 PRIVATE_KEY PUBLIC_KEY"
  exit 1
fi

PRIVATE_KEY="$1"
PUBLIC_KEY="$2"

openssl genrsa -out app.prv 2048
openssl rsa -in app.prv -out "$PUBLIC_KEY" -pubout -outform PEM
openssl pkcs8 -topk8 -inform pem -in app.prv -outform pem -out "$PRIVATE_KEY" -nocrypt
rm -rf ./app.prv