#!/bin/bash

echo "Check if the Hypersonic data directory must be removed..."

if [ ! -f "$LIFERAY_HOME"/.data_hypersonic_removed ]; then
  echo "Remove the Hypersonic data directory..."
  rm -rf "$LIFERAY_HOME"/data/hypersonic
  echo "Remove the Hypersonic data directory...[REMOVED]"

  touch "$LIFERAY_HOME"/.data_hypersonic_removed
fi

echo "Check if the Hypersonic data directory must be removed...[DONE]"
