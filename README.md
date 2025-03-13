# Real-Time Chat Application

A modern real-time chat application built with **Kotlin** and **Jetpack Compose**, leveraging **Apollo Client** to access a **GraphQL** server powered by **Spring Boot**.

## Features

- **Real-time messaging** with GraphQL subscriptions
- **Jetpack Compose UI** for a modern, responsive experience
- **Apollo Client** for seamless GraphQL integration
- **Kotlin Coroutines & Flow** for efficient data handling
- **State Management** with ViewModel and LiveData
- **Lightweight & Scalable** backend powered by Spring Boot

## Tech Stack

### Frontend (Android)
- **Language:** Kotlin
- **Framework:** Jetpack Compose
- **GraphQL Client:** Apollo Client
- **State Management:** ViewModel, LiveData
- **Concurrency:** Kotlin Coroutines, Flow
- **Networking:** OkHttp, Retrofit (for REST fallbacks)

### Backend (GraphQL Server)
The backend for this chat application is built using **Spring Boot** with GraphQL support.

➡️ **[GraphQL Server Repository](https://github.com/anubhav-auth/messagingPrac)**

## Getting Started

### Prerequisites
- Android Studio (latest version)
- JDK 17+
- Apollo Client dependencies
- Access to the GraphQL server

### Setup Instructions
1. Clone this repository:
   ```bash
   git clone https://github.com/anubhav-auth/message_practice.git
   cd message_practice
   ```

2. Open the project in **Android Studio**.
3. Sync dependencies using Gradle.
4. Configure the **GraphQL server URL** inside `gradle.properties` or `local.properties`:
   ```properties
   GRAPHQL_SERVER_URL=https://your-server.com/graphql
   ```
5. Run the application on an emulator or device.

## GraphQL API Integration

This app interacts with the GraphQL backend using **Apollo Client**. Queries, mutations, and subscriptions are handled via GraphQL operations.

### GraphQL Schema

```graphql
enum MessageStatus {
    UNSENT
    SENT
    DELIVERED
    READ
}

type Message {
    id: String!
    topic: String!
    content: String!
    sender: String!
    receiver: String!
    status: MessageStatus!
    sentAt: String!
    deliveredAt: String!
    readAt: String!
}

type MessageStatusUpdates {
    id: String!
    receiver: String!
    status: MessageStatus!
    deliveredAt: String!
    readAt: String!
}

type Query {
    syncMessages(topic: String): [Message!]!
}

type Mutation {
    sendMessage(id: String!, topic: String!, content: String!, sender: String!, sentAt: String!): Message!
    statusUpdate(messageId: String!, topic: String!, status: MessageStatus!, deliveredAt: String!, readAt: String!): MessageStatusUpdates!
}

type Subscription {
    messageAdded(topic: String!): Message!
    messageUpdates(topic: String!): MessageStatusUpdates!
}
```

### Example Query (Fetching Chat Messages)
```graphql
query GetMessages($topic: String!) {
  syncMessages(topic: $topic) {
    id
    content
    sender
    receiver
    status
    sentAt
    deliveredAt
    readAt
  }
}
```

## Contributing

1. Fork the repository.
2. Create a new branch (`feature/new-feature` or `bugfix/fix-issue`).
3. Commit your changes with meaningful messages.
4. Push to your branch and create a **Pull Request**.

## License

This project is licensed under the **MIT License**. See the `LICENSE` file for details.

---

### Contact
For any queries, feel free to reach out:
- **Email:** anubhav-auth@gmail.com
- **GitHub:** [your-username](https://github.com/anubhav-auth)

Happy Coding! 🚀
