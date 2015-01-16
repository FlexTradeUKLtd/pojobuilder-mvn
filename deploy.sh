#!/bin/bash

if [[ $TRAVIS_TAG =~ ^v.* ]]
then
    echo mvn deploy --settings ./settings.xml -DperformRelease=true

elif [[ $TRAVIS_BRANCH =~ stable/.* ]] || [[ $TRAVIS_BRANCH == 'master' ]]
then
    echo mvn deploy --settings ./settings.xml

fi