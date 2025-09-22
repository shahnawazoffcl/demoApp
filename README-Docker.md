# SchoolVroom Backend - Docker Setup

This guide explains how to run the SchoolVroom Backend using Docker.

## Prerequisites

- Docker (version 20.10+)
- Docker Compose (version 2.0+)

## Quick Start

### Option 1: Using the Build Script (Recommended)

```bash
# Make the script executable (first time only)
chmod +x docker-build.sh

# Build the Docker image
./docker-build.sh build

# Run the application (includes MySQL database)
./docker-build.sh run

# View logs
./docker-build.sh logs

# Stop the application
./docker-build.sh stop
```

### Option 2: Using Docker Compose Directly

```bash
# Build and run the application
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop the application
docker-compose down
```

### Option 3: Build Docker Image Only

```bash
# Build the Docker image
docker build -t schoolvroom-backend:latest .

# Run with external MySQL (update connection details)
docker run -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/schoolvroom \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  schoolvroom-backend:latest
```

## Services

The Docker Compose setup includes:

### Backend Service
- **Container**: `schoolvroom-backend`
- **Port**: `8083`
- **Health Check**: `/actuator/health`
- **Base URL**: `http://localhost:8083`

### MySQL Database
- **Container**: `schoolvroom-mysql`
- **Port**: `3306`
- **Database**: `schoolvroom`
- **Username**: `schoolvroom_user`
- **Password**: `schoolvroom_password`
- **Root Password**: `rootpassword`

## Configuration

### Environment Variables

You can customize the application by modifying environment variables in `docker-compose.yml`:

```yaml
environment:
  # Database
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/schoolvroom
  SPRING_DATASOURCE_USERNAME: schoolvroom_user
  SPRING_DATASOURCE_PASSWORD: schoolvroom_password
  
  # Server
  SERVER_PORT: 8083
  
  # Security
  JWT_SECRET: your-secret-key-here
  
  # CORS
  CORS_ALLOWED_ORIGINS: http://localhost:3000,http://localhost:3001
```

### Volume Mounts

- **MySQL Data**: Persistent storage for database data
- **Logs**: Application logs (optional)

## Docker Commands

### Build Script Commands

```bash
./docker-build.sh build      # Build Docker image
./docker-build.sh run        # Start services
./docker-build.sh stop       # Stop services
./docker-build.sh restart    # Restart services
./docker-build.sh logs       # View logs
./docker-build.sh clean      # Clean up containers and images
./docker-build.sh help       # Show help
```

### Manual Docker Compose Commands

```bash
# Start services in background
docker-compose up -d

# Start services with output
docker-compose up

# Stop services
docker-compose down

# Rebuild and start
docker-compose up --build

# View logs
docker-compose logs -f backend
docker-compose logs -f mysql

# Scale services (if needed)
docker-compose up -d --scale backend=2
```

## Health Checks

Both services include health checks:

- **Backend**: `http://localhost:8083/actuator/health`
- **MySQL**: Internal mysqladmin ping

Check service status:
```bash
docker-compose ps
```

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using the port
   lsof -i :8083
   lsof -i :3306
   
   # Stop conflicting services or change ports in docker-compose.yml
   ```

2. **Database Connection Issues**
   ```bash
   # Check MySQL logs
   docker-compose logs mysql
   
   # Verify database is ready
   docker-compose exec mysql mysql -u schoolvroom_user -p schoolvroom
   ```

3. **Application Not Starting**
   ```bash
   # Check backend logs
   docker-compose logs backend
   
   # Check if services are healthy
   docker-compose ps
   ```

4. **Memory Issues**
   ```bash
   # Adjust JVM memory settings in docker-compose.yml
   environment:
     JAVA_OPTS: "-Xmx512m -Xms256m"
   ```

### Database Access

Access MySQL directly:
```bash
# Using Docker exec
docker-compose exec mysql mysql -u schoolvroom_user -p schoolvroom

# Using MySQL client (if installed locally)
mysql -h localhost -P 3306 -u schoolvroom_user -p schoolvroom
```

### Logs

View different types of logs:
```bash
# Application logs
docker-compose logs -f backend

# Database logs
docker-compose logs -f mysql

# All logs
docker-compose logs -f

# Last 100 lines
docker-compose logs --tail=100 backend
```

## Production Considerations

For production deployment:

1. **Security**:
   - Change default passwords
   - Use secrets management
   - Enable SSL/TLS
   - Use non-root database user

2. **Performance**:
   - Tune JVM settings
   - Configure connection pooling
   - Set up monitoring

3. **Backup**:
   - Regular database backups
   - Persistent volume backups

4. **Networking**:
   - Use custom networks
   - Configure proper firewall rules
   - Set up load balancing if needed

## Development

For development with auto-reload:

1. Mount source code as volume
2. Use development profiles
3. Enable hot reloading

```yaml
# Add to backend service in docker-compose.yml
volumes:
  - ./src:/app/src
environment:
  SPRING_PROFILES_ACTIVE: development
  SPRING_DEVTOOLS_RESTART_ENABLED: true
```

## API Documentation

Once running, access:
- **Health Check**: `http://localhost:8083/actuator/health`
- **API Base URL**: `http://localhost:8083`
- **Metrics**: `http://localhost:8083/actuator/metrics` (if enabled) 