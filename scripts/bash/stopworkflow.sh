#!/bin/sh
kubectl delete -f /home/centos/egar-workflow-api/scripts/kube/workflow-api-deployment.yaml
kubectl delete -f /home/centos/egar-workflow-api/scripts/kube/workflow-api-service.yaml

