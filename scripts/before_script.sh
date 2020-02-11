#!/usr/bin/env bash
set -e

python3 -m venv venv
which pip
pip install mkdocs-material
mkdocs -V
if [ "$TRAVIS_PULL_REQUEST" = "false" ]
then
  # switch git to using ssh and a github deploy key so that mkdocs gh-pages can deploy correctly
  openssl aes-256-cbc -K $TRAVIS_KINTA_DEPLOY_KEY_KEY -iv $TRAVIS_KINTA_DEPLOY_KEY_IV -in travis_kinta_deploy_key.enc -out travis_kinta_deploy_key -d
  chmod 600 travis_kinta_deploy_key
  eval $(ssh-agent -s)
  ssh-add travis_kinta_deploy_key
  rm travis_kinta_deploy_key
  git remote set-url origin git@github.com:dailymotion/kinta.git
  git push origin master
fi
