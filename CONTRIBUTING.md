# 🤝 Contributing to Citopia

## Team Members

| Name | Alias |
|------|-------|
| Ehsanullah Ehsanullah | Sano |
| Mustafa | — |
| Tayma AlDabbas | — |
| A | — |

---

## Git Workflow

### Branch Naming
```
feature/[issue#]-short-description    → new features
bugfix/[issue#]-short-description     → bug fixes
docs/[issue#]-short-description       → documentation only
```

**Examples:**
```
feature/5-implement-city-class
bugfix/23-fix-pathfinding-crash
docs/11-update-readme
```

### Commit Message Format
```
[type] Short summary (max 72 chars)

Optional longer description explaining what and why.

Closes #issue-number
```

**Types:** `feat`, `fix`, `test`, `docs`, `refactor`, `style`, `ci`

**Examples:**
```
feat: implement City model class with position and population

Add City class with name, x/y coordinates, and population fields.
Includes unit tests for creation and population updates.

Closes #5
```

---

## Development Process

### 1. Pick an Issue
- Go to the project board
- Assign yourself to an issue in **Ready** column
- Move it to **In Progress**

### 2. Create a Branch
```bash
git checkout master
git pull origin master
git checkout -b feature/5-implement-city-class
```

### 3. Write Code + Tests
- Follow Java naming conventions
- Write unit tests for all public methods
- Run tests locally before pushing:
  ```bash
  ./gradlew core:test
  ```

### 4. Push & Open Merge Request
```bash
git push origin feature/5-implement-city-class
```
- Open MR on GitLab using the default template
- Link the related issue (`Closes #5`)
- Assign a reviewer from the team

### 5. Code Review
- Reviewer checks code quality, tests, and documentation
- At least **1 approval** required
- CI pipeline must be **green** (tests pass)

### 6. Merge
- Use **Squash and Merge** for clean history
- Delete the source branch after merge
- Issue auto-closes when MR is merged

---

## Code Standards

### Java Conventions
- Class names: `PascalCase` → `GameState`, `CityFactory`
- Methods/variables: `camelCase` → `getPopulation()`, `routeDistance`
- Constants: `UPPER_SNAKE_CASE` → `MAX_VEHICLES`, `DEFAULT_SPEED`
- Packages: `lowercase` → `com.citopia.model`

### Documentation
- All public classes must have a **class-level JavaDoc** comment
- All public methods must have **JavaDoc** with `@param`, `@return`, `@throws`
- Complex logic should have inline comments explaining *why*

### Testing
- Test class naming: `ClassNameTest.java`
- Test method naming: `testMethodName_scenario_expected()`
- One assertion concept per test method
- Target: **≥80% code coverage**

---

## Project Board Columns

| Column | Meaning |
|--------|---------|
| **Backlog** | All planned issues |
| **Ready** | Refined and ready to work on |
| **In Progress** | Someone is actively working on it |
| **Review** | MR is open, awaiting review |
| **Done** | Merged and closed |

---

## Questions?

Ask in the team chat or open a GitLab issue with the `question` label.
