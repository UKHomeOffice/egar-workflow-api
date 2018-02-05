#!/bin/sh
echo Starting Workflow-API version:$WORKFLOW_API_VER
rm -rf /home/centos/egar-workflow-api/scripts/kube/workflow-api-deployment.yaml; envsubst < "/home/centos/egar-workflow-api/scripts/kube/workflow-api-deployment-template.yaml" > "/home/centos/egar-workflow-api/scripts/kube/workflow-api-deployment.yaml" 
kubectl create -f /home/centos/egar-workflow-api/scripts/kube/workflow-api-deployment.yaml
kubectl create -f /home/centos/egar-workflow-api/scripts/kube/workflow-api-service.yaml

