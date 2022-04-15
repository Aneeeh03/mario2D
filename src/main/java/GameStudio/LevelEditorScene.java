package GameStudio;


import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class    LevelEditorScene extends Scene{
    private String vertexShaderSrc= "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "void main(){\n" +
            "    fColor=aColor;\n" +
            "    gl_Position = vec4(aPos , 1.0) ;\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "out vec4 color;\n" +
            "void main() {\n" +
            "    color=fColor;\n" +
            "}";

    private int vertexID,fragmentID,shaderProgram;

    private float[] vertexArray={
            50.5f,-50.5f,0.0f,        1.0f,0.0f,0.0f,1.0f,   //BR 0
            -50.5f,50.5f,0.0f,        0.0f,1.0f,0.0f,1.0f,   //TL 1
            50.5f,50.5f,0.0f,         0.0f,0.0f,1.0f,1.0f,   //TR 2
            -50.5f,-50.5f,0.0f,       1.0f,1.0f,0.0f,1.0f    //BL 3
    };
    private int[] elementArray = {

            2,1,0, //TR triangle
            0,1,3, //BL triangle
    };


    private int vaoID,vboID,eboID;
    private Shader defaultShader;
    public LevelEditorScene(){

    }

    @Override
    public void init(){
        this.camera=new Camera(new Vector2f());
        defaultShader =new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        vaoID=glGenVertexArrays();
        glBindVertexArray(vaoID);
        FloatBuffer vertexBuffer= BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        vboID=glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID=glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementBuffer,GL_STATIC_DRAW);

        int positionsSize=3;
        int colorSize=4;
        int floatSizeBytes = 4;
        int vertexSizeBytes=(positionsSize+colorSize)* floatSizeBytes;
        glVertexAttribPointer(0,positionsSize,GL_FLOAT,false,vertexSizeBytes,0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1,colorSize,GL_FLOAT,false,vertexSizeBytes,positionsSize*floatSizeBytes);
        glEnableVertexAttribArray(1);



    }

    @Override
    public void update(float dt) {
        camera.position.x-=dt *50.0f;
        defaultShader.use();
        defaultShader.uploadMat4f("uProjection",camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView",camera.getViewMatrix());
        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES,elementArray.length,GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();
    }


}
