#!/bin/bash

echo "Check if the OSGi State directory must be removed..."

if [ -d "$LIFERAY_HOME"/osgi/state ]; then
  echo "Remove the OSGi State directory..."
  rm -rf "$LIFERAY_HOME"/osgi/state
  echo "Remove the OSGi State directory...[REMOVED]"
fi

echo "Check if the OSGi State directory must be removed...[DONE]"
