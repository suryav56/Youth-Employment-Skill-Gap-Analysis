# Skill Gap Portal for Youth Employment & Career Development

An AI-powered, modern SaaS platform designed to help young job seekers and professionals analyze technical skill gaps, undergo targeted MCQ evaluations, retrieve structured learning path roadmaps (powered by the Groq Llama-3 API), and track recommended course progress.

---

## 🛠️ Technology Stack

* **Frontend**: Next.js (TypeScript, React, Tailwind CSS, Lucide Icons)
* **Backend**: Java 21 + Spring Boot 3.3.3 + Spring Security + JPA / Hibernate
* **Database**: MySQL Server 8.0+
* **AI Integration**: Groq API (Chat Completions using `llama3-8b-8192`)
* **Build Tools**: Apache Maven 3.9.9 (local distribution included in workspace)

---

## ⚙️ Environment Configurations

Create a `.env` file at your environment config level or specify environment variables during runtime:

```bash
# Database Configuration
DB_USERNAME=root
DB_PASSWORD=root
DB_URL=jdbc:mysql://localhost:3306/skill_gap_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# Groq AI Key
# Note: If no API key is provided, the backend falls back to high-quality local roadmap generation
GROQ_API_KEY=your_actual_groq_api_key_here

# Frontend API Url
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Never hardcode secret credentials in source code.

---

## 🚀 Running the Project Locally

### 1. Database Setup
Create the MySQL database from your command line or database admin client:
```sql
CREATE DATABASE IF NOT EXISTS skill_gap_portal;
```

### 2. Run the Spring Boot Backend
From the workspace root, run the compilation and boot server using the embedded Maven directory:
```powershell
# Compile and run the backend server
./maven/bin/mvn.cmd spring-boot:run -f backend/pom.xml
```
* The backend will spin up on **`http://localhost:8080`**.
* A startup `DataInitializer` bean automatically seeds core skills, MCQs, and courses if the tables are empty.

### 3. Run the Next.js Frontend
Navigate to the `frontend/` directory, install dependencies (if not already done), and run the dev server:
```bash
cd frontend
npm run dev
```
* The frontend will spin up on **`http://localhost:3000`**.

---

## 🌍 Production Deployment Guidelines

### Frontend (Vercel)
1. Import the `frontend` subfolder to Vercel.
2. Configure the Build Command: `npm run build`.
3. Set the Environment Variables:
   * `NEXT_PUBLIC_API_URL`: Your deployed Spring Boot backend URL (e.g. `https://api.yourdomain.com`).

### Backend (Railway / Render / AWS)
1. Link your backend code directory. Set the root directory to `backend/` or specify custom builder commands.
2. Build commands on Railway: Maven wrapper builds automatically.
3. Configure the Environment Variables:
   * `SPRING_DATASOURCE_URL`: Your production MySQL JDBC url.
   * `SPRING_DATASOURCE_USERNAME`: Production DB user.
   * `SPRING_DATASOURCE_PASSWORD`: Production DB password.
   * `GROQ_API_KEY`: Your production Groq API Key.
   * `JWT_SECRET`: Generate a custom 256-bit secure hex key.

### CORS Configuration
Our backend `SecurityConfig.java` is preconfigured to accept credential-sharing requests from all domains (`allowedOriginPatterns("*")`), which simplifies preview deployments and Vercel branch builds.

---

## 💎 Key Feature Modules

1. **Dashboard Home**: Custom gauges displaying profile completion, tracked skills count, assessment score history, and career skill fit.
2. **Profile Manager**: Set your target career goal (e.g. Frontend Developer) which adjusts the quiz and roadmap parameters.
3. **Skills Manager**: Add and delete skills, adjust experience levels, and mark skills as "learning" or "acquired".
4. **Quiz Assessment**: Interactive 10-question MCQ quiz matching your target path. Answering correctly automatically updates your profile skills to "acquired/intermediate"!
5. **AI Gap Audit**: Compares your skills against role requirements in real time and utilizes Llama-3 to generate a weekly study path.
6. **Tracked Courses**: Recommends Udemy, Coursera, and freeCodeCamp courses matching your weak spots. Move courses between "To Do", "In Progress", and "Completed".
