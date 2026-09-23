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
# Stage 2: Runtime image nhe (JRE 17)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Tao user non-root de tang tinh bao mat
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy file jar da build tu builder stage
COPY --from=builder /app/target/*.jar app.jar

# Thu muc upload file (neu he thong co luu tru file anh/bao cao)
VOLUME /tmp

# Port mac dinh cua Spring Boot Tomcat
EXPOSE 8080

# Toi uu hoa bo nho cho goi Free cua Render (512MB RAM):
# -Xmx384m: Gioi han Heap memory toi da 384MB de tranh bi OOM kill
# -XX:+UseSerialGC: GC nhe cho container don nhan / it RAM
ENTRYPOINT ["java", "-Xms256m", "-Xmx384m", "-Xss512k", "-XX:+UseSerialGC", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]