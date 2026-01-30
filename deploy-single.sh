#!/bin/bash
# 发布单个项目到 GitHub Packages
PROJECT=$1
if [ -z "$PROJECT" ]; then
    echo "用法: ./deploy-single.sh <项目目录>"
    exit 1
fi
cd "$PROJECT" && mvn clean deploy -DaltDeploymentRepository=githubarkmsg::https://maven.pkg.github.com/arkmsg/ark-nexus -DskipTests
