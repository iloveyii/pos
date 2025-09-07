# Stage 1: Install Node.js on a clean Ubuntu
FROM ubuntu:16.04 as node_builder

RUN apt update -y
RUN apt install -y curl
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

RUN apt-get update -y
RUN apt-get install -y software-properties-common -y
RUN add-apt-repository ppa:ubuntu-toolchain-r/test -y
RUN apt-get update -y
RUN apt-get install -y libc6

RUN export NVM_DIR="$HOME/.nvm"

RUN nvm install 16
# Verify installation
# RUN node --version && npm --version

