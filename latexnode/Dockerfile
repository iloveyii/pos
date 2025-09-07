FROM fabtesta/ubuntu-xelatex

# Install dependencies
RUN apt-get update && apt-get install -y curl ca-certificates bash git \
    && rm -rf /var/lib/apt/lists/*

# Set NVM directory
ENV NVM_DIR=/root/.nvm

# Install NVM
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash

# Install Node.js v16 using NVM
RUN . "$NVM_DIR/nvm.sh" \
    && nvm install 16 \
    && nvm alias default 16 \
    && nvm use default

# Make Node.js available in later layers
ENV PATH=$NVM_DIR/versions/node/v16.0.0/bin:$PATH

CMD ["node", "--version"]
