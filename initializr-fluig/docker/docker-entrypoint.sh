#!/bin/bash
#
# Copyright 2018, TOTVS S.A.
# All rights reserved.
#
set -eo pipefail

# set java configuration
if [ "${1:0:1}" = '-' ]; then
  set -- java "$@" -Xms512m -Xmx512m -jar fluig-initializr.jar
fi

if [ "$1" = 'java' ]; then
  # better view of the process with the full command and args
  echo "$ $@"
fi

# If argument is not related, we assume that
# user wants to run his own process, for example
# a "bash" shell to explore this image
exec "$@"