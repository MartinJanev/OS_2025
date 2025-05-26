FROM eclipse-temurin
WORKDIR /usr/src/myapp
COPY Main.java .
RUN javac Main.java
EXPOSE 80
CMD ["java", "Main"]