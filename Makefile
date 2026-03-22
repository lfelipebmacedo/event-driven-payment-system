COMPOSE=docker compose
API_DIR=payment-api

.PHONY: up down build logs ps test clean api-run api-test api-package

up:
	$(COMPOSE) up --build -d

down:
	$(COMPOSE) down --remove-orphans

build:
	$(COMPOSE) build

logs:
	$(COMPOSE) logs -f

ps:
	$(COMPOSE) ps

test:
	cd $(API_DIR) && ./mvnw clean test

api-run:
	cd $(API_DIR) && ./mvnw spring-boot:run

api-test:
	cd $(API_DIR) && ./mvnw test

api-package:
	cd $(API_DIR) && ./mvnw clean package

clean:
	cd $(API_DIR) && ./mvnw clean
