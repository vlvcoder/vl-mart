FROM openjdk:11
MAINTAINER Vlvcoder
COPY target/vl-mart-0.0.1-SNAPSHOT.jar vl-mart-0.0.1-SNAPSHOT.jar
COPY all all
COPY bread bread
COPY milk milk
COPY cheese cheese
COPY eggs eggs
COPY sausage sausage
COPY ham ham
COPY yogurt yogurt
COPY pasta pasta
COPY tea tea
COPY coffee coffee
COPY butter butter
COPY cookie cookie
COPY fish fish
COPY caviar caviar
COPY staffs staffs
COPY banners/.  banners/
COPY banners/banner.txt banner.txt
ENTRYPOINT ["java", "-jar", "/vl-mart-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
