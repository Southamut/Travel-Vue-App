##✈️ Travel Content Platform (Vue.js & Spring Boot)A **responsive website** for travelers to **search and discover travel topics** using **keywords and tags**. Users can **register and log in** to **create and share their own travel content**.

###Deployed WebsiteYou can check the project at: [https://travel-vue-app.vercel.app/](https://travel-vue-app.vercel.app/)

---

## 🏗️ Architecture Overview

### System Architecture


```
Frontend (Vue)  ◄──► Backend (Spring Boot)  ◄──►   Database

``` 
## 📚 Libraries & Packages

### Backend

| Package | Role |
|---------|------|
| `spring-boot-starter-web` | Core dependencies for building RESTful APIs. |
| `spring-boot-starter-data-jpa` | Provides Java Persistence API (JPA) implementation. |
| `postgresql` | JDBC Driver for connecting to the SQL Database. |
| `spring-boot-starter-validation` | For validating DTOs (e.g., using `@NotNull`). |

### Frontend

| Package | Role |
|---------|------|
| `vue` | The progressive JavaScript framework for the user interface. |
| `vue-router` | Official router for single-page applications (SPAs). |
| `pinia` | The intuitive and light-weight state management library. |
| `tailwindcss` | Utility-first CSS framework for styling. |
| `DaisyUI` | Layout, reusable UI components (including toasts). |
| `lucide-vue-next` | Icon library. |
| `axios` | Promise-based HTTP client for API calls. |

---

##📁 Project Structure```
travel-app-project/
├── backend/
│   └── src/main/java
│       ├── .../config/                     # CORS, Security, Database connection
│       ├── .../controller/                 # REST API endpoints (e.g., UserController)
│       ├── .../dto/                        # Request/Response/Validation models
│       ├── .../entity/                     # JPA Entities (Database Models)
│       ├── .../exception/                  # Custom exceptions and handlers
│       ├── .../repository/                 # Data access layer (DAO)
│       ├── .../service/                    # Business logic implementation
│       └── TravelAppApplication.java       # Spring Boot entry point
└── frontend/
    ├── src/
    │   ├── views/                          # Vue Pages mapped to routes (e.g., Home.vue, Login.vue)
    │   ├── components/                     # Reusable UI components (e.g., NavBar.vue)
    │   ├── router/                         # Vue Router configuration
    │   ├── stores/                         # Pinia state modules
    │   └── App.vue                         # Main application component
    └── package.json                        # Frontend dependencies

```