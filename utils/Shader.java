/**
 * Trieda Shader spravuje OpenGL shader program.
 * Zodpovedá za vytvorenie, kompiláciu a linkovanie vertex a fragment shaderov,
 * aktiváciu shader programu a nastavovanie uniformných premenných.
 * Obsahuje preddefinovaný jednoduchý vertex a fragment shader pre vykresľovanie
 * farebných objektov s transformáciami (model, view, projection).
 *
 * @author Simon Kyselica
 */
package utils;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class Shader {
    private int programId;

    /**
     * Konštruktor pre triedu Shader.
     * Vytvorí a inicializuje shader program volaním statickej metódy {@link #createShaderProgram()}.
     * Výsledné ID shader programu je uložené pre ďalšie použitie.
     */
    public Shader() {
        this.programId = createShaderProgram();
    }

    /**
     * Aktivuje tento shader program pre nasledujúce vykresľovacie operácie.
     * Všetky nasledujúce príkazy na vykresľovanie budú používať tento shader program.
     */
    public void use() {
        GL33.glUseProgram(this.programId);
    }

    /**
     * Nastaví uniformnú premennú typu matica 4x4 (mat4) v shader programe.
     *
     * @param name   Názov uniformnej premennej v shader kóde.
     * @param matrix {@link Matrix4f} matica, ktorá sa má nastaviť.
     */
    public void setMat4(String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            matrix.get(fb);
            int uniformLocation = GL33.glGetUniformLocation(this.programId, name);
            GL33.glUniformMatrix4fv(uniformLocation, false, fb);
        }
    }

    /**
     * Nastaví uniformnú premennú typu vektor s tromi zložkami (vec3) v shader programe.
     *
     * @param name Názov uniformnej premennej v shader kóde.
     * @param x    Hodnota prvej zložky (X).
     * @param y    Hodnota druhej zložky (Y).
     * @param z    Hodnota tretej zložky (Z).
     */
    public void setVec3(String name, float x, float y, float z) {
        int uniformLocation = GL33.glGetUniformLocation(this.programId, name);
        GL33.glUniform3f(uniformLocation, x, y, z);
    }

    /**
     * Nastaví uniformnú premennú typu float v shader programe.
     *
     * @param name  Názov uniformnej premennej v shader kóde.
     * @param value Hodnota typu float, ktorá sa má nastaviť.
     */
    public void setFloat(String name, float value) {
        int uniformLocation = GL33.glGetUniformLocation(this.programId, name);
        GL33.glUniform1f(uniformLocation, value);
    }

    /**
     * Vytvorí, skompiluje a zlinkuje shader program z preddefinovaných
     * vertex a fragment shader zdrojových kódov.
     * Vertex shader spracováva pozície vrcholov a transformácie.
     * Fragment shader určuje farbu fragmentov.
     * V prípade chyby pri kompilácii alebo linkovaní vypíše chybovú správu.
     *
     * @return ID novovytvoreného a zlinkovaného shader programu.
     */
    public static int createShaderProgram() {
        String vertexShaderSource =
                "#version 330 core\n" +
                        "layout (location = 0) in vec3 aPos;\n" +
                        "layout (location = 1) in vec3 aColor;\n" +
                        "out vec3 ourColor;\n" +
                        "uniform mat4 model;\n"+
                        "uniform mat4 view;\n" +
                        "uniform mat4 projection;\n" +
                        "void main() {\n" +
                        "   gl_Position = projection * view * model * vec4(aPos, 1.0);\n" +
                        "   ourColor = aColor;\n" +
                        "}\n";

        String fragmentShaderSource =
                "#version 330 core\n" +
                        "in vec3 ourColor;\n" +
                        "out vec4 FragColor;\n" +
                        "void main() {\n" +
                        "   FragColor = vec4(ourColor, 1.0);\n" +
                        "}\n";

        int vertexShader = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
        GL33.glShaderSource(vertexShader, vertexShaderSource);
        GL33.glCompileShader(vertexShader);

        int success = GL33.glGetShaderi(vertexShader, GL33.GL_COMPILE_STATUS);
        if (success == GL33.GL_FALSE) {
            String infoLog = GL33.glGetShaderInfoLog(vertexShader);
            System.err.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED\n" + infoLog);
        }

        int fragmentShader = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);
        GL33.glShaderSource(fragmentShader, fragmentShaderSource);
        GL33.glCompileShader(fragmentShader);


        int shaderProgram = GL33.glCreateProgram();
        GL33.glAttachShader(shaderProgram, vertexShader);
        GL33.glAttachShader(shaderProgram, fragmentShader);
        GL33.glLinkProgram(shaderProgram);

        success = GL33.glGetProgrami(shaderProgram, GL33.GL_LINK_STATUS);
        if (success == GL33.GL_FALSE) {
            String infoLog = GL33.glGetProgramInfoLog(shaderProgram);
            System.err.println("ERROR::SHADER::PROGRAM::LINKING_FAILED\n" + infoLog);
        }

        GL33.glDeleteShader(vertexShader);
        GL33.glDeleteShader(fragmentShader);

        return shaderProgram;
    }

    /**
     * Uvoľní zdroje alokované týmto shader programom.
     * Odstráni shader program z OpenGL kontextu.
     * aby sa predišlo únikom pamäte na strane GPU.
     */
    public void cleanup() {
        GL33.glDeleteProgram(this.programId);
    }
}
