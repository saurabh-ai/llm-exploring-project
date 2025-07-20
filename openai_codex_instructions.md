# OpenAI Codex Instructions for Java Mastery

## System Prompt Configuration
Add this system prompt when using Codex for Java development:

```
You are an expert Java mentor and code assistant helping a student progress through a structured 4-phase Java mastery program. The student is currently in [Phase X] focusing on [current learning topics]. 

Generate high-quality, educational Java code that:
1. Follows Oracle Java coding conventions
2. Includes comprehensive documentation and learning comments
3. Demonstrates best practices appropriate for the current learning phase
4. Provides multiple implementation approaches when beneficial for learning
5. Includes proper error handling and input validation
6. Generates corresponding unit tests with educational value

Always explain the reasoning behind design decisions and highlight key learning concepts in the code.
```

## Context Prompts for Each Interaction

### Basic Code Generation:
```
Context: Java Mastery Learning Project - Phase [X]
Current Focus: [specific learning objectives]
Project: [current project name]

Task: [your specific request]

Requirements:
- Follow Java best practices for Phase [X] level
- Include educational comments explaining key concepts
- Demonstrate [specific Java concepts you're learning]
- Generate with proper exception handling
- Include corresponding unit tests

Learning Objectives:
- [objective 1]
- [objective 2]
- [objective 3]
```

## Phase-Specific Prompting

### Phase 1 (Fundamentals) - Months 1-3
```
Phase 1 Context:
- Learning: Java syntax, OOP principles, basic I/O, exception handling
- Complexity Level: Beginner - simple classes and methods
- Focus Areas: Encapsulation, inheritance, polymorphism, file operations
- Avoid: Advanced libraries, complex patterns, multithreading

Generate code that clearly demonstrates basic OOP concepts with detailed explanatory comments.
```

### Phase 2 (Intermediate) - Months 4-7
```
Phase 2 Context:
- Learning: Collections, generics, lambda expressions, streams, multithreading
- Complexity Level: Intermediate - multi-class applications
- Focus Areas: Functional programming, concurrent programming, advanced data structures
- Include: Generic implementations, lambda expressions, thread-safe operations

Generate code that showcases modern Java features with performance considerations.
```

### Phase 3 (Advanced) - Months 8-12
```
Phase 3 Context:
- Learning: Design patterns, JDBC, Spring Framework, RESTful APIs
- Complexity Level: Advanced - enterprise-style applications
- Focus Areas: Architectural patterns, database integration, web services
- Include: Layered architecture, dependency injection, API design

Generate production-quality code following enterprise development patterns.
```

### Phase 4 (Expert) - Months 13-16
```
Phase 4 Context:
- Learning: Microservices, performance optimization, security, DevOps
- Complexity Level: Expert - scalable, secure, high-performance systems
- Focus Areas: System architecture, optimization, security best practices
- Include: Performance monitoring, security implementations, containerization

Generate enterprise-grade code with advanced optimizations and security considerations.
```

## Detailed Prompting Templates

### 1. Class Creation Template:
```
Create a Java class [ClassName] that demonstrates [specific concept].

Learning Context:
- Phase: [1-4]
- Concepts to demonstrate: [list concepts]
- Complexity level: [beginner/intermediate/advanced/expert]

Requirements:
- Include complete Javadoc documentation
- Add learning-focused inline comments
- Implement proper encapsulation
- Include input validation and error handling
- Follow [specific design pattern if applicable]
- Generate corresponding unit tests

Code Structure:
- Package: com.javamastery.[project-name]
- Class name: [ClassName]
- Key methods: [list expected methods]
- Exception handling: [specific scenarios]

Educational Goals:
- Explain [concept 1] through implementation
- Demonstrate [concept 2] best practices
- Show proper [concept 3] usage
```

### 2. Project Generation Template:
```
Generate a complete Java project for [project description].

Project Context:
- Learning Phase: [X]
- Educational Focus: [main learning objectives]
- Project Complexity: [simple/moderate/complex/enterprise]

Technical Requirements:
- Java version: 17+
- Build tool: Maven
- Testing framework: JUnit 5
- Dependencies: [list specific dependencies for phase]
- Architecture: [specific pattern if applicable]

File Structure Needed:
- Main application classes
- Supporting utility classes
- Configuration files
- Test classes with comprehensive coverage
- README with learning notes
- Maven POM with appropriate dependencies

Learning Integration:
- Code comments explaining key concepts
- Multiple implementation approaches where educational
- Performance considerations (Phase 3-4)
- Security considerations (Phase 4)
- Testing best practices demonstration
```

### 3. Code Review and Improvement Template:
```
Review and improve the following Java code for a Phase [X] learning project:

[Insert existing code here]

Review Criteria:
- Code quality and Java best practices
- Educational value and learning opportunities
- Appropriate complexity for Phase [X]
- Error handling and edge cases
- Testing coverage and quality
- Documentation completeness

Please provide:
1. Improved version of the code
2. Explanation of changes made
3. Learning concepts highlighted
4. Additional features or methods that would enhance learning
5. Corresponding unit tests
6. Alternative implementation approaches
```

### 4. Testing Generation Template:
```
Generate comprehensive unit tests for the following Java class:

[Insert class code here]

Testing Requirements:
- Framework: JUnit 5
- Coverage target: 80%+
- Test types: Unit tests, edge cases, error conditions
- Learning focus: Demonstrate testing best practices for Phase [X]

Include:
- Positive test cases (valid inputs)
- Negative test cases (invalid inputs, edge cases)
- Exception testing
- Boundary value testing
- Mock objects where appropriate (Phase 3-4)
- Performance tests (Phase 4)

Educational Elements:
- Comments explaining testing strategies
- Examples of good test data
- Demonstration of testing patterns
- Best practices for test organization
```

## Advanced Prompting Techniques

### 1. Multi-Approach Generation:
```
Generate 3 different implementations of [functionality] suitable for different learning phases:

Approach 1 (Beginner - Phase 1):
- Simple, straightforward implementation
- Focus on clarity and basic concepts
- Minimal dependencies

Approach 2 (Intermediate - Phase 2-3):
- More sophisticated design
- Use of design patterns
- Better performance considerations

Approach 3 (Advanced - Phase 4):
- Enterprise-grade implementation
- Performance optimized
- Production-ready with monitoring and security

For each approach, explain:
- When to use this approach
- Key concepts demonstrated
- Trade-offs and considerations
```

### 2. Refactoring Guidance:
```
Refactor the following code to demonstrate progression from Phase [X] to Phase [Y]:

[Insert original code]

Refactoring Goals:
- Apply [specific design patterns]
- Improve [specific aspects: performance/maintainability/testability]
- Add [specific features or concepts]
- Maintain educational value

Show the evolution:
1. Original code analysis
2. Step-by-step refactoring process
3. Final improved version
4. Explanation of improvements
5. Learning objectives achieved
```

### 3. Architecture Design:
```
Design the architecture for [application description] as a Phase [X] learning project.

Architecture Requirements:
- Appropriate complexity for Phase [X]
- Demonstrate [specific architectural concepts]
- Include [specific technologies/frameworks]
- Support [specific functionality]

Provide:
1. High-level architecture diagram (textual description)
2. Package structure
3. Class hierarchy and relationships
4. Key interfaces and abstractions
5. Data flow and interaction patterns
6. Technology stack justification
7. Implementation roadmap for learning
```

## Code Quality Specifications

### Documentation Standards:
```
For all generated code, include:

Class-level documentation:
/**
 * [Class description and purpose]
 * 
 * Learning Objectives:
 * - [objective 1]
 * - [objective 2]
 * 
 * Key Concepts Demonstrated:
 * - [concept 1]
 * - [concept 2]
 * 
 * Design Patterns Used:
 * - [pattern 1] for [reason]
 * - [pattern 2] for [reason]
 * 
 * @author Java Mastery Student
 * @version 1.0
 * @since Phase [X]
 */

Method-level documentation:
/**
 * [Method description]
 * 
 * Learning Focus: [specific concept this method demonstrates]
 * 
 * @param paramName [parameter description and constraints]
 * @return [return value description]
 * @throws ExceptionType [when and why this exception is thrown]
 */
```

### Error Handling Requirements:
```
Include robust error handling:
- Input validation with meaningful error messages
- Specific exception types (not generic Exception)
- Logging statements for debugging
- Recovery mechanisms where appropriate
- Error documentation in Javadoc
```

### Testing Requirements:
```
Generate tests that:
- Cover all public methods
- Test edge cases and boundary conditions
- Include both positive and negative test cases
- Demonstrate testing best practices
- Use descriptive test method names
- Follow Given-When-Then structure
- Include performance tests for Phase 3-4 projects
```

## Integration with Learning Progress

### Progress Tracking Prompts:
```
Based on the code generated, update my learning progress:

Current Phase: [X]
Project: [project name]
Code Generated: [brief description]

Please provide:
1. Learning objectives achieved through this code
2. Java concepts successfully demonstrated
3. Areas for further improvement or study
4. Suggested next steps or enhancements
5. Related topics to explore
6. Assessment of readiness for next phase concepts
```

### Learning Assessment:
```
Evaluate this code for Phase [X] learning completeness:

[Insert code here]

Assessment Criteria:
- Appropriate use of Phase [X] concepts
- Code quality and best practices
- Educational value and clarity
- Complexity appropriate for learning level
- Areas demonstrating mastery
- Areas needing improvement

Provide: