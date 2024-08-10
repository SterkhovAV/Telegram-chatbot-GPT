# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the Docker image
FROM maven:3.9.8-eclipse-temurin-21-alpine
ENV APP_HOME /app
WORKDIR $APP_HOME
COPY --from=build /app/target/*.jar $APP_HOME/chatbot-app.jar
EXPOSE 7080
ENTRYPOINT ["sh", "-c", "\
echo BOT_NAME=$BOT_NAME && \
echo BOT_TOKEN=$BOT_TOKEN && \
echo BOT_BUFFER_THRESHOLD=$BOT_BUFFER_THRESHOLD && \
echo BOT_DIALOG_TIMEOUT=$BOT_DIALOG_TIMEOUT && \
echo BOT_PREVIOUS_MESSAGES_LIMIT=$BOT_PREVIOUS_MESSAGES_LIMIT && \
echo OPENAI_BASE_URL=$OPENAI_BASE_URL && \
echo OPENAI_API_KEY=$OPENAI_API_KEY && \
echo DATABASE_URL=$DATABASE_URL && \
echo DATABASE_USER=$DATABASE_USER && \
echo DATABASE_PASSWORD=$DATABASE_PASSWORD && \
java -jar /app/chatbot-app.jar"]