# Utilisez une image Java officielle en tant que base
FROM eclipse-temurin:17-jdk

# Définir le répertoire de travail
WORKDIR /app

COPY ./ /app/
COPY ./generatekeys.sh /app/joueur/generatekeys.sh
COPY ./handle_start.sh /app/joueur/handle_start.sh

RUN chmod a+x /app/joueur/generatekeys.sh
RUN chmod a+x /app/joueur/handle_start.sh

# Exécutez Maven pour construire l'application
# Définir le point d'entrée de l'application
# On a pas le choix, les fichiers doivent être présent dans les resources avant le build : car ils doivent être présent dans le jar.
# Du coup, à chaque réexécution on est obliger de rebuild l'image. La solution est pas top.
# Je continue à penser qu'une gestion via Vault reste une meilleur idée.
# https://www.hashicorp.com/products/vault
CMD /bin/bash ./handle_start.sh ${PRIVATE_KEY} ${PUBLIC_KEY} /app/src/main/resources "http://${CONSUL_HOST}:${CONSUL_PORT}" && ./mvnw clean package -DskipTests=true && java -jar target/service-utilisateur-0.0.1-SNAPSHOT.jar