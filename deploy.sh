#!/bin/bash

if [[ $TRAVIS_TAG =~ ^v.* ]]
then
  #  mvn deploy --settings ./settings.xml -DperformRelease=true
    echo Not deploying from travis
elif [[ $TRAVIS_BRANCH =~ stable/.* ]] || [[ $TRAVIS_BRANCH == 'master' ]]
then
    mvn deploy --settings ./settings.xml

fi
