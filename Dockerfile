# Stage 1: Build ứng dụng với Maven
FROM maven:3-openjdk-17 AS build

# Thiết lập thư mục làm việc để build
WORKDIR /app

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Build ứng dụng và bỏ qua kiểm tra
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng với OpenJDK
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file JAR từ "build stage" sang "run stage"
COPY --from=build /app/target/truyen247-be-0.0.1-SNAPSHOT.jar app.jar

# Cấu hình cổng ứng dụng
EXPOSE 8080

# Câu lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
