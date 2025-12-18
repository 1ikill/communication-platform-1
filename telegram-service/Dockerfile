############ TDLIB BUILDER ############
FROM eclipse-temurin:17-jdk AS tdlib-builder

RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    git \
    unzip \
    wget \
    zlib1g-dev \
    libssl-dev \
    php \
    gperf \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Clone old TDLib
WORKDIR /app
RUN git clone --depth 1 https://github.com/tdlib/td.git && \
        cd td && \
        git fetch --depth 1 origin ef580cd3dd0e5223c2be503342dc29e128be866e && \
        git checkout ef580cd3dd0e5223c2be503342dc29e128be866e

# Build TDLib C++ core
WORKDIR /app/td
RUN mkdir build && cd build && \
    cmake -DCMAKE_BUILD_TYPE=Release -DTD_ENABLE_JNI=ON -DCMAKE_INSTALL_PREFIX=/app/td/example/java/td .. && \
    cmake --build . --target install

########################################
# Build Java bindings from example/java
########################################

WORKDIR /app/td/example/java
RUN mkdir build && cd build && \
    cmake -DCMAKE_BUILD_TYPE=Release -DTd_DIR=/app/td/example/java/td/lib/cmake/Td -DCMAKE_INSTALL_PREFIX=/app/td/example/java .. && \
    cmake --build . --target install

WORKDIR /app/td/example/java/bin
RUN jar cvf tdlib-java-example.jar org

RUN mvn install:install-file -Dfile=tdlib-java-example.jar -DgroupId=com.telegram -DartifactId=tdlib -Dversion=1.8.42 -DgeneratePom=true -Dpackaging=jar

########################################
# Build the application
########################################
FROM tdlib-builder AS app-builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

########################################
# Final runtime image
########################################
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy TDLib native libraries from tdlib-builder
COPY --from=tdlib-builder /app/td/example/java/bin/*.so /app/lib/
COPY --from=tdlib-builder /root/.m2/repository/com/telegram/tdlib/1.8.42/tdlib-1.8.42.jar /app/lib/

# Copy application JAR from app-builder
COPY --from=app-builder /app/target/*.jar app.jar

ENV LD_LIBRARY_PATH="/app/lib"

EXPOSE 8082
ENTRYPOINT ["java","-Djava.library.path=/app/lib","-jar","app.jar"]
