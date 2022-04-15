package GameStudio;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// This is a Singleton class, as we only need
// 1 window instance running at once.
public class Window {
    private final int width,height;
    private long glfwWindow; // Returned by glfw, when window is created
    private final String title;
    private static Window window = null;
    public float r,g,b,a;

    private static Scene currentScene;
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
    }

    public static void changeScene(int newScene){
        switch(newScene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "Unknown Scene '" + newScene + "'";
                break;
        }
    }

    public static Window get(){
        if(Window.window == null)
            Window.window = new Window();

        return Window.window;
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();
        loop();

        // Free memory, since Java might not clear memory like C does.
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free Error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

    }

    public void init() {
        //  Error callback - in-case any errors show, GLFW will print here
        GLFWErrorCallback.createPrint(System.err).set();

        //  Initialize GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        //  Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED,GLFW_TRUE);

        //  Create window --> returns <long int> address of pointer to window
        glfwWindow = glfwCreateWindow(this.width,this.height,this.title,NULL,NULL);
        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW Window");
        }

        // ------ MOUSE CALLBACKS ------

        // Attach mousePosCallback -- any mouse MOVEMENT, call this
        glfwSetCursorPosCallback(glfwWindow,MouseListener::mousePosCallback);

        // Attach mouseButtonCallback -- any mouse CLICK, call this
        glfwSetMouseButtonCallback(glfwWindow,MouseListener::mouseButtonCallback);

        // Attach mouseScrollCallback -- any mouse SCROLL, call this
        glfwSetScrollCallback(glfwWindow,MouseListener::mouseScrollCallback);

        // ------ KEY CALLBACKS ------
        glfwSetKeyCallback(glfwWindow,KeyListener::keyCallback);

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //Enable V-Sync
        glfwSwapInterval(1);

        // Make the window visible now
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;
        while(!glfwWindowShouldClose(glfwWindow))
        {
            // Poll events
            glfwPollEvents();

            glClearColor(r,g,b,a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                currentScene.update(dt);
            }
            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = Time.getTime();
        }

    }
}
