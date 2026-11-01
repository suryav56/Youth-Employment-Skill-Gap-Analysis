package com.skillgapportal.backend.config;

import com.skillgapportal.backend.entity.Course;
import com.skillgapportal.backend.entity.Question;
import com.skillgapportal.backend.entity.Skill;
import com.skillgapportal.backend.repository.CourseRepository;
import com.skillgapportal.backend.repository.QuestionRepository;
import com.skillgapportal.backend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SkillRepository skillRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeSkills();
        initializeCourses();
        initializeQuestions();
    }

    private void initializeSkills() {
        if (skillRepository.count() > 0) {
            return;
        }

        List<Skill> skills = List.of(
            // Frontend
            Skill.builder().name("HTML").category("Frontend").build(),
            Skill.builder().name("CSS").category("Frontend").build(),
            Skill.builder().name("JavaScript").category("Frontend").build(),
            Skill.builder().name("React").category("Frontend").build(),
            Skill.builder().name("TypeScript").category("Frontend").build(),
            
            // Backend
            Skill.builder().name("Java").category("Backend").build(),
            Skill.builder().name("Spring Boot").category("Backend").build(),
            Skill.builder().name("MySQL").category("Backend").build(),
            Skill.builder().name("Git").category("Backend").build(),
            Skill.builder().name("REST APIs").category("Backend").build(),
            
            // Data Science
            Skill.builder().name("Python").category("Data Science").build(),
            Skill.builder().name("SQL").category("Data Science").build(),
            Skill.builder().name("Machine Learning").category("Data Science").build(),
            Skill.builder().name("Data Analysis").category("Data Science").build(),
            Skill.builder().name("Statistics").category("Data Science").build(),
            
            // Management / Soft Skills
            Skill.builder().name("Agile").category("Management").build(),
            Skill.builder().name("Scrum").category("Management").build(),
            Skill.builder().name("Communication").category("Management").build(),
            Skill.builder().name("Risk Management").category("Management").build(),
            Skill.builder().name("Jira").category("Management").build()
        );

        skillRepository.saveAll(skills);
        System.out.println("Initialized core skills catalog.");
    }

    private void initializeCourses() {
        if (courseRepository.count() > 0) {
            return;
        }

        List<Course> courses = List.of(
            Course.builder()
                .title("Modern React with Redux [2026]")
                .provider("Udemy")
                .url("https://www.udemy.com/course/react-redux/")
                .category("Frontend")
                .difficulty("INTERMEDIATE")
                .build(),
            Course.builder()
                .title("Responsive Web Design Certification")
                .provider("freeCodeCamp")
                .url("https://www.freecodecamp.org/learn/2022/responsive-web-design/")
                .category("Frontend")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("TypeScript Mastery")
                .provider("Coursera")
                .url("https://www.coursera.org/learn/typescript")
                .category("Frontend")
                .difficulty("INTERMEDIATE")
                .build(),
            Course.builder()
                .title("The Complete JavaScript Course 2026: From Zero to Expert")
                .provider("Udemy")
                .url("https://www.udemy.com/course/the-complete-javascript-course/")
                .category("Frontend")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("CSS - The Complete Guide 2026 (including Flexbox, Grid & Sass)")
                .provider("Udemy")
                .url("https://www.udemy.com/course/css-the-complete-guide-to-css-flexbox-grid-sass/")
                .category("Frontend")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("Spring Boot 3 & Spring Framework 6")
                .provider("Udemy")
                .url("https://www.udemy.com/course/spring-springboot-jpa-hibernate/")
                .category("Backend")
                .difficulty("INTERMEDIATE")
                .build(),
            Course.builder()
                .title("Java Programming Masterclass")
                .provider("Coursera")
                .url("https://www.coursera.org/specializations/java-programming")
                .category("Backend")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("The Ultimate MySQL Bootcamp")
                .provider("Udemy")
                .url("https://www.udemy.com/course/the-ultimate-mysql-bootcamp/")
                .category("Backend")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("Git & GitHub Complete Guide")
                .provider("Udemy")
                .url("https://www.udemy.com/course/git-complete/")
                .category("Backend")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("Rest API Design & Development with Spring Boot")
                .provider("Udemy")
                .url("https://www.udemy.com/course/rest-api-design-spring-boot/")
                .category("Backend")
                .difficulty("INTERMEDIATE")
                .build(),
            Course.builder()
                .title("Python for Data Science and Machine Learning")
                .provider("Udemy")
                .url("https://www.udemy.com/course/python-for-data-science-and-machine-learning-bootcamp/")
                .category("Data Science")
                .difficulty("INTERMEDIATE")
                .build(),
            Course.builder()
                .title("SQL for Data Science")
                .provider("Coursera")
                .url("https://www.coursera.org/learn/sql-for-data-science")
                .category("Data Science")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("Machine Learning A-Z: AI, Python & R")
                .provider("Udemy")
                .url("https://www.udemy.com/course/machinelearning/")
                .category("Data Science")
                .difficulty("INTERMEDIATE")
                .build(),
            Course.builder()
                .title("Agile Crash Course: Agile Project Management")
                .provider("Udemy")
                .url("https://www.udemy.com/course/agile-crash-course/")
                .category("Management")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("Scrum Alliance CSM Prep Course")
                .provider("Coursera")
                .url("https://www.coursera.org/learn/scrum-master-certification")
                .category("Management")
                .difficulty("INTERMEDIATE")
                .build(),
            Course.builder()
                .title("Jira Project Management Masterclass")
                .provider("Udemy")
                .url("https://www.udemy.com/course/jira-masterclass/")
                .category("Management")
                .difficulty("BEGINNER")
                .build(),
            Course.builder()
                .title("Communication Skills for Tech Professionals")
                .provider("Udemy")
                .url("https://www.udemy.com/course/communication-skills-tech/")
                .category("Management")
                .difficulty("BEGINNER")
                .build()
        );

        courseRepository.saveAll(courses);
        System.out.println("Initialized learning courses catalog.");
    }

    private void initializeQuestions() {
        if (questionRepository.count() > 0) {
            return;
        }

        // Fetch skills to map questions properly
        List<Skill> skills = skillRepository.findAll();
        Map<String, Skill> skillMap = new HashMap<>();
        for (Skill s : skills) {
            skillMap.put(s.getName().toLowerCase(), s);
        }

        List<Question> questions = List.of(
            // --- FRONTEND QUESTIONS ---
            // React
            Question.builder()
                .skill(skillMap.get("react"))
                .category("Frontend")
                .questionText("What is the purpose of React's useState hook?")
                .optionA("To manage application routing and path history")
                .optionB("To render static CSS animations in components")
                .optionC("To allow functional components to maintain and update local state")
                .optionD("To query external REST API resources")
                .correctOption("C")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("react"))
                .category("Frontend")
                .questionText("Which hook should be used to memoize the result of a resource-intensive calculation in React?")
                .optionA("useEffect")
                .optionB("useMemo")
                .optionC("useCallback")
                .optionD("useRef")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),
            Question.builder()
                .skill(skillMap.get("react"))
                .category("Frontend")
                .questionText("What problem does Concurrent Mode / Fiber architecture solve in React?")
                .optionA("It speeds up the server-side compilation of assets")
                .optionB("It enables React to pause, resume, and prioritize rendering work for smoother UI responsiveness")
                .optionC("It replaces CSS stylesheets with component-level styling dynamically")
                .optionD("It ensures direct sync with real-time web socket connections natively")
                .correctOption("B")
                .difficulty("ADVANCED")
                .build(),

            // HTML
            Question.builder()
                .skill(skillMap.get("html"))
                .category("Frontend")
                .questionText("Which HTML5 element is used to represent introductory content or navigational links?")
                .optionA("<header>")
                .optionB("<nav>")
                .optionC("<section>")
                .optionD("<intro>")
                .correctOption("A")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("html"))
                .category("Frontend")
                .questionText("Which semantic tag represents content that is tangentially related to the content around it, like sidebars?")
                .optionA("<aside>")
                .optionB("<section>")
                .optionC("<article>")
                .optionD("<details>")
                .correctOption("A")
                .difficulty("INTERMEDIATE")
                .build(),

            // CSS
            Question.builder()
                .skill(skillMap.get("css"))
                .category("Frontend")
                .questionText("What is the default value of the position property in CSS?")
                .optionA("relative")
                .optionB("static")
                .optionC("absolute")
                .optionD("fixed")
                .correctOption("B")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("css"))
                .category("Frontend")
                .questionText("In CSS Flexbox, which property controls alignment along the main axis?")
                .optionA("align-items")
                .optionB("align-content")
                .optionC("justify-content")
                .optionD("flex-direction")
                .correctOption("C")
                .difficulty("INTERMEDIATE")
                .build(),

            // JavaScript
            Question.builder()
                .skill(skillMap.get("javascript"))
                .category("Frontend")
                .questionText("Which keyword allows declaring block-scoped variables that can be reassigned?")
                .optionA("var")
                .optionB("const")
                .optionC("let")
                .optionD("def")
                .correctOption("C")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("javascript"))
                .category("Frontend")
                .questionText("What does a JavaScript Promise status evaluate to if it completes successfully?")
                .optionA("fulfilled")
                .optionB("resolved")
                .optionC("accepted")
                .optionD("done")
                .correctOption("A")
                .difficulty("INTERMEDIATE")
                .build(),

            // TypeScript
            Question.builder()
                .skill(skillMap.get("typescript"))
                .category("Frontend")
                .questionText("What is the main benefit of using TypeScript over vanilla JavaScript?")
                .optionA("TypeScript compiles directly to machine code for faster loads")
                .optionB("TypeScript provides compile-time static type checking")
                .optionC("TypeScript eliminates the need for CSS styling files")
                .optionD("TypeScript runs natively inside all modern web browsers")
                .correctOption("B")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("typescript"))
                .category("Frontend")
                .questionText("In TypeScript, how do you define a type that is a union of multiple possible types?")
                .optionA("Using the & operator")
                .optionB("Using the | operator")
                .optionC("Using the || operator")
                .optionD("Using the extends keyword")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),
            Question.builder()
                .skill(skillMap.get("typescript"))
                .category("Frontend")
                .questionText("What is the difference between 'unknown' and 'any' types in TypeScript?")
                .optionA("unknown is type-safe because it doesn't allow calling arbitrary operations on the value without checking/casting first, whereas any disables all checks")
                .optionB("any is type-safe, while unknown bypasses type checking completely")
                .optionC("unknown is only for primitive types, while any is for complex objects")
                .optionD("There is no technical difference between any and unknown")
                .correctOption("A")
                .difficulty("ADVANCED")
                .build(),


            // --- BACKEND QUESTIONS ---
            // Java
            Question.builder()
                .skill(skillMap.get("java"))
                .category("Backend")
                .questionText("Which of the following is NOT a primitive data type in Java?")
                .optionA("int")
                .optionB("boolean")
                .optionC("String")
                .optionD("double")
                .correctOption("C")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("java"))
                .category("Backend")
                .questionText("What does Java's garbage collector reclaim when it runs?")
                .optionA("Unreachable objects in heap memory")
                .optionB("Variables declared on the call stack")
                .optionC("Classes loaded into method area")
                .optionD("Thread pools that are currently blocked")
                .correctOption("A")
                .difficulty("INTERMEDIATE")
                .build(),

            // Spring Boot
            Question.builder()
                .skill(skillMap.get("spring boot"))
                .category("Backend")
                .questionText("In Spring Boot, which annotation combined with @Controller enables REST support, writing response bodies directly?")
                .optionA("@RestController")
                .optionB("@ResponseBody")
                .optionC("@Component")
                .optionD("@Endpoint")
                .correctOption("A")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("spring boot"))
                .category("Backend")
                .questionText("Which annotation is used to inject dependencies automatically in Spring Framework?")
                .optionA("@Inject")
                .optionB("@Autowired")
                .optionC("@Dependency")
                .optionD("@Resource")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),

            // MySQL
            Question.builder()
                .skill(skillMap.get("mysql"))
                .category("Backend")
                .questionText("Which SQL statement is used to remove all records from a table without dropping the table itself?")
                .optionA("DELETE TABLE")
                .optionB("DROP TABLE")
                .optionC("TRUNCATE TABLE")
                .optionD("REMOVE TABLE")
                .correctOption("C")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("mysql"))
                .category("Backend")
                .questionText("What type of JOIN returns all records from the left table and matched records from the right table?")
                .optionA("INNER JOIN")
                .optionB("FULL JOIN")
                .optionC("LEFT JOIN")
                .optionD("RIGHT JOIN")
                .correctOption("C")
                .difficulty("INTERMEDIATE")
                .build(),

            // Git
            Question.builder()
                .skill(skillMap.get("git"))
                .category("Backend")
                .questionText("Which Git command records staged snapshot changes into local history?")
                .optionA("git push")
                .optionB("git add")
                .optionC("git commit")
                .optionD("git save")
                .correctOption("C")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("git"))
                .category("Backend")
                .questionText("Which command lets you move modifications from a branch and apply them directly on top of another branch?")
                .optionA("git merge")
                .optionB("git rebase")
                .optionC("git push")
                .optionD("git checkout")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),

            // REST APIs
            Question.builder()
                .skill(skillMap.get("rest apis"))
                .category("Backend")
                .questionText("Which HTTP method is most appropriate for updating an existing resource fully in a RESTful API?")
                .optionA("GET")
                .optionB("POST")
                .optionC("PATCH")
                .optionD("PUT")
                .correctOption("D")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("rest apis"))
                .category("Backend")
                .questionText("What HTTP status code is returned for a successful request that results in creation of a new resource?")
                .optionA("200 OK")
                .optionB("201 Created")
                .optionC("202 Accepted")
                .optionD("204 No Content")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),


            // --- DATA SCIENCE QUESTIONS ---
            // Python
            Question.builder()
                .skill(skillMap.get("python"))
                .category("Data Science")
                .questionText("Which Python library is primarily used for multi-dimensional array operations and scientific computation?")
                .optionA("Pandas")
                .optionB("NumPy")
                .optionC("Matplotlib")
                .optionD("Flask")
                .correctOption("B")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("python"))
                .category("Data Science")
                .questionText("In Pandas, which data structure is used to represent a 2D labeled tabular format?")
                .optionA("Series")
                .optionB("DataFrame")
                .optionC("List")
                .optionD("Array")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),

            // SQL
            Question.builder()
                .skill(skillMap.get("sql"))
                .category("Data Science")
                .questionText("Which SQL clause is used to filter groups generated by aggregate functions like SUM or COUNT?")
                .optionA("WHERE")
                .optionB("HAVING")
                .optionC("GROUP BY")
                .optionD("FILTER")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),

            // Machine Learning
            Question.builder()
                .skill(skillMap.get("machine learning"))
                .category("Data Science")
                .questionText("What type of learning problem involves predicting a continuous target numerical value (e.g. house prices)?")
                .optionA("Classification")
                .optionB("Clustering")
                .optionC("Regression")
                .optionD("Dimensionality Reduction")
                .correctOption("C")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("machine learning"))
                .category("Data Science")
                .questionText("What is the purpose of cross-validation in Machine Learning?")
                .optionA("To speed up model training time")
                .optionB("To evaluate model generalization performance and detect overfitting")
                .optionC("To perform feature extraction automatically")
                .optionD("To compress the model deployment package")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),

            // Data Analysis
            Question.builder()
                .skill(skillMap.get("data analysis"))
                .category("Data Science")
                .questionText("What is data normalization primarily used for?")
                .optionA("Formatting column names to uppercase")
                .optionB("Scaling numerical values to a common range (e.g. 0 to 1) for objective comparison")
                .optionC("Removing all text data columns from the dataset")
                .optionD("Converting raw records into Excel sheets")
                .correctOption("B")
                .difficulty("BEGINNER")
                .build(),

            // Statistics
            Question.builder()
                .skill(skillMap.get("statistics"))
                .category("Data Science")
                .questionText("What does standard deviation measure in a dataset?")
                .optionA("The middle value when sorted")
                .optionB("The average value of elements")
                .optionC("The spread or variability of values around the mean")
                .optionD("The sum total value of occurrences")
                .correctOption("C")
                .difficulty("INTERMEDIATE")
                .build(),


            // --- MANAGEMENT QUESTIONS ---
            // Agile
            Question.builder()
                .skill(skillMap.get("agile"))
                .category("Management")
                .questionText("What does the Agile Manifesto prioritize over comprehensive documentation?")
                .optionA("Contract negotiation")
                .optionB("Following a fixed plan")
                .optionC("Working software")
                .optionD("Rigid processes and tool chains")
                .correctOption("C")
                .difficulty("BEGINNER")
                .build(),

            // Scrum
            Question.builder()
                .skill(skillMap.get("scrum"))
                .category("Management")
                .questionText("What is the typical timeboxed duration of a standard Scrum Sprint?")
                .optionA("1 to 4 weeks")
                .optionB("2 to 3 months")
                .optionC("1 day")
                .optionD("Flexible, determined by developers daily")
                .correctOption("A")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("scrum"))
                .category("Management")
                .questionText("Who is responsible for maximizing the value of the product resulting from work of the Scrum Team?")
                .optionA("Scrum Master")
                .optionB("Product Owner")
                .optionC("Developers")
                .optionD("Project Manager")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),

            // Communication
            Question.builder()
                .skill(skillMap.get("communication"))
                .category("Management")
                .questionText("What is active listening primarily about in project communication?")
                .optionA("Thinking about your next reply while the other person speaks")
                .optionB("Fully concentrating, understanding, responding, and remembering what is being said")
                .optionC("Nodding constantly without recording details")
                .optionD("Recording all audio calls to write automatic transcripts later")
                .correctOption("B")
                .difficulty("BEGINNER")
                .build(),

            // Risk Management
            Question.builder()
                .skill(skillMap.get("risk management"))
                .category("Management")
                .questionText("In risk management, what does 'mitigation' refer to?")
                .optionA("Ignoring the risk until it occurs")
                .optionB("Reducing the probability and/or impact of an adverse risk event")
                .optionC("Transferring the entire risk cost to insurance agencies")
                .optionD("Declaring the project closed immediately")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build(),

            // Jira
            Question.builder()
                .skill(skillMap.get("jira"))
                .category("Management")
                .questionText("What is a Kanban board primarily used for in Agile software development?")
                .optionA("To host repository source code and review pull requests")
                .optionB("To visualize project work items, track progress, and limit work-in-progress")
                .optionC("To perform automated database performance analysis")
                .optionD("To configure deployment pipelines on AWS or Vercel")
                .correctOption("B")
                .difficulty("BEGINNER")
                .build(),
            Question.builder()
                .skill(skillMap.get("jira"))
                .category("Management")
                .questionText("What type of Jira issue represents a unit of work that can be completed within a sprint?")
                .optionA("Epic")
                .optionB("Story/Task")
                .optionC("Project")
                .optionD("Dashboard")
                .correctOption("B")
                .difficulty("INTERMEDIATE")
                .build()
        );

        questionRepository.saveAll(questions);
        System.out.println("Initialized skills assessment questions.");
    }
}
