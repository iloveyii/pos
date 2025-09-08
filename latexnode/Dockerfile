FROM fabtesta/ubuntu-xelatex

# Install Node.js directly (more efficient than NVM in containers)
RUN apt-get update && apt-get install -y \
    curl \
    ca-certificates \
    bash \
    git \
    && curl -fsSL https://deb.nodesource.com/setup_16.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/* \
    && corepack enable

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Create app directory and set permissions
WORKDIR /usr/src/app
RUN chown appuser:appuser /usr/src/app

# Copy package files first
COPY --chown=appuser:appuser ./package*.json ./

# Install dependencies as non-root user
USER root
RUN mkdir /home/appuser

RUN npm ci --only=production

# Copy application code
COPY --chown=appuser:appuser . .

# Expose port (if your server uses one)
EXPOSE 3000

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:3000/health || exit 1

CMD ["node", "server.js"]