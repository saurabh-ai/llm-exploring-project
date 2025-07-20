# Claude Code Instructions

## Project Context
This is a Java mastery learning repository with a structured 4-phase curriculum spanning 12-16 months. I'm progressing through hands-on projects to build expertise from fundamentals to enterprise-level development.

## Current Learning Phase
**Phase:** [Update this - Phase 1/2/3/4]  
**Current Project:** [Update with current project]  
**Learning Focus:** [Update with current topics]

## Code Generation Guidelines

### 1. Java Code Standards
- Use **Java 17+** features when appropriate
- Follow **Oracle Java coding conventions**
- Include comprehensive **Javadoc comments**
- Implement **proper exception handling**
- Use **meaningful variable and method names**
- Apply **SOLID principles** where applicable

### 2. Project Structure
Always generate code following this structure:
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── javamastery/
│   │           └── [project-name]/
│   └── resources/
└── test/
    └── java/
        └── com/
            └── javamastery/
                └── [project-name]/
```

### 3. Build Configuration
- Generate **Maven** `pom.xml` with appropriate dependencies
- Include **JUnit 5** for testing
- Add **common dependencies** based on project phase:
  - Phase 1-2: Core Java, JUnit
  - Phase 3: Spring Boot, JDBC drivers
  - Phase 4: Spring Security, Docker support

### 4. Code Quality Requirements
- **Unit tests** for all public methods
- **Integration tests** for database/API operations
- **Input validation** and error handling
- **Clean code practices** (small methods, single responsibility)
- **Design patterns** implementation where appropriate

### 5. Documentation Standards
For each generated file, include:
- **Class-level Javadoc** explaining purpose
- **Method-level Javadoc** with parameters and return values
- **TODO comments** for areas needing expansion
- **Learning notes** section explaining key concepts

### 6. Incremental Complexity
- Start with **basic implementation** then suggest enhancements
- Provide **multiple approaches** (beginner vs advanced)
- Include **refactoring suggestions** for learning
- Show **evolution path** to more sophisticated solutions

## Phase-Specific Instructions

### Phase 1 (Fundamentals)
- Focus on **core Java syntax** and **OOP principles**
- Use **basic data structures** (arrays, ArrayList)
- Implement **simple file I/O** operations
- Emphasize **proper class design** and **encapsulation**

### Phase 2 (Intermediate)
- Utilize **Collections framework** extensively
- Implement **lambda expressions** and **streams**
- Add **multithreading** where appropriate
- Use **generics** for type safety

### Phase 3 (Advanced)
- Apply **design patterns** (Factory, Observer, Strategy)
- Integrate **database operations** with JDBC
- Build **RESTful APIs** with Spring Boot
- Implement **proper layered architecture**

### Phase 4 (Expert)
- Create **microservices** architecture
- Add **Spring Security** for authentication
- Implement **containerization** with Docker
- Include **performance optimization** techniques

## Command Examples

### Project Creation
```bash
# Create new project structure
claude-code create java-project [project-name] --phase [1-4]

# Generate Maven configuration
claude-code generate pom.xml --dependencies [spring-boot,junit,jdbc]
```

### Code Generation
```bash
# Generate class with tests
claude-code generate class [ClassName] --with-tests --phase [current-phase]

# Create complete CRUD operations
claude-code generate crud [EntityName] --database [mysql,h2] --spring-boot

# Generate design pattern implementation
claude-code generate pattern [singleton,factory,observer] --with-example
```

### Code Review & Improvement
```bash
# Review existing code for best practices
claude-code review [file-path] --phase [current-phase] --suggest-improvements

# Refactor for design patterns
claude-code refactor [file-path] --apply-pattern [pattern-name]

# Generate tests for existing code
claude-code generate tests [file-path] --coverage-target 80
```

## Learning Integration

### After Each Code Generation:
1. **Explain the concepts** demonstrated in the code
2. **Highlight learning objectives** achieved
3. **Suggest next steps** for enhancement
4. **Provide alternative implementations** for comparison
5. **Include relevant resources** for deeper understanding

### Code Comments Format:
```java
/**
 * [Description of class/method]
 * 
 * Learning Objectives:
 * - [Objective 1]
 * - [Objective 2]
 * 
 * Key Concepts Demonstrated:
 * - [Concept 1]
 * - [Concept 2]
 * 
 * @author Java Mastery Student
 */
```

## Error Handling & Debugging

### Always Include:
- **Comprehensive try-catch blocks**
- **Meaningful error messages**
- **Logging statements** for debugging
- **Input validation** methods
- **Recovery strategies** where possible

### Debugging Support:
- Add **debug print statements** during development
- Include **assertion statements** for verification
- Generate **test data** for edge cases
- Provide **debugging tips** in comments

## Performance Considerations

### For Phase 3-4 Projects:
- Use **efficient algorithms** and data structures
- Implement **connection pooling** for databases
- Add **caching** mechanisms where appropriate
- Include **performance measurement** code
- Suggest **optimization opportunities**

## Integration Instructions

### Always Generate:
1. **Main application class** with proper entry point
2. **Configuration files** (application.properties, etc.)
3. **README.md** for the specific project
4. **Sample data** or test fixtures
5. **Build and run instructions**

### Directory-Specific Actions:
- `src/main/java/`: Production code with full documentation
- `src/test/java/`: Comprehensive test suites
- `src/main/resources/`: Configuration and static files
- `docs/`: Project documentation and learning notes

## Continuous Learning Support

### After Each Session:
- **Update progress** in main README
- **Document lessons learned** in project README
- **Suggest next topics** to explore
- **Provide additional resources** for study
- **Highlight industry best practices** demonstrated

This instruction file ensures Claude Code generates high-quality, educational Java code that aligns with your learning journey and industry standards.