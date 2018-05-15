#
# Copyright 2018, TOTVS S.A.
# All rights reserved.
#

SHELL = /bin/bash

REGISTRY ?= docker.fluig.com
# if version is not defined, use commit hash as version
VERSION ?= $(shell git rev-parse --short=7 HEAD)
IMAGE ?= platform/fluig-initializr
ROOTPATH ?= $(shell git rev-parse --show-toplevel)
TMP_DIR := "$(shell mktemp -d $(ROOTPATH)/tmp.build.XXXXXXXXXX)"
ARTIFACTS ?= "$(shell git rev-parse --show-toplevel)/initializr-fluig/target"
DOCKERPATH ?= "$(shell git rev-parse --show-toplevel)/initializr-fluig/docker"

# ensure that latest isn't use for build
define isLatest
	@[ $(VERSION) != "latest" ] || (echo "VERSION can't be latest in $(@)"; exit 1)
endef

# Rule "help"
help:
	@echo -e "\nUsage: make [target]\n\nTarget: \n"
	@grep -h "##" $(MAKEFILE_LIST) | grep -v "(help\|grep)" | sed -e "s/## //" -e 's/^/  /' -e 's/:/ -/'

# Rule "build"
.PHONY: build
build: ## Build the Dockerfile
	$(call isLatest)
	cp -R $(ARTIFACTS)/fluig-initializr.jar $(TMP_DIR)
	cp -R $(DOCKERPATH)/* $(TMP_DIR)
	cd $(TMP_DIR)
	docker build --force-rm \
		--no-cache -t $(IMAGE):$(VERSION) \
		$(TMP_DIR)
	rm -rf $(TMP_DIR)

# Rule "build:local"
.PHONY: build\:local
build\:local: ## Build the Dockerfile.local
	$(call isLatest)
	cp -R $(ARTIFACTS)/* $(TMP_DIR)
	cp -R $(DOCKERPATH)/* $(TMP_DIR)
	cd $(TMP_DIR)
	docker build --force-rm \
		-t "$(IMAGE)":local \
		$(TMP_DIR)
	rm -rf $(TMP_DIR)

# Rule "tag"
.PHONY: tag
tag:
tag: ## Tag the image to "latest" version
	$(call isLatest)
	docker tag $(IMAGE):$(VERSION) $(IMAGE):latest

# Rule "registry"
.PHONY: registry
registry:
registry: ## Tag the image on the custom registry
	docker tag $(IMAGE):$(VERSION) $(REGISTRY)/$(IMAGE):$(VERSION)

# Rule "push"
.PHONY: push
push: registry
push: ## Execute "tag" rule and push the image to remote registry
	docker push $(REGISTRY)/$(IMAGE):$(VERSION)
