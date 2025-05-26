FROM eclipse-temurin
WORKDIR /usr/src/myapp
COPY . .
RUN mkdir data
RUN javac ExecutionCounter2.java
CMD ["java", "ExecutionCounter2"]