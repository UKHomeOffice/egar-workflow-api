#!/bin/sh
export KUBE_NAMESPACE=egar-test
export KUBE_SERVER=${KUBE_SERVER}
export KUBE_TOKEN=${KUBE_TOKEN}

kd -f workflow-api-deployment-template.yaml -f workflow-api-service.yaml
