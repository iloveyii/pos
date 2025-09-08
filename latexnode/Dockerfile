FROM fabtesta/ubuntu-xelatex

# Create app directory
WORKDIR /usr/src/app

# Install dependencies
RUN apt-get update && apt-get install -y curl ca-certificates bash git \
    && rm -rf /var/lib/apt/lists/*

# Set NVM directory
ENV NVM_DIR=/root/.nvm

# Install NVM
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash

# Install Node.js v16 (must source nvm.sh in the same RUN)
RUN bash -c ". $NVM_DIR/nvm.sh \
    && nvm install 16 \
    && nvm alias default 16"

# Make Node + npm available for later steps
ENV PATH=$NVM_DIR/versions/node/v16.0.0/bin:$PATH

# Copy package.json and install dependencies
COPY ./package*.json ./

# Copy source code
COPY . .

CMD ["node", "server.js"]
