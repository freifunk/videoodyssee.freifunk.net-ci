#!/bin/bash -e

#if [ -n "$(git status --porcelain)" ]; then
#	  git status --porcelain
#  	echo "there are changes";
#  	echo "please commit everything so we have a reproducible git commit ID as LambdaCD version";
#  	exit 1;
#fi

git show HEAD --pretty=format:"%h %H" --no-patch > resources/version.txt
lein uberjar
ansible-playbook -i ansible-host.txt deploy-lambdacd.yml
