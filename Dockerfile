# Stage 1: Build ứng dụng
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml và download dependencies trước (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code và build ứng dụng
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/BaiTapThucTap-0.0.1-SNAPSHOT.jar app.jar

# Expose port Spring Boot
EXPOSE 8080

# Lệnh chạy
ENTRYPOINT ["java", "-jar", "app.jar"]
