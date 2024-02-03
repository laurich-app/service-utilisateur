#!/bin/bash

if [ "$#" -ne 4 ]; then
  echo "Usage: $0 PRIVATE_KEY PUBLIC_KEY FOLDER_RESSOURCE URL_DISCOVERY"
  exit 1
fi

PRIVATE_KEY="$1"
PUBLIC_KEY="$2"
FOLDER_RESSOURCE="$3"
URL_DISCOVERY="$4"

# GENERATION DES CLES

./generatekeys.sh "$PRIVATE_KEY" "$PUBLIC_KEY"

# DEPLACER LES CLES

mv "$PRIVATE_KEY" "$FOLDER_RESSOURCE/$PRIVATE_KEY"
mv "$PUBLIC_KEY" "$FOLDER_RESSOURCE/$PUBLIC_KEY"

# PUSH LES CLES

curl -X PUT \
  "${URL_DISCOVERY}/v1/kv/config/application/publicKey" \
  -H "Content-Type: text/plain" \
  --data-raw "$(cat ${FOLDER_RESSOURCE}/${PUBLIC_KEY})"