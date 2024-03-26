# Energy management application
## Overview
This project is a demonstration of a full-stack application built using Java Spring Boot for the backend, React for the frontend, and MySQL for the database. It follows a microservices architecture with synchronous communication between services using REST APIs and asynchronous communication using RabbitMQ queues. The application also incorporates WebSocket integration for real-time notifications and chat services. Additionally, Spring Security is implemented for authentication and authorization, ensuring secure access to resources. CRUD operations are supported for managing entities within the system.
## Microservices description
### User Management Microservice
This microservice, residing within the package "spring-boot-login-final," (initially developed in the "spring-demo-master" package without Spring Security), facilitates user management functionalities. It offers CRUD operations on users, secured via Spring Boot Token-based Authentication with Spring Security & JWT. The microservice primarily revolves around the User entity and seamlessly integrates with the device microservice through REST APIs.
### Device Management Microservice
Contained within the "device-management" package, this microservice orchestrates the management of Device and User entities. Each device is assigned to a specific user, facilitating CRUD operations on devices. Interconnected with the users microservice through REST APIs, it ensures synchronization between user entries in both microservices. Additionally, integration with the monitoring microservice is established via queues utilizing RabbitMQ. This synchronization mechanism ensures seamless alignment between device entries in the device management and monitoring microservices.
### Monitoring Management Microservice
Within the monitoring package, real-time management of device energy consumption is executed, capturing hourly consumption data for each device. The functionality is powered by the producer package, employing straightforward Java code to read data from a CSV source and transmit it seamlessly to generate device consumption in real-time. This data is transmitted via JSON format through a RabbitMQ queue to the monitoring microservice for storage and analysis.
Moreover, an advanced notification system is integrated using WebSockets. When the consumption exceeds a predetermined maximum limit per hour, instant notifications are dispatched to users via the frontend interface. This proactive alert system ensures users are promptly informed of any anomalies, enhancing overall system efficiency and user experience.
### ChatServer Microservice
Within the chatservice package, the backend infrastructure for facilitating seamless communication between admins and clients is established using WebSockets. This robust setup ensures instantaneous and efficient transmission of messages between parties.
The core functionality of the chat interface is implemented within the React frontend. Here, users can engage in real-time conversations, leveraging the backend WebSocket setup to facilitate smooth and responsive communication.
This comprehensive solution provides a user-friendly interface for admins and clients to interact seamlessly, fostering effective communication and enhancing overall user experience.
## Technologies Used
- Java Spring Boot
- React
- MySQL
- RabbitMQ
- WebSocket
- Spring Security
## Features
- Microservices architecture
- Synchronous communication via REST APIs
- Asynchronous communication with RabbitMQ queues
- Real-time notifications and chat services using WebSocket
- Secure authentication and authorization with Spring Security
- CRUD operations for managing entities
