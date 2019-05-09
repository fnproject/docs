FROM debian
RUN apt-get update
RUN apt-get install -y imagemagick
ENTRYPOINT ["convert", "-", "-resize", "256x256", "-"]
