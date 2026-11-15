BACKEND_DOCKERFILE=./backend/Dockerfile 
BACKEND_IMAGE_NAME=isys-lab1-backend
BACKEND_PATH=./backend

COMPOSE=docker-compose.yaml
COMPOSE_PROD=docker-compose.prod.yaml
JMETER_RUN_ID := $(shell date +%s%N)

all: run

build-backend:
	docker buildx build \
		-f $(BACKEND_DOCKERFILE) \
		-t $(BACKEND_IMAGE_NAME) $(BACKEND_PATH)

run: build-backend
	docker compose -f ${COMPOSE} up --force-recreate

prod: build-backend
	docker compose -f ${COMPOSE_PROD} up --force-recreate

jmeter-run:
	jmeter -n -t jmeter/test.jmx -l jmeter/results-$(JMETER_RUN_ID).jtl -e -o jmeter/report-$(JMETER_RUN_ID) \
		-Jhost=localhost -Jport=8080 \
		-JadminA=admin-a -JadminAPassword=password \
		-JadminB=admin-b -JadminBPassword=password \
		-Juser=user -JuserPassword=password \
		-JsharedRingName=ring-for-update-delete \
		-JuniqueRingNameA=Unique-Ring-$(JMETER_RUN_ID) \
		-JuniqueRingNameB=unique-ring-$(JMETER_RUN_ID)