COMPOSE=docker compose
API_DIR=payment-api

.PHONY: up down build logs ps test clean api-run api-run-debug api-test api-package

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

api-run-debug:
	cd $(API_DIR) && ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

api-test:
	cd $(API_DIR) && ./mvnw test

api-package:
	cd $(API_DIR) && ./mvnw clean package

clean:
	cd $(API_DIR) && ./mvnw clean
