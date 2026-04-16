Perfecto. Vamos a desglosar los requisitos para que sean completamente atómicos (un solo comportamiento verificable por requisito) y redactados en inglés. Esto es ideal para usarlos como *prompts* en Android Studio, ya que los modelos de IA suelen procesar las instrucciones técnicas con mayor precisión en inglés.

Aquí tienes la Especificación de Requisitos (Software Requirements Specification - SRS) adaptada a tu V1.0:

### **Functional Requirements (FR)**

**1. Level Selection Screen**
* **FR-01.01:** The system shall display a "Level Selection Screen" as the initial screen upon application launch.
* **FR-01.02:** The Level Selection Screen shall display a distinct UI element (e.g., button or card) for each of the following CEFR levels: A1, A2, B1, B2, C1, C2.
* **FR-01.03:** The system shall capture the tap event on any level UI element.
* **FR-01.04:** Upon capturing a tap event on a level, the system shall navigate to the "Category Selection Screen".
* **FR-01.05:** The system shall pass the selected level identifier (e.g., "C1") as an argument to the Category Selection Screen.

**2. Category Selection Screen**
* **FR-02.01:** The Category Selection Screen shall display the currently selected level in the top application bar.
* **FR-02.02:** The Category Selection Screen shall display a selectable UI element labeled "Use of English".
* **FR-02.03:** The Category Selection Screen shall visually disable or hide any exercise categories other than "Use of English" for this version.
* **FR-02.04:** Upon capturing a tap event on the "Use of English" element, the system shall navigate to the "Exercise List Screen".
* **FR-02.05:** The system shall pass both the selected level identifier and the category identifier as arguments to the Exercise List Screen.

**3. Exercise List Screen**
* **FR-03.01:** The Exercise List Screen shall retrieve the list of exercises from the local database matching the passed level and category arguments.
* **FR-03.02:** The Exercise List Screen shall display a vertically scrollable list of the retrieved exercises.
* **FR-03.03:** Each item in the scrollable list shall display the exercise title or identifier (e.g., "Test 1 - Part 1").
* **FR-03.04:** The system shall render a visual indicator (e.g., a green checkmark icon) on the list item if the exercise's state is evaluated as "Resolved".
* **FR-03.05:** The system shall render a default visual state (no checkmark) on the list item if the exercise's state is evaluated as "Unresolved".

**4. Data Ingestion and Persistence**
* **FR-04.01:** Upon the first launch of the application, the system shall read a predefined JSON file located in the local `assets` folder.
* **FR-04.02:** The system shall parse the JSON file into predefined Kotlin Data Classes representing the exercises.
* **FR-04.03:** The system shall insert the parsed exercise data into the local SQLite database.
* **FR-04.04:** The system shall query the local database to retrieve the "Resolved" / "Unresolved" status of each exercise.
* **FR-04.05:** The system shall provide a mechanism to update the database record of a specific exercise, changing its status from "Unresolved" to "Resolved".

---

### **Non-Functional Requirements (NFR)**

**1. Technology Stack & Frameworks**
* **NFR-01.01:** The application shall be developed natively for the Android operating system.
* **NFR-01.02:** The application source code shall be written exclusively in the Kotlin programming language.
* **NFR-01.03:** The User Interface (UI) shall be built entirely using the Jetpack Compose declarative framework. No XML layout files shall be used.

**2. Architecture & Design Patterns**
* **NFR-02.01:** The application architecture shall strictly follow the Model-View-ViewModel (MVVM) design pattern.
* **NFR-02.02:** UI state shall be managed and exposed by the ViewModels using Kotlin `StateFlow`.
* **NFR-02.03:** The application shall follow Material Design 3 (Material You) guidelines for spacing, typography, and component structure.

**3. Data Storage & Performance**
* **NFR-03.01:** Local data persistence shall be implemented using the Android Room persistence library.
* **NFR-03.02:** The application shall operate 100% offline without requiring external network calls.
* **NFR-03.03:** All disk I/O operations (reading JSON, querying/updating the Room database) must be executed on a background thread.
* **NFR-03.04:** Background thread execution shall be managed using Kotlin Coroutines (`Dispatchers.IO`).
* **NFR-03.05:** The Main Thread (UI Thread) shall remain unblocked during data loading, ensuring smooth scroll performance (60 FPS) on the Exercise List Screen.