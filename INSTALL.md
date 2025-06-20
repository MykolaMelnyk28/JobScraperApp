# Installation Guide

This guide will help you set up and run the Job Scraper Application locally.

---

## Prerequisites

- **Java 21** (OpenJDK or Oracle JDK)
- **Maven** (for building the project)
- **Docker** (optional, for running PostgreSQL in a container)
- **Chrome Browser** (required by Selenium ChromeDriver)
- **Git** (to clone the repository)

---

## Steps

### 1. Clone the repository

```bash
git clone https://github.com/MykolaMelnyk28/JobScraperApp.git
cd JobScraperApp
```

### 2. Create a `.env` file in the project root:

This file is used by the Spring Boot application.

```dotenv
SPRING_DATASOURCE_USERNAME=<Database username>
SPRING_DATASOURCE_PASSWORD=<Databadse password>
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/jobs_db
```

### 3. Create a postgres.env file in the project root:

This file is used by the PostgreSQL Docker container (if using Docker)

```dotenv
POSTGRES_DB=jobs_db
POSTGRES_USER=<Database username>
POSTGRES_PASSWORD=<Databadse password>
```

Make sure both files are placed in the root of your project and referenced in your docker-compose.yml.

### 4. Run with Docker

Before starting, make sure you have created the following files:

- `<project root>/.env`
- `<project root>/postgres.env`

Also, ensure that the SQL dump file exists at ./db/init.sql. This file will be used to initialize the database when the PostgreSQL container is started for the first time.

```commandline
docker-compose up -d job-scrapper-app
```

### 5. Open home page

URL: http://localhost:8080.

### 6. First-time usage

At first launch, the database is empty.

To populate it with job listings:

Open the main page of the application.

Look for the first section labeled Synchronization.

Select a desired filter.

Fill in the page and size fields — these control which and how many job elements to fetch.

Click the Synchronize jobs button.

This will start the scraping process from https://jobs.techstars.com/jobs.
Depending on the number of elements specified in size, this operation may take some time. Please be patient.

Once synchronization is complete, the page will refresh automatically and the jobs will appear as cards.