#!/bin/bash -e

#if [ -n "$(git status --porcelain)" ]; then
#	  git status --porcelain
#  	echo "there are changes";
#  	echo "please commit everything so we have a reproducible git commit ID as LambdaCD version";
#  	exit 1;
#fi

USER=$1

lein clean
lein uberjar
ansible-playbook -i ansible-host.txt --ask-sudo-pass --user $USER deploy-lambdacd.yml
