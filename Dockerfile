FROM openjdk:8-jre-stretch

RUN addgroup --gid 1001 scuser \
	&& adduser --home /scuser --gid 1001 --uid 1001 --disabled-password --gecos "" scuser
USER scuser

COPY target/*.jar /scuser/app.jar
CMD ["java", "-Xms128m", "-Xmx256m", "-jar", "/scuser/app.jar"]
