#!/bin/bash

# SchoolVroom Backend Docker Build Script

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Help function
show_help() {
    echo "SchoolVroom Backend Docker Management Script"
    echo ""
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  build       Build the Docker image"
    echo "  run         Run the application with docker-compose"
    echo "  stop        Stop the application"
    echo "  restart     Restart the application"
    echo "  logs        Show application logs"
    echo "  clean       Clean up Docker images and containers"
    echo "  help        Show this help message"
    echo ""
}

# Build Docker image
build_image() {
    print_status "Building SchoolVroom Backend Docker image..."
    docker build -t schoolvroom-backend:latest .
    print_success "Docker image built successfully!"
}

# Run with docker-compose
run_app() {
    print_status "Starting SchoolVroom Backend with MySQL..."
    docker-compose up -d
    print_success "Application started! Backend available at http://localhost:8083"
    print_status "Use '$0 logs' to view application logs"
}

# Stop application
stop_app() {
    print_status "Stopping SchoolVroom Backend..."
    docker-compose down
    print_success "Application stopped!"
}

# Restart application
restart_app() {
    print_status "Restarting SchoolVroom Backend..."
    docker-compose down
    docker-compose up -d
    print_success "Application restarted!"
}

# Show logs
show_logs() {
    print_status "Showing application logs (press Ctrl+C to exit)..."
    docker-compose logs -f backend
}

# Clean up
clean_up() {
    print_warning "This will remove all SchoolVroom Docker containers and images."
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_status "Cleaning up Docker resources..."
        docker-compose down -v --remove-orphans
        docker rmi schoolvroom-backend:latest 2>/dev/null || true
        docker system prune -f
        print_success "Cleanup completed!"
    else
        print_status "Cleanup cancelled."
    fi
}

# Main script logic
case "${1:-help}" in
    build)
        build_image
        ;;
    run)
        run_app
        ;;
    stop)
        stop_app
        ;;
    restart)
        restart_app
        ;;
    logs)
        show_logs
        ;;
    clean)
        clean_up
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown option: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 