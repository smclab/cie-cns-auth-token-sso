#!/bin/bash

tent=60
while :; do

  echo "Waiting for PostgreSQL $tent"

  curl -s postgres:5432
  result=$?

  if [ "$result" -eq 0 -o "$result" -eq 52 ] ; then
    break
  fi

  tent=$((tent - 1))

  if [ "$tent" -le 0 ]; then
    echo "Timeout on connection to PostgreSQL"
    break
  fi

  sleep 1

done
