# Java Solar System Simulation (LWJGL)

## 1. About The Project

The goal of this semester project is to design and implement a solar system simulation in Java using the Lightweight Java Game Library (LWJGL). The project aims to create a realistic model of celestial bodies, including the sun, planets orbiting in their respective paths, and moons orbiting planets, all adhering to basic physical laws of gravity.

The simulation incorporates essential space mechanics such as orbital path calculations, body rotations, and (planned) surface textures and visual effects. An interactive user interface is designed to allow users to freely navigate between celestial bodies, zooming in and out of objects.

A key part of this work includes a UML diagram detailing the application's structure, main classes, their attributes, methods, and interrelationships. The overall objective is to develop a clear, well-structured, and extensible project that showcases Java programming skills and Object-Oriented Programming (OOP) principles.

## 2. Key Features

*   Interactive 3D visualization of a simplified solar system.
*   Celestial bodies (Sun, Planets, Moons) with individual rotational and orbital mechanics.
*   Hierarchical movement: Moons orbit planets, and planets orbit the Sun.
*   Dynamic camera controls for free exploration (WASD for movement, mouse for orientation).
*   Implementation based on Java and LWJGL 3 (with OpenGL).
*   Demonstration of OOP principles, particularly polymorphism.

## 3. Implementation Details

### 3.1. Polymorphism

Polymorphism is a cornerstone of this solar system simulation, implemented through several key approaches:

*   **`CelestialBody` Interface:** This interface defines a common contract for all celestial bodies. It mandates methods like `update(float deltaTime)` for updating their state and `getModelMatrix()` for retrieving their transformation matrix.
*   **Concrete Implementations:**
    *   `Sphere`: Represents the Sun and serves as a base class for other spherical bodies.
    *   `Planet`: Extends `Sphere`, representing a planet.
    *   `Moon`: Extends `Sphere`, representing a moon.
    Each of these classes provides its own specific implementation of the `update()` method, tailored to the behavior of that type of body (e.g., Sun's rotation, planet's orbital motion, moon's orbital motion).
*   **Dynamic Dispatch:** Polymorphism is evident during state updates and rendering. The main simulation loop iterates through a `List<CelestialBody>` containing all celestial objects. For each object, the `update()` method is called via the `CelestialBody` interface, dynamically invoking the specific implementation for the Sun, a planet, or a moon.
*   **Renderer Interaction:** The `Renderer` class utilizes methods like `getMesh()` and `getModelMatrix()` from the `CelestialBody` interface to draw each body without needing to know its specific concrete type.
*   **Hierarchical Movement:** Polymorphism also facilitates the hierarchical structure of celestial movement. Planets orbit the Sun, and moons orbit planets. Each orbiting body (`Planet`, `Moon`) contains its own logic within its `update()` method to calculate its orbital path around its parent body. Despite these differences, all bodies adhere to the `CelestialBody` interface for state updates and rendering data, allowing for consistent processing within the simulation.

### 3.2. Program Structure and Simulation Mechanics

The simulation logic is organized into the following key classes and interfaces:

*   **`core.Main`**: The entry point for launching the LWJGL application.
*   **`Window`**: The main application class. It initializes the GLFW window, OpenGL context, manages the main program loop (game loop), handles user input (keyboard, mouse), and coordinates scene updates and rendering.
*   **`Camera`**: Manages the user's viewpoint in the 3D scene. It allows for camera movement and rotation in space and generates the view matrix required for rendering.
*   **`Renderer`**: Responsible for rendering objects to the screen using OpenGL. It manages shaders, sets projection and view matrices, and executes draw commands for individual meshes.
*   **`Mesh`**: Represents the geometric data of a 3D object (vertices, indices). It manages Vertex Array Objects (VAOs) and Vertex Buffer Objects (VBOs) in OpenGL.
*   **`CelestialBody` (interface)**: Defines the common contract for all celestial bodies. It prescribes methods such as `update(float deltaTime)`, `getMesh()`, `getPosition()`, and `getModelMatrix()`, which all concrete celestial bodies must implement.
*   **`Sphere`**: A concrete class implementing `CelestialBody`. It represents a spherical body and serves as the base for the Sun, planets, and moons. It implements logic for self-rotation and sphere mesh generation.
*   **`Planet`**: A class extending `Sphere`. It represents a planet that, in addition to its own rotation, performs orbital motion around another `CelestialBody` (the Sun).
*   **`Moon`**: A class extending `Sphere`. It represents a moon that, in addition to its own rotation, performs orbital motion around another `CelestialBody` (a planet).

**Simulation of Movement and Hierarchy:**

*   Each body implementing `CelestialBody` has an `update(float deltaTime)` method, called in every frame.
*   `Planet` and `Moon` classes, in their `update()` methods, calculate their new position in orbit around their parent body (`orbitsAround`) based on orbital radius, period, and elapsed time.
*   All bodies (including `Sphere` for the Sun) also simulate rotation around their own axis.
*   The hierarchy (moons orbit planets, planets orbit the Sun) is achieved by each orbiting body holding a reference to the body it orbits, calculating its position relative to it.

**Interaction and Visualization:**

*   The user can freely move the camera using keyboard keys and change its orientation by moving the mouse.
*   Celestial bodies are visualized as simple colored spheres.
*   The simulation runs in real-time, with smooth body movement thanks to the use of `deltaTime` for time-independent updates.

## 4. User Guide

### 4.1. Running the Simulation

The simulation can be launched directly from your IDE by running the `main` method in the `core.Main` class. Ensure that the LWJGL 3 libraries are correctly configured for the project.

### 4.2. Controls

*   **Camera Movement (Forward/Backward/Left/Right):** `W` / `S` / `A` / `D` keys.
*   **Camera Orientation:** Mouse movement.
*   **Exit Simulation:** `ESC` key.

Explore the simulated solar system, observe the motion of planets around the Sun, and moons around their parent planets.

## 5. Conclusion and Future Work

The development of this solar system simulation focused on mastering the fundamentals of 3D graphics using LWJGL and OpenGL, as well as the practical application of object-oriented programming, particularly the principle of polymorphism. The project successfully implements the basic functionality of a dynamic solar system model with multiple types of celestial bodies and hierarchical movement.

This project provides a solid foundation for future enhancements, such as:

*   Adding textures to celestial bodies.
*   Implementing lighting and shadow effects.
*   Introducing more complex models for celestial bodies.
*   Incorporating additional physical phenomena.
*   Adding more interactive elements.
