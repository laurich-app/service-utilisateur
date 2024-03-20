# Utilisez une image Java officielle en tant que base
FROM eclipse-temurin:17-jdk

# Définir le répertoire de travail
WORKDIR /app

COPY ./ /app/

RUN ./mvnw clean package -DskipTests=true

CMD java -jar target/service-utilisateur-0.0.1-SNAPSHOT.jar