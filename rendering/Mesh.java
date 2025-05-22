/**
 * Trieda Mesh reprezentuje 3D geometrický model (sieť) pozostávajúci z vrcholov a indexov.
 * Zodpovedá za vytvorenie a správu OpenGL Vertex Array Object (VAO) a Vertex Buffer Objects (VBOs)
 * pre ukladanie dát vrcholov (pozície, farby, atď.) a indexov, ktoré definujú trojuholníky meshu.
 * Tiež nastavuje vertex attribute pointers pre shadery.
 *
 * @author Simon Kyselica
 */
package rendering;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    private int vaoId;       // ID Vertex Array Object
    private int vertexVboId; // ID Vertex Buffer Object pre dáta vrcholov
    private int indexVboId;  // ID Vertex Buffer Object pre indexy
    private int vertexCount; // Počet indexov, ktoré sa majú vykresliť

    /**
     * Konštruktor pre triedu Mesh.
     * Vytvára a inicializuje OpenGL buffery (VAO, VBOs) s poskytnutými dátami vrcholov a indexov.
     * Nastavuje vertex attribute pointers pre pozíciu (layout 0) a farbu (layout 1) vrcholov.
     * Predpokladá, že pole {@code vertices} obsahuje dáta v tvare [x, y, z, r, g, b, x, y, z, r, g, b, ...].
     *
     * @param vertices Pole float hodnôt reprezentujúce dáta vrcholov (napr. pozícia, farba).
     *                 Každý vrchol má 6 hodnôt: 3 pre pozíciu (x,y,z) a 3 pre farbu (r,g,b).
     * @param indices  Pole int hodnôt reprezentujúce indexy, ktoré definujú trojuholníky meshu.
     */
    public Mesh(float[] vertices, int[] indices) {
        this.vertexCount = indices.length; // Počet indexov určuje, koľko vrcholov sa vykreslí

        // Vytvorenie a bindovanie Vertex Array Object (VAO)
        this.vaoId = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(this.vaoId);

        // Vytvorenie Vertex Buffer Object (VBO) pre dáta vrcholov
        this.vertexVboId = GL33.glGenBuffers();
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip(); // Naplnenie buffera a jeho "prevrátenie" pre čítanie
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, this.vertexVboId);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexBuffer, GL33.GL_STATIC_DRAW); // Nahranie dát do VBO
        MemoryUtil.memFree(vertexBuffer); // Uvoľnenie natívnej pamäte

        // Vytvorenie Vertex Buffer Object (VBO) pre indexy (Element Buffer Object)
        this.indexVboId = GL33.glGenBuffers();
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, this.indexVboId);
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL33.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexBuffer);

        // Nastavenie vertex attribute pointer pre pozíciu vrcholu (layout = 0)
        // 3 komponenty typu float, nie sú normalizované, krok (stride) je 6 floatov, offset je 0
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL33.glEnableVertexAttribArray(0); // Povolenie atribútu

        // Nastavenie vertex attribute pointer pre farbu vrcholu (layout = 1)
        // 3 komponenty typu float, nie sú normalizované, krok (stride) je 6 floatov, offset sú 3 floaty
        GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL33.glEnableVertexAttribArray(1); // Povolenie atribútu

        // Unbindovanie VAO, aby sa predišlo náhodným zmenám
        GL33.glBindVertexArray(0);
    }

    /**
     * Vráti ID Vertex Array Object (VAO) tohto meshu.
     * VAO obsahuje konfiguráciu vertex bufferov a attribute pointerov.
     *
     * @return ID VAO.
     */
    public int getVaoId() {
        return this.vaoId;
    }

    /**
     * Vráti počet indexov v tomto meshi.
     * Tento počet sa používa pri vykresľovaní na určenie, koľko vrcholov sa má spracovať.
     *
     * @return Počet indexov.
     */
    public int getVertexCount() {
        return this.vertexCount;
    }

    /**
     * Uvoľní OpenGL zdroje alokované týmto meshom.
     * Odstráni vertex buffer objects (VBOs) pre vrcholy a indexy,
     * a vertex array object (VAO).
     * Táto metóda by sa mala volať, keď mesh už nie je potrebný,
     * aby sa predišlo únikom pamäte na strane GPU.
     */
    public void cleanup() {
        GL33.glDeleteBuffers(this.vertexVboId);
        GL33.glDeleteBuffers(this.indexVboId);
        GL33.glDeleteVertexArrays(this.vaoId);
    }
}
