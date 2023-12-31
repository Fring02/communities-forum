# microservices-forum
***
This is a microservices-based REST API solution for a forum service which allows to create communities and write posts, share thoughts and ideas in posts and provides networking opportunities.
---
>The technologies used for the development:
>
>- **Java 17**
>- **Spring Boot, Data, Security**
>- **Spring Cloud: Cloud Config, Cloud Eureka**
>- **Redis**
>- **PostGreSQL, MySQL**
>- **RabbitMQ**
>- **Docker**

The project is based on microservices architecture and contains of multiple microservices. Each of them will be described below:
## 1. Config server
The config server is a service which provides externalized configuration for consuming services in a distributed system. Implemented with Spring Cloud Config Server. Each of the underlying services, except service discovery, acts as a config client.
## 2. Service discovery
The service discovery allows other services to safely and easily communicate with each other.
## 3. API Gateway
The API gateway is a component of the app-delivery infrastructure that sits between clients and services and provides centralized handling of API communication between them.
## 4. Authorization service
Authorization service is responsible for managing users registration and login using JWT authentication. In case of success, registration and login return JWT access and refresh tokens. After the access token expiration, token needs to be refreshed and this is also provided. In case of sign out, revoke operation is also available.
## 5. Users service
Users service is a CRUD service for managing users: retrieving, updating and deleting (admin permission only). 
Users service provides subscription feature: users can subscribe to others via endpoint. A further sending of notification to a user follows after subscription as a message to broker Rabbit MQ.
## 6. Posts service
Posts service is a CRUD service for managing posts: posts have content, titles, dates of being posted, views, likes and dislikes system. Posts can be created, edited by their owners; deleted either by owners or by moderators of a community post belongs to.
## 7. Communities service
Communities service is a CRUD service for managing communities: communities have members (users), posts created by members, roles for members, tags and karma. Each community is created, maintained and edited by admin only. The moderators of a community are responsible for managing posts by deleting them or approving. Post can be created by any member inside a community, but with a minimal amount of karma. Karma is increased by subscriptions of user.
## 8. Notifications service
Notifications service is a web service which creates and deletes user notifications. Notification is created: 1) if a user subscribed to other user 2)If a post was created in a community 3)With a scheduled event of recommending random popular community. Notifications are fetched from a broker and stored in a Redis cache. 

### Contacts:
- [Fring02](https://github.com/Fring02)
- [LinkedIn profile](https://www.linkedin.com/in/sultanbekkhassenov/)
