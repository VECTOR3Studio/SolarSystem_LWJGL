/**
 * Trieda Window reprezentuje hlavné okno aplikácie.
 * Zodpovedá za inicializáciu GLFW a OpenGL, načítanie zdrojov,
 * vykresľovanie scény, spracovanie vstupov od používateľa a
 * správu životného cyklu aplikácie.
 * Vytvára a zobrazuje simuláciu slnečnej sústavy.
 *
 * @author Simon Kyselica
 */
package core;

import entities.CelestialBody;
import entities.Moon;
import entities.Planet;
import entities.Sphere;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import rendering.Renderer;
import utils.Shader;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private long window;
    private int width;
    private int height;
    private Shader shader;
    private Camera camera;
    private Renderer renderer;
    private Matrix4f projectionMatrix;

    private float lastMouseX;
    private float lastMouseY;
    private boolean firstMouse = true;

    private long lastFrameTime;
    private float deltaTime;

    private List<CelestialBody> objects;
    private List<Planet> planets;

    /**
     * Konštruktor pre triedu Window.
     * Inicializuje šírku a výšku okna a zoznamy pre vesmírne telesá.
     *
     * @param width Šírka okna v pixeloch.
     * @param height Výška okna v pixeloch.
     */
    public Window(int width, int height) {
        this.width = width;
        this.height = height;
        this.objects = new ArrayList<>();
        this.planets = new ArrayList<>();
    }

    /**
     * Spustí hlavnú slučku aplikácie.
     * Táto metóda inicializuje okno, spravuje hernú slučku (loop)
     * a po jej ukončení vykoná potrebné čistenie zdrojov.
     */
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        this.init();
        this.loop();

        glfwFreeCallbacks(this.window);
        this.cleanup();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        this.window = glfwCreateWindow(this.width, this.height, "Solar System", NULL, NULL);
        if (this.window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(this.window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(this.window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    this.window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(this.window);

        glfwSwapInterval(1);

        glfwShowWindow(this.window);
        GL.createCapabilities();

        GL33.glEnable(GL33.GL_DEPTH_TEST);

        this.camera = new Camera(new Vector3f(0, 5, 10f));

        this.renderer = new Renderer();
        this.renderer.init((float) this.width / this.height);


        Sphere sun = new Sphere(
                new Vector3f(0, 0, 0),
                2.0f,
                new Vector3f(1.0f, 0.9f, 0.2f),
                60.0f
        );
        this.objects.add(sun);

        Planet mercury = new Planet(
                new Vector3f(0, 0, 0),
                0.045f,
                0.715f,
                4.82f,
                293f,
                new Vector3f(0.6f, 0.6f, 0.6f),
                sun
        );
        this.objects.add(mercury);

        Planet venus = new Planet(
                new Vector3f(0, 0, 0),
                0.112f,
                1.320f,
                12.30f,
                -1215.0f,
                new Vector3f(0.9f, 0.8f, 0.6f),
                sun
        );
        this.objects.add(venus);

        Planet earth = new Planet(
                new Vector3f(0, 0, 0),
                0.117f,
                1.833f,
                20.0f,
                5.0f,
                new Vector3f(0.2f, 0.5f, 0.8f),
                sun
        );
        this.objects.add(earth);

        Moon earthMoon = new Moon(
                0.03f,
                0.3f,
                2.0f,
                27.3f,
                new Vector3f(0.7f, 0.7f, 0.7f),
                earth
        );
        this.objects.add(earthMoon);

        Planet mars = new Planet(
                new Vector3f(0, 0, 0),
                0.062f,
                2.787f,
                37.62f,
                5.13f,
                new Vector3f(0.8f, 0.3f, 0.1f),
                sun
        );
        this.objects.add(mars);

        Moon phobos = new Moon(
                0.01f,
                0.15f,
                0.8f,
                0.318f,
                new Vector3f(0.5f, 0.45f, 0.4f),
                mars
        );
        this.objects.add(phobos);

        Moon deimos = new Moon(
                0.008f,
                0.25f,
                1.5f,
                1.262f,
                new Vector3f(0.55f, 0.5f, 0.45f),
                mars
        );
        this.objects.add(deimos);

        Planet jupiter = new Planet(
                new Vector3f(0, 0, 0),
                1.282f,
                9.533f,
                237.27f,
                2.07f,
                new Vector3f(0.8f, 0.7f, 0.5f),
                sun
        );
        this.objects.add(jupiter);

        Planet saturn = new Planet(
                new Vector3f(0, 0, 0),
                1.067f,
                17.563f,
                589.13f,
                2.22f,
                new Vector3f(0.9f, 0.85f, 0.6f),
                sun
        );
        this.objects.add(saturn);

        Planet uranus = new Planet(
                new Vector3f(0, 0, 0),
                0.465f,
                35.237f,
                1680.64f,
                -3.59f,
                new Vector3f(0.6f, 0.8f, 0.9f),
                sun
        );
        this.objects.add(uranus);

        Planet neptune = new Planet(
                new Vector3f(0, 0, 0),
                0.452f,
                55.092f,
                3295.54f,
                3.36f,
                new Vector3f(0.3f, 0.4f, 0.9f),
                sun
        );
        this.objects.add(neptune);

        this.lastFrameTime = System.currentTimeMillis();

        GLFW.glfwSetCursorPosCallback(this.window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if (Window.this.firstMouse) {
                    Window.this.lastMouseX = (float) xpos;
                    Window.this.lastMouseY = (float) ypos;
                    Window.this.firstMouse = false;
                }
                float offsetX = (float) xpos - Window.this.lastMouseX;
                float offsetY = Window.this.lastMouseY - (float) ypos;
                Window.this.lastMouseX = (float) xpos;
                Window.this.lastMouseY = (float) ypos;

                Window.this.camera.processMouse(offsetX, offsetY);
            }
        });

        GLFW.glfwSetInputMode(this.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    private void loop() {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        while (!glfwWindowShouldClose(this.window)) {
            long currentTime = System.currentTimeMillis();
            this.deltaTime = (currentTime - this.lastFrameTime) / 1000.0f;
            this.lastFrameTime = currentTime;
            this.camera.processKeyboard(this.window);

            for (CelestialBody body : this.objects) {
                body.update(this.deltaTime);
            }

            this.renderer.beginRender();
            for (CelestialBody body : this.objects) {
                this.renderer.render(body, this.camera.getViewMatrix());
            }


            glfwSwapBuffers(this.window);
            glfwPollEvents();
        }
    }

    /**
     * Uvoľní všetky alokované zdroje.
     * Táto metóda by sa mala volať pred ukončením aplikácie,
     * aby sa predišlo únikom pamäte a zdrojov.
     * Uvoľňuje zdroje vesmírnych telies, renderera, GLFW okna a ukončuje GLFW.
     */
    public void cleanup() {
        for (CelestialBody body : this.objects) {
            body.cleanup();
        }

        this.renderer.cleanup();
        glfwDestroyWindow(this.window);
        glfwTerminate();
    }
}
