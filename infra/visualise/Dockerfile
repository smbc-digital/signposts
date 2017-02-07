FROM openjdk:8

RUN groupadd visualise
RUN useradd -g visualise -ms /bin/bash visualise

COPY ./visualise.jar ./home/visualise/visualiser.jar
RUN chmod +x ./home/visualise/visualiser.jar

USER visualise
WORKDIR ./home/visualise

CMD ["java","-jar","visualiser.jar"]

