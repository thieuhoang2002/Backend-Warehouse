# ------------------------------------------------------------------------------
# Stage 1: Build the Spring Boot application using Maven Wrapper
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper va pom.xml de tan dung Docker layer cache
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Neu chua co application.properties (do da gitignore), copy file example lam khung
COPY src/ ./src/
RUN if [ ! -f src/main/resources/application.properties ]; then \
        cp src/main/resources/application.properties.example src/main/resources/application.properties; \
    fi

# Build file JAR, bo qua tests de tiet kiem thoi gian deploy
RUN ./mvnw clean package -DskipTests

# ------------------------------------------------------------------------------
# Stage 2: Runtime image nhe (JRE 17) + fontconfig cho JasperReports
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Cai fontconfig va ttf-dejavu de JasperReports xuat PDF khong bi loi thieu font tren Linux Alpine
RUN apk add --no-cache fontconfig ttf-dejavu

# Tao user non-root de tang tinh bao mat
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy file jar da build tu builder stage
COPY --from=builder /app/target/*.jar app.jar

# Thu muc upload file tam
VOLUME /tmp

# Port mac dinh cua Spring Boot Tomcat
EXPOSE 8080

# Toi uu hoa bo nho cho goi Free cua Render (512MB RAM):
# -Xms128m -Xmx350m: Gioi han Heap memory de khong bi Render OOMKilled
# -XX:+UseSerialGC: GC tiet kiem CPU/RAM cho container don nhan
# -Djava.awt.headless=true: Bat buoc cho JasperReports render PDF tren headless Linux
ENTRYPOINT ["java", \
  "-Xms128m", \
  "-Xmx350m", \
  "-Xss512k", \
  "-XX:+UseSerialGC", \
  "-Djava.awt.headless=true", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]