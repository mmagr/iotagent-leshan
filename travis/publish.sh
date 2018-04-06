#!/bin/bash -ex

version="latest"
if [ ${TRAVIS_BRANCH} != "master" ] ; then
  version=${TRAVIS_BRANCH}
fi

tag="${TRAVIS_REPO_SLUG}:${version}"
docker build -t ${tag} -f Dockerfile .


docker login -u="${DOCKER_USERNAME}" -p="${DOCKER_PASSWORD}"
docker push ${tag}
