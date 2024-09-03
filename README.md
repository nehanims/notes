# Intelligent Chat Assistant

## Overview
The Intelligent Chat Assistant is a sophisticated, locally-hosted voice chat application with advanced analytics capabilities. It leverages cutting-edge AI technologies to provide a powerful, customizable chat experience with voice recognition and metric extraction features.

## Features

### Architecture and Core Technologies
- Scalable microservices architecture using Apache Kafka for event-driven communication
- Backend services implemented with Spring Boot and Kotlin
- React-based frontend with a ChatGPT-like interface for text and voice interactions
- Configurable, self-hosted open LLM models served via Ollama

### Speech Recognition and Natural Language Processing
- Automatic voice note transcription using OpenAI's Whisper model
- LLM-based metric extraction for identifying user-defined data points

### Data Management and Storage
- MinIO (S3-compatible) for object storage of audio files and transcriptions
- PostgreSQL for relational data management and metric storage
- H2 database support for development environments

### User Interface and Experience
- Features for editing, approving, and rejecting extracted metrics
- Intuitive and responsive frontend interface

### Analytics and Insights
- Advanced analytics for extracting key metrics from conversations
- Valuable insights from user interactions for tracking specific information

### DevOps and Deployment
- Dockerized components including transcription services, Kafka clusters, and object storage
- Ongoing work for full application dockerization

## Ongoing Enhancements
- Chat functionality for specific metrics within the UI
- Migration to a fully reactive backend using Spring WebFlux
- Implementation of Retrieval-Augmented Generation (RAG) for improved contextual understanding

