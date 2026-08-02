# Build stage
FROM node:22-alpine AS build
WORKDIR /workspace

COPY package.json package-lock.json* ./
RUN npm ci

COPY . .
RUN npm run build:prod

# Runtime stage
FROM nginx:1.27-alpine
COPY --from=build /workspace/dist/bank-account-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget -qO- http://localhost:80/ || exit 1
