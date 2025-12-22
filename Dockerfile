FROM gradle:9.2.1-jdk21-jammy AS build
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties* ./

COPY content/build.gradle content/
COPY identity/build.gradle identity/
COPY gamification/build.gradle gamification/
COPY learning/build.gradle learning/

RUN gradle dependencies --no-daemon || true

COPY . .

RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]